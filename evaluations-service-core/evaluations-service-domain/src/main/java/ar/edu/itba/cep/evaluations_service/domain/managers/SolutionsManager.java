package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.events.ExamFinishedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExamScoredEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExamSolutionSubmittedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.DataLoadingHelper;
import ar.edu.itba.cep.evaluations_service.domain.helpers.StateVerificationHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import ar.edu.itba.cep.evaluations_service.security.authentication.AuthenticationHelper;
import ar.edu.itba.cep.evaluations_service.services.SolutionService;
import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.errors.UniqueViolationError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult.Result.APPROVED;
import static java.util.function.Predicate.not;


/**
 * Manager for {@link ExamSolutionSubmission}s and {@link ExerciseSolution}s.
 */
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class SolutionsManager implements SolutionService {

    private final ExamRepository examRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExamSolutionSubmissionRepository submissionRepository;
    private final ExerciseSolutionRepository solutionRepository;
    private final ExerciseSolutionResultRepository resultsRepository;
    private final ApplicationEventPublisher publisher;


    // ================================================================================================================
    // Exams Solution Submissions
    // ================================================================================================================

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public Page<ExamSolutionSubmission> getSolutionSubmissionsForExam(long examId, final PagingRequest pagingRequest)
            throws NoSuchEntityException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        return submissionRepository.getByExam(exam, pagingRequest);
    }

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (" +
                    "   hasAuthority('TEACHER')" +
                    "       and @examSolutionSubmissionAuthorizationProvider.isExamOwner(#submissionId, principal)" +
                    ")" +
                    " or (" +
                    "   hasAuthority('STUDENT')" +
                    "       and @examSolutionSubmissionAuthorizationProvider.isOwner(#submissionId, principal)" +
                    ")"
    )
    public Optional<ExamSolutionSubmission> getSubmission(final long submissionId) {
        return submissionRepository.findById(submissionId);
    }

    @Override
    @Transactional
    @PreAuthorize("isFullyAuthenticated() and hasAuthority('STUDENT')")
    public ExamSolutionSubmission createExamSolutionSubmission(long examId)
            throws NoSuchEntityException, UniqueViolationException, IllegalStateException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        final var submitter = AuthenticationHelper.currentUserUsername();

        // First check that the exam is in progress in order to create solutions for exercises owned by it.
        performExamInProgressStateVerification(exam);

        // Then check if there is a submission for the exam by the current user
        if (submissionRepository.existsSubmissionFor(exam, submitter)) {
            throw new UniqueViolationException(List.of(SUBMISSION_ALREADY_EXISTS));
        }

        // Create the submission
        final var submission = submissionRepository.save(new ExamSolutionSubmission(exam, submitter));
        // And create a solution for each exercise belonging to the exam, setting the created submission
        exerciseRepository.getExamExercises(exam)
                .stream()
                .map(exercise -> new ExerciseSolution(submission, exercise))
                .forEach(solutionRepository::save)
        ;

        // Return the created submission
        return submission;
    }


    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('STUDENT')" +
                    "   and @examSolutionSubmissionAuthorizationProvider.isOwner(#submissionId, principal)"
    )
    public void submitSolutions(long submissionId) throws NoSuchEntityException, IllegalStateException {
        final var submission = DataLoadingHelper.loadExamSolutionSubmission(submissionRepository, submissionId);
        performExamInProgressStateVerification(submission.getExam()); // TODO: Allow if finished?
        doPlaceSubmission(submission);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (" +
                    "   hasAuthority('TEACHER')" +
                    "       and @examSolutionSubmissionAuthorizationProvider.isExamOwner(#submissionId, principal)" +
                    ")"
    )
    public void scoreSubmission(final long submissionId) {
        final var submission = DataLoadingHelper.loadExamSolutionSubmission(submissionRepository, submissionId);
        if (Objects.nonNull(submission.getScore())) {
            return; // Do not calculate it again.
        }
        StateVerificationHelper.checkSubmitted(submission);
        final var totalScore = solutionRepository.getExerciseSolutions(submission)
                .stream()
                .map(this::buildContainer)
                .peek(SolutionAndResultsContainer::verifyPendingExecutions)
                .filter(SolutionAndResultsContainer::isApproved)
                .mapToInt(SolutionAndResultsContainer::getScore)
                .sum();
        submission.score(totalScore);
        publisher.publishEvent(ExamScoredEvent.create(submission));
        submissionRepository.save(submission);
    }


    // ================================================================================================================
    // Exercises Solutions
    // ================================================================================================================

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (" +
                    "   hasAuthority('TEACHER')" +
                    "       and @examSolutionSubmissionAuthorizationProvider.isExamOwner(#submissionId, principal)" +
                    ")" +
                    " or (" +
                    "   hasAuthority('STUDENT')" +
                    "       and @examSolutionSubmissionAuthorizationProvider.isOwner(#submissionId, principal)" +
                    ")"
    )
    public List<ExerciseSolution> getSolutionsForSubmission(final long submissionId) throws NoSuchEntityException {
        final var submission = DataLoadingHelper.loadExamSolutionSubmission(submissionRepository, submissionId);
        return solutionRepository.getExerciseSolutions(submission);
    }

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (" +
                    "   hasAuthority('TEACHER')" +
                    "       and @exerciseSolutionAuthorizationProvider.isExamOwner(#solutionId, principal)" +
                    ")" +
                    " or (" +
                    "   hasAuthority('STUDENT')" +
                    "       and @exerciseSolutionAuthorizationProvider.isOwner(#solutionId, principal)" +
                    ")"
    )
    public Optional<ExerciseSolution> getSolution(final long solutionId) {
        return solutionRepository.findById(solutionId);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('STUDENT')" +
                    "   and @exerciseSolutionAuthorizationProvider.isOwner(#solutionId, principal)"
    )
    public void modifySolution(final long solutionId, final String answer, final String compilerFlags, final String mainFileName)
            throws NoSuchEntityException, IllegalEntityStateException {
        final var solution = DataLoadingHelper.loadSolution(solutionRepository, solutionId);
        performExamInProgressStateVerification(solution.getExercise().getExam());
        performSolutionNotSubmittedVerification(solution.getSubmission());
        solution.setAnswer(answer);
        solution.setCompilerFlags(compilerFlags);
        solution.setMainFileName(mainFileName);
        solutionRepository.save(solution);
    }


    // ================================================================================================================
    // Event Listeners
    // ================================================================================================================

    /**
     * Handles the given {@code event}.
     *
     * @param event The {@link ExamSolutionSubmittedEvent} to be handled.
     * @throws IllegalArgumentException If the {@code event} is {@code null},
     *                                  or if it contains a {@code null} {@link ExamSolutionSubmission}
     */
    @Transactional
    @EventListener(ExamFinishedEvent.class)
    public void examFinished(final ExamFinishedEvent event) throws IllegalArgumentException {
        Assert.notNull(event, "The event must not be null");
        submitNonFinished(event.getExam());
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Performs an {@link Exam} state verification (i.e checks if solutions for the given {@code exam} can be submitted,
     * throwing an {@link IllegalEntityStateError} if its state is not upcoming).
     *
     * @param exam The {@link Exam} to be checked.
     * @throws IllegalEntityStateException If the given {@link Exam}'s state is not {@link Exam.State#UPCOMING}.
     */
    private static void performExamInProgressStateVerification(final Exam exam) throws IllegalEntityStateException {
        Assert.notNull(exam, "The exam to be checked must not be null");
        if (exam.getState() != Exam.State.IN_PROGRESS) {
            throw new IllegalEntityStateException(EXAM_IS_NOT_IN_PROGRESS);
        }
    }

    /**
     * Performs an {@link ExamSolutionSubmission} state verification (i.e checks if already submitted).
     *
     * @param submission The {@link ExamSolutionSubmission} to be checked.
     * @throws IllegalEntityStateException If the given {@link ExamSolutionSubmission}'s state
     *                                     is not {@link ExamSolutionSubmission.State#UNPLACED}.
     */
    private static void performSolutionNotSubmittedVerification(final ExamSolutionSubmission submission)
            throws IllegalEntityStateException {
        Assert.notNull(submission, "The submission to be checked must not be null");
        if (Objects.equals(submission.getState(), ExamSolutionSubmission.State.SUBMITTED)) {
            throw new IllegalEntityStateException(EXAM_SOLUTION_ALREADY_SUBMITTED);
        }
    }

    /**
     * Submits all the {@link ExamSolutionSubmission} belonging to the given {@code exam} that are still unplaced.
     *
     * @param exam The {@link Exam} whose {@link ExamSolutionSubmission} must be placed.
     */
    private void submitNonFinished(final Exam exam) {
        Assert.notNull(exam, "The exam whose submissions must be placed must not be null");
        submissionRepository
                .getByExamAndState(exam, ExamSolutionSubmission.State.UNPLACED)
                .forEach(this::doPlaceSubmission);
    }

    /**
     * Places the given {@code submission}
     * (i.e sets the {@link ExamSolutionSubmission} as submitted and stores the new state).
     *
     * @param submission The {@link ExamSolutionSubmission} to be submitted.
     */
    private void doPlaceSubmission(final ExamSolutionSubmission submission) {
        submission.submit();
        submissionRepository.save(submission);
        publisher.publishEvent(ExamSolutionSubmittedEvent.create(submission));
    }

    /**
     * Builds a {@link SolutionAndResultsContainer} from the given {@code solution},
     * using the {@link #resultsRepository}
     * to retrieve the {@link ExerciseSolutionResult}s of the said {@code solution}.
     *
     * @param solution The {@link ExerciseSolution}.
     * @return The build {@link SolutionAndResultsContainer}.
     */
    private SolutionAndResultsContainer buildContainer(final ExerciseSolution solution) {
        return SolutionAndResultsContainer.build(solution, resultsRepository.find(solution));
    }


    /**
     * An {@link IllegalEntityStateError} that indicates that a certain action that involves an {@link Exam}
     * cannot be performed because the said {@link Exam}'s state is not in progress
     * (it has not started yet or has finished already).
     */
    private final static IllegalEntityStateError EXAM_IS_NOT_IN_PROGRESS =
            new IllegalEntityStateError("The exam is not in progress state", "state");

    /**
     * An {@link IllegalEntityStateError} that indicates that an {@link ExamSolutionSubmission} is already submitted.
     */
    private final static IllegalEntityStateError EXAM_SOLUTION_ALREADY_SUBMITTED =
            new IllegalEntityStateError("The exam solution is already submitted", "state");

    /**
     * An {@link IllegalStateException} that indicates that the {@link ExamSolutionSubmission}
     * owns an {@link ExerciseSolution} with pending executions.
     */
    private final static IllegalEntityStateError PENDING_EXECUTIONS =
            new IllegalEntityStateError("The submission contains pending executions");

    /**
     * An {@link UniqueViolationError} that indicates that an {@link ExamSolutionSubmission} already exists
     * for a given {@link Exam} and {@code submitter}.
     */
    private final static UniqueViolationError SUBMISSION_ALREADY_EXISTS =
            new UniqueViolationError("An Exam Solution Submission already exists", "exam", "submitter");


    /**
     * Wraps an {@link ExerciseSolution} with its {@link ExerciseSolutionResult}s.
     * It contains methods to check if there are {@link ExerciseSolutionResult}s with pending executions,
     * and to check if the {@link ExerciseSolution} is approved, based on the result's mark.
     */
    @ToString(doNotUseGetters = true)
    @EqualsAndHashCode(doNotUseGetters = true)
    @AllArgsConstructor(staticName = "build")
    private static final class SolutionAndResultsContainer {

        /**
         * The {@link ExerciseSolution}.
         */
        private final ExerciseSolution solution;
        /**
         * The {@link ExerciseSolutionResult}s.
         */
        private final List<ExerciseSolutionResult> results;

        /**
         * Verifies if there any of the {@link #results} is pending of execution results.
         */
        private void verifyPendingExecutions() {
            if (results.stream().anyMatch(not(ExerciseSolutionResult::isMarked))) {
                throw new IllegalEntityStateException(PENDING_EXECUTIONS);
            }
        }

        /**
         * Checks if the {@link #solution} is approved.
         *
         * @return {@code true} if approved, or {@code false} otherwise.
         */
        private boolean isApproved() {
            return results.stream().allMatch(res -> res.getResult() == APPROVED);
        }

        /**
         * Returns the awarded score for the {@link #solution} (i.e it returns the solution's exercise awarded score).
         *
         * @return The amount of score it is awarded to the solution.
         */
        private int getScore() {
            return solution.getExercise().getAwardedScore();
        }
    }
}
