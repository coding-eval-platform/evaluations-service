package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.events.ExamSolutionSubmittedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.DataLoadingHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExamSolutionSubmissionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Manager for {@link ExamSolutionSubmission}s and {@link ExerciseSolution}s.
 */
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class SolutionsManager implements SolutionService {

    /**
     * Repository for {@link Exam}s.
     */
    private final ExamRepository examRepository;
    /**
     * Repository for {@link Exercise}s.
     */
    private final ExerciseRepository exerciseRepository;
    /**
     * Repository for {@link ExamSolutionSubmission}s.
     */
    private final ExamSolutionSubmissionRepository examSolutionSubmissionRepository;
    /**
     * Repository for {@link ExerciseSolution}s.
     */
    private final ExerciseSolutionRepository exerciseSolutionRepository;

    /**
     * An {@link ApplicationEventPublisher} to publish relevant events to the rest of the application's components.
     */
    private final ApplicationEventPublisher publisher;


    // ================================================================================================================
    // Exams Solution Submissions
    // ================================================================================================================

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public Page<ExamSolutionSubmission> getSolutionSubmissionsForExam(long examId, PagingRequest pagingRequest)
            throws NoSuchEntityException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        return examSolutionSubmissionRepository.getByExam(exam, pagingRequest);
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
        return examSolutionSubmissionRepository.findById(submissionId);
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
        if (examSolutionSubmissionRepository.existsSubmissionFor(exam, submitter)) {
            throw new UniqueViolationException(List.of(SUBMISSION_ALREADY_EXISTS));
        }

        // Create the submission
        final var submission = examSolutionSubmissionRepository.save(new ExamSolutionSubmission(exam, submitter));
        // And create a solution for each exercise belonging to the exam, setting the created submission
        exerciseRepository.getExamExercises(exam)
                .stream()
                .map(exercise -> new ExerciseSolution(submission, exercise))
                .forEach(exerciseSolutionRepository::save)
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
        final var submission = DataLoadingHelper.loadExamSolutionSubmission(examSolutionSubmissionRepository, submissionId);
        performExamInProgressStateVerification(submission.getExam()); // TODO: Allow if finished?
        submission.submit();
        examSolutionSubmissionRepository.save(submission);
        publisher.publishEvent(ExamSolutionSubmittedEvent.create(submission));
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
        final var submission = DataLoadingHelper.loadExamSolutionSubmission(examSolutionSubmissionRepository, submissionId);
        return exerciseSolutionRepository.getExerciseSolutions(submission);
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
        return exerciseSolutionRepository.findById(solutionId);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('STUDENT')" +
                    "   and @exerciseSolutionAuthorizationProvider.isOwner(#solutionId, principal)"
    )
    public void modifySolution(final long solutionId, final String answer)
            throws NoSuchEntityException, IllegalEntityStateException {
        final var solution = DataLoadingHelper.loadSolution(exerciseSolutionRepository, solutionId);
        performExamInProgressStateVerification(solution.getExercise().getExam());
        performSolutionNotSubmittedVerification(solution.getSubmission());
        solution.setAnswer(answer);
        exerciseSolutionRepository.save(solution);
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
     * An {@link UniqueViolationError} that indicates that an {@link ExamSolutionSubmission} already exists
     * for a given {@link Exam} and {@code submitter}.
     */
    private final static UniqueViolationError SUBMISSION_ALREADY_EXISTS =
            new UniqueViolationError("An Exam Solution Submission already exists", "exam", "submitter");
}
