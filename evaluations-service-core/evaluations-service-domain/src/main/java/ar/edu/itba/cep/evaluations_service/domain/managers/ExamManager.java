package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.helpers.DataLoadingHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.Language;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import ar.edu.itba.cep.evaluations_service.security.authentication.AuthenticationHelper;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import ar.edu.itba.cep.evaluations_service.services.ExamWithOwners;
import ar.edu.itba.cep.evaluations_service.services.ExamWithoutOwners;
import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * Manager for {@link Exam}s, {@link Exercise}s and {@link TestCase}s.
 * It provides the functionality to create, modify, delete and read those entities, allowing also to change their state
 * (e.g start or finish an {@link Exam}).
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ExamManager implements ExamService {

    /**
     * Repository for {@link Exam}s.
     */
    private final ExamRepository examRepository;
    /**
     * Repository for {@link Exercise}s.
     */
    private final ExerciseRepository exerciseRepository;
    /**
     * Repository for {@link TestCase}s.
     */
    private final TestCaseRepository testCaseRepository;


    // ================================================================================================================
    // Exams
    // ================================================================================================================

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<ExamWithoutOwners> listAllExams(final PagingRequest pagingRequest) {
        return examRepository.findAll(pagingRequest).map(ExamWithoutOwners::new);
    }

    @Override
    @PreAuthorize("isFullyAuthenticated() and (hasAuthority('ADMIN') or hasAuthority('TEACHER'))")
    public Page<ExamWithoutOwners> listMyExams(final PagingRequest pagingRequest) {
        return examRepository.getOwnedBy(
                AuthenticationHelper.currentUserUsername(),
                pagingRequest
        ).map(ExamWithoutOwners::new);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public Optional<ExamWithOwners> getExam(final long examId) {
        return examRepository.findById(examId).map(exam -> {
            exam.getOwners().size(); // Initialize Lazy Collection
            return new ExamWithOwners(exam);
        });
    }

    @Override
    @Transactional
    @PreAuthorize("isFullyAuthenticated() and (hasAuthority('ADMIN') or hasAuthority('TEACHER'))")
    public Exam createExam(final String description, final LocalDateTime startingAt, final Duration duration)
            throws IllegalArgumentException {
        final var exam = new Exam(description, startingAt, duration, AuthenticationHelper.currentUserUsername());
        return examRepository.save(exam);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public void modifyExam(final long examId,
                           final String description, final LocalDateTime startingAt, final Duration duration)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        exam.update(description, startingAt, duration); // The Exam verifies state by its own.
        examRepository.save(exam);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public void startExam(final long examId) throws NoSuchEntityException, IllegalEntityStateException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        // First verify that the exam has at least once exercise.
        final var exercises = exerciseRepository.getExamExercises(exam);
        if (exercises.isEmpty()) {
            throw new IllegalEntityStateException(EXAM_DOES_NOT_CONTAIN_EXERCISES);
        }
        // Then, verify that all exercises have at least one test case.
        if (exercises.stream().map(testCaseRepository::getExercisePrivateTestCases).anyMatch(List::isEmpty)) {
            throw new IllegalEntityStateException(EXAM_CONTAIN_EXERCISE_WITHOUT_TEST_CASE);
        }
        // Then, start the exam.
        exam.startExam(); // The Exam verifies state by its own.
        // Finally, save the exam.
        examRepository.save(exam);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public void finishExam(final long examId) throws NoSuchEntityException, IllegalEntityStateException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        exam.finishExam(); // The Exam verifies state by its own.
        examRepository.save(exam);
        // TODO: submit all exam submissions for this exam?
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public void addOwnerToExam(final long examId, final String owner)
            throws NoSuchEntityException, IllegalArgumentException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        exam.addOwner(owner);
        examRepository.save(exam);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public void removeOwnerFromExam(final long examId, final String owner)
            throws NoSuchEntityException, IllegalEntityStateException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        exam.removeOwner(owner);
        examRepository.save(exam);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public void deleteExam(final long examId) throws IllegalEntityStateException {
        examRepository.findById(examId)
                .ifPresent(exam -> {
                    performExamUpcomingStateVerification(exam);
                    testCaseRepository.deleteExamTestCases(exam);
                    exerciseRepository.deleteExamExercises(exam);
                    examRepository.delete(exam);
                });
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))" +
                    " or (hasAuthority('STUDENT') and @examAuthorizationProvider.hasStarted(#examId))"
    )
    public List<Exercise> getExercises(final long examId) throws NoSuchEntityException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        return exerciseRepository.getExamExercises(exam);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public void clearExercises(final long examId) throws NoSuchEntityException, IllegalEntityStateException {
        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        performExamUpcomingStateVerification(exam);
        testCaseRepository.deleteExamTestCases(exam);
        exerciseRepository.deleteExamExercises(exam);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @examAuthorizationProvider.isOwner(#examId, principal))"
    )
    public Exercise createExercise(
            final long examId,
            final String question,
            final Language language,
            final String solutionTemplate,
            final int awardedScore)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {

        final var exam = DataLoadingHelper.loadExam(examRepository, examId);
        performExamUpcomingStateVerification(exam);
        final var exercise = new Exercise(question, language, solutionTemplate, awardedScore, exam);
        return exerciseRepository.save(exercise);
    }

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @exerciseAuthorizationProvider.isOwner(#exerciseId, principal))" +
                    " or (hasAuthority('STUDENT') and @exerciseAuthorizationProvider.examHasStarted(#exerciseId))"
    )
    public Optional<Exercise> getExercise(long exerciseId) {
        return exerciseRepository.findById(exerciseId);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @exerciseAuthorizationProvider.isOwner(#exerciseId, principal))"
    )
    public void modifyExercise(
            final long exerciseId,
            final String question,
            final Language language,
            final String solutionTemplate,
            final int awardedScore)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {

        final var exercise = DataLoadingHelper.loadExercise(exerciseRepository, exerciseId);
        performExamUpcomingStateVerification(exercise.getExam());
        exercise.update(question, language, solutionTemplate, awardedScore);
        exerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @exerciseAuthorizationProvider.isOwner(#exerciseId, principal))"
    )
    public void deleteExercise(final long exerciseId) throws IllegalEntityStateException {
        exerciseRepository.findById(exerciseId)
                .ifPresent(exercise -> {
                    performExamUpcomingStateVerification(exercise.getExam());
                    testCaseRepository.deleteExerciseTestCases(exercise);
                    exerciseRepository.delete(exercise);
                });
    }


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @exerciseAuthorizationProvider.isOwner(#exerciseId, principal))" +
                    " or (hasAuthority('STUDENT') and @exerciseAuthorizationProvider.examHasStarted(#exerciseId))"
    )
    public List<TestCase> getPublicTestCases(final long exerciseId) throws NoSuchEntityException {
        final var exercise = DataLoadingHelper.loadExercise(exerciseRepository, exerciseId);
        return testCaseRepository.getExercisePublicTestCases(exercise);
    }

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @exerciseAuthorizationProvider.isOwner(#exerciseId, principal))"
    )
    public List<TestCase> getPrivateTestCases(final long exerciseId) throws NoSuchEntityException {
        final var exercise = DataLoadingHelper.loadExercise(exerciseRepository, exerciseId);
        return testCaseRepository.getExercisePrivateTestCases(exercise);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @exerciseAuthorizationProvider.isOwner(#exerciseId, principal))"
    )
    public TestCase createTestCase(
            final long exerciseId,
            final TestCase.Visibility visibility,
            final Long timeout,
            final List<String> inputs,
            final List<String> expectedOutputs)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {
        final var exercise = DataLoadingHelper.loadExercise(exerciseRepository, exerciseId);
        performExamUpcomingStateVerification(exercise.getExam());
        return testCaseRepository.save(new TestCase(visibility, timeout, inputs, expectedOutputs, exercise));
    }

    @Override
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @testCaseAuthorizationProvider.isOwner(#testCaseId, principal))" +
                    " or (" +
                    "       hasAuthority('STUDENT')" +
                    "           and @testCaseAuthorizationProvider.examHasStarted(#testCaseId)" +
                    "           and @testCaseAuthorizationProvider.isPublic(#testCaseId)" +
                    ")"
    )
    public Optional<TestCase> getTestCase(long testCaseId) {
        return testCaseRepository.findById(testCaseId).map(testCase -> {
            testCase.getInputs().size(); // Initialize Lazy Collection
            testCase.getExpectedOutputs().size(); // Initialize Lazy Collection
            return testCase;
        });
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @testCaseAuthorizationProvider.isOwner(#testCaseId, principal))"
    )
    public void modifyTestCase(final long testCaseId,
                               final TestCase.Visibility visibility,
                               final Long timeout,
                               final List<String> inputs,
                               final List<String> expectedOutputs)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {
        final var testCase = DataLoadingHelper.loadTestCase(testCaseRepository, testCaseId);
        performExamUpcomingStateVerification(testCase.getExercise().getExam());
        testCase.update(visibility, timeout, inputs, expectedOutputs);
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (hasAuthority('TEACHER') and @testCaseAuthorizationProvider.isOwner(#testCaseId, principal))"
    )
    public void deleteTestCase(final long testCaseId) throws IllegalEntityStateException {
        testCaseRepository.findById(testCaseId)
                .ifPresent(testCase -> {
                    performExamUpcomingStateVerification(testCase.getExercise().getExam());
                    testCaseRepository.delete(testCase);
                });
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Performs an {@link Exam} state verification (i.e checks if the given {@code exam} can be modified,
     * throwing an {@link IllegalEntityStateError} if its state is not upcoming).
     *
     * @param exam The {@link Exam} to be checked.
     * @throws IllegalEntityStateException If the given {@link Exam}'s state is not {@link Exam.State#UPCOMING}.
     */
    private static void performExamUpcomingStateVerification(final Exam exam) throws IllegalEntityStateException {
        Assert.notNull(exam, "The exam to be checked must not be null");
        if (exam.getState() != Exam.State.UPCOMING) {
            throw new IllegalEntityStateException(EXAM_IS_NOT_UPCOMING);
        }
    }

    /**
     * An {@link IllegalEntityStateError} that indicates that a certain action that involves an {@link Exam}
     * cannot be performed because the said {@link Exam}'s state is not upcoming (it has started or finished already).
     */
    private final static IllegalEntityStateError EXAM_IS_NOT_UPCOMING =
            new IllegalEntityStateError("The exam is not in upcoming state", "state");

    /**
     * An {@link IllegalEntityStateError} that indicates that a certain action that involves an {@link Exam}
     * cannot be performed because the said {@link Exam}'s does not contain any {@link Exercise}.
     */
    private final static IllegalEntityStateError EXAM_DOES_NOT_CONTAIN_EXERCISES =
            new IllegalEntityStateError("The exam does not contain any exercise");

    /**
     * An {@link IllegalEntityStateError} that indicates that a certain action that involves an {@link Exam}
     * cannot be performed because the said {@link Exam}'s contains an {@link Exercise} without {@link TestCase}s.
     */
    private final static IllegalEntityStateError EXAM_CONTAIN_EXERCISE_WITHOUT_TEST_CASE =
            new IllegalEntityStateError("The exam contains an exercise without any private test case");
}
