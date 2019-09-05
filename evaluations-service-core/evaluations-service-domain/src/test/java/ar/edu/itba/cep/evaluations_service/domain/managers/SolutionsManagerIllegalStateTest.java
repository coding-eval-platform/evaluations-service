package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExamSolutionSubmissionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static ar.edu.itba.cep.evaluations_service.models.Exam.State.*;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link SolutionsManager}, containing tests for the illegal state situations
 * (i.e how the manager behaves when operating with submissions that are not in a valid state for operating with).
 */
@ExtendWith(MockitoExtension.class)
class SolutionsManagerIllegalStateTest extends AbstractSolutionsManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository                   An {@link ExamRepository}
     *                                         that is injected to the {@link SolutionsManager}.
     * @param exerciseRepository               An {@link ExerciseRepository}
     *                                         that is injected to the {@link SolutionsManager}.
     * @param examSolutionSubmissionRepository An {@link ExamSolutionSubmissionRepository}
     *                                         that is injected to the {@link SolutionsManager}.
     * @param exerciseSolutionRepository       An {@link ExerciseSolutionRepository}
     *                                         that is injected to the {@link SolutionsManager}.
     * @param publisher                        An {@link ApplicationEventPublisher}
     *                                         that is injected to the {@link SolutionsManager}.
     */
    SolutionsManagerIllegalStateTest(
            @Mock(name = "examRepository") final ExamRepository examRepository,
            @Mock(name = "exerciseRepository") final ExerciseRepository exerciseRepository,
            @Mock(name = "submissionRepository") final ExamSolutionSubmissionRepository examSolutionSubmissionRepository,
            @Mock(name = "exerciseSolutionRepository") final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock(name = "eventPublisher") final ApplicationEventPublisher publisher) {
        super(examRepository, exerciseRepository, examSolutionSubmissionRepository, exerciseSolutionRepository, publisher);
    }

    // ================================================================================================================
    // Exam Solution Submission
    // ================================================================================================================

    /**
     * Tests that creating an {@link ExamSolutionSubmission} for an upcoming {@link Exam} is not allowed.
     *
     * @param exam A mocked {@link Exam} (the one for which an exercise is being created).
     */
    @Test
    void testCreateExamSolutionSubmissionForInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "authentication") final Authentication authentication,
            @Mock(name = "securityContext") final SecurityContext securityContext) {
        testCreateExamSolutionSubmission(exam, UPCOMING, TestHelper.validOwner(), authentication, securityContext);
    }

    /**
     * Tests that creating an {@link ExamSolutionSubmission} for a finished {@link Exam} is not allowed.
     *
     * @param exam A mocked {@link Exam} (the one for which an exercise is being created).
     */
    @Test
    void testCreateExamSolutionSubmissionForFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "authentication") final Authentication authentication,
            @Mock(name = "securityContext") final SecurityContext securityContext) {
        testCreateExamSolutionSubmission(exam, FINISHED, TestHelper.validOwner(), authentication, securityContext);
    }

    /**
     * Tests that submitting solutions for an in upcoming {@link Exam} is not allowed.
     *
     * @param submission A mocked {@link ExamSolutionSubmission} (the one being submitted).
     * @param exam       A mocked {@link Exam} (the one to which the submission belongs).
     */
    @Test
    void testSolutionsAreNotSubmittedWhenExamIsUpcoming(
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "exam") final Exam exam) {
        testSubmitSolutionsExamState(exam, UPCOMING, submission);
    }

    /**
     * Tests that submitting solutions for a finished {@link Exam} is not allowed.
     *
     * @param submission A mocked {@link ExamSolutionSubmission} (the one being submitted).
     * @param exam       A mocked {@link Exam} (the one to which the submission belongs).
     */
    @Test
    void testSolutionsAreNotSubmittedWhenExamIsFinished(
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "exam") final Exam exam) {
        testSubmitSolutionsExamState(exam, FINISHED, submission);
    }

    /**
     * Tests that submitting solutions when already submitted is not allowed.
     *
     * @param submission A mocked {@link ExamSolutionSubmission} (the one being submitted).
     * @param exam       A mocked {@link Exam} (the one to which the submission belongs).
     */
    @Test
    void testSolutionsAreNotSubmittedWhenAlreadySubmitted(
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "exam") final Exam exam) {
        final var submissionId = TestHelper.validExerciseId();
        when(exam.getState()).thenReturn(IN_PROGRESS);
        when(submission.getExam()).thenReturn(exam);
        doThrow(IllegalEntityStateException.class).when(submission).submit();
        when(examSolutionSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> solutionsManager.submitSolutions(submissionId),
                "Submitting solutions when already submitted is being allowed"
        );
        verify(exam, only()).getState();
        verify(submission, times(1)).getExam();
        verify(submission, times(1)).submit();
        verifyNoMoreInteractions(submission);
        verifyOnlySubmissionSearch(submissionId);
    }


    // ================================================================================================================
    // Exercises Solutions
    // ================================================================================================================

    /**
     * Tests that modifying a solution for an upcoming {@link Exam} is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the one to which the {@code exercise} belongs).
     * @param exercise A mocked {@link Exercise} (the one to which the {@code solution} belongs to).
     * @param solution A mocked {@link ExerciseSolution} (the one being tried to be modified).
     */
    @Test
    void testSolutionsAreNotModifiedWhenExamIsUpcoming(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "solution") final ExerciseSolution solution) {
        testModifySolutionsExamState(exam, UPCOMING, exercise, solution);
    }

    /**
     * Tests that modifying a solution for a finished {@link Exam} is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the one to which the {@code exercise} belongs).
     * @param exercise A mocked {@link Exercise} (the one to which the {@code solution} belongs to).
     * @param solution A mocked {@link ExerciseSolution} (the one being tried to be modified).
     */
    @Test
    void testSolutionsAreNotModifiedWhenExamIsFinished(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "solution") final ExerciseSolution solution) {
        testModifySolutionsExamState(exam, FINISHED, exercise, solution);
    }

    /**
     * Tests that modifying an already submitted solution is not allowed.
     *
     * @param exam       A mocked {@link Exam} (the one to which the {@code exercise} belongs).
     * @param exercise   A mocked {@link Exercise} (the one to which the {@code solution} belongs to).
     * @param submission A mocked {@link ExamSolutionSubmission} (the one to which the {@code solution} belongs to).
     * @param solution   A mocked {@link ExerciseSolution} (the one being tried to be modified).
     */
    @Test
    void testSolutionsAreNotSubmittedWhenAlreadySubmitted(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "solution") final ExerciseSolution solution) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(solution.getExercise()).thenReturn(exercise);
        when(exercise.getExam()).thenReturn(exam);
        when(exam.getState()).thenReturn(IN_PROGRESS);
        when(solution.getSubmission()).thenReturn(submission);
        when(submission.getState()).thenReturn(ExamSolutionSubmission.State.SUBMITTED);
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> solutionsManager.modifySolution(solutionId, TestHelper.validExerciseSolutionAnswer()),
                "Modifying an already submitted solution is being allowed"
        );
        verify(exam, only()).getState();
        verify(solution, times(1)).getExercise();
        verify(exercise, only()).getExam();
        verify(submission, only()).getState();
        verify(solution, times(1)).getSubmission();
        verifyNoMoreInteractions(solution);
        verifyOnlySolutionSearch(solutionId);
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Abstract tests
    // ========================================

    /**
     * Tests that creating an {@link ExamSolutionSubmission}
     * for an {@code exam} with the given {@code state} is not allowed.
     *
     * @param exam            The {@link Exam} for which an {@link ExamSolutionSubmission} is being tried to be created.
     * @param state           The {@link Exam.State} being tested.
     * @param submitter       The principal of the user performing the operation
     *                        (used to setup the {@code securityContext}).
     * @param authentication  The {@link Authentication} set in the {@code securityContext}.
     * @param securityContext The {@link SecurityContext} used to retrieve user information.
     */
    private void testCreateExamSolutionSubmission(
            final Exam exam,
            final Exam.State state,
            final String submitter,
            final Authentication authentication,
            final SecurityContext securityContext) {
        TestHelper.setupSecurityContext(submitter, authentication, securityContext);
        final var examId = TestHelper.validExamId();
        when(exam.getState()).thenReturn(state);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> solutionsManager.createExamSolutionSubmission(examId),
                "Creating an exam solution submission for an exam with " + state + " state is being allowed"
        );
        verifyOnlyExamSearch(examId);
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that submitting an {@link ExamSolutionSubmission}
     * for an {@code exam} with the given {@code state} is not allowed.
     *
     * @param exam       The {@link Exam} for which an {@link ExamSolutionSubmission} is being tried to be created.
     * @param state      The {@link Exam.State} being tested.
     * @param submission The {@link ExamSolutionSubmission} trying to be submitted.
     */
    private void testSubmitSolutionsExamState(
            final Exam exam,
            final Exam.State state,
            final ExamSolutionSubmission submission) {
        final var submissionId = TestHelper.validExamSolutionSubmissionId();
        when(exam.getState()).thenReturn(state);
        when(submission.getExam()).thenReturn(exam);
        when(examSolutionSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> solutionsManager.submitSolutions(submissionId),
                "Submitting solutions for an exam with " + state + " state is being allowed"
        );
        verify(exam, only()).getState();
        verify(submission, only()).getExam();
        verifyOnlySubmissionSearch(submissionId);
    }

    /**
     * Tests that modifying an {@link ExerciseSolution}
     * for an {@code exam} with the given {@code state} is not allowed.
     *
     * @param exam     The {@link Exam} that owns the {@link Exercise}
     *                 owning the {@code solution} that is being tried to be modified.
     * @param state    The {@link Exam.State} being tested.
     * @param exercise The {@link Exercise} that owns the {@code solution} that is being tried to be modified.
     * @param solution The {@link ExerciseSolution} being tried to be modified.
     */
    private void testModifySolutionsExamState(
            final Exam exam,
            final Exam.State state,
            final Exercise exercise,
            final ExerciseSolution solution) {

        final var solutionId = TestHelper.validExerciseSolutionId();
        when(solution.getExercise()).thenReturn(exercise);
        when(exercise.getExam()).thenReturn(exam);
        when(exam.getState()).thenReturn(state);
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> solutionsManager.modifySolution(solutionId, TestHelper.validExerciseSolutionAnswer()),
                "Modifying a solution for an exam with " + state + " state is being allowed"
        );
        verify(exam, only()).getState();
        verify(solution, only()).getExercise();
        verify(exercise, only()).getExam();
        verifyOnlySolutionSearch(solutionId);
    }
}
