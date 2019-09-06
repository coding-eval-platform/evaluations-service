package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.events.ExamSolutionSubmittedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExamSolutionSubmissionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Test class for {@link SolutionsManager}, containing tests for the happy paths
 * (i.e how the manager behaves when operating with valid values, entity states, etc.).
 */
@ExtendWith(MockitoExtension.class)
class SolutionsManagerHappyPathsTest extends AbstractSolutionsManagerTest {

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
    SolutionsManagerHappyPathsTest(
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
     * Tests that searching for an {@link ExamSolutionSubmission} that exists
     * returns the expected {@link ExamSolutionSubmission}.
     *
     * @param submission A mocked {@link ExamSolutionSubmission}
     *                   (which is returned by {@link SolutionsManager#getSubmission(long)}).
     */
    @Test
    void testSearchForExamThatExists(@Mock(name = "submission") final ExamSolutionSubmission submission) {
        final var submissionsId = TestHelper.validExerciseSolutionId();
        when(submission.getId()).thenReturn(submissionsId);
        when(examSolutionSubmissionRepository.findById(submissionsId)).thenReturn(Optional.of(submission));
        final var submissionOptional = solutionsManager.getSubmission(submissionsId);
        Assertions.assertAll("Searching for a submission that exists is not working as expected",
                () -> Assertions.assertTrue(
                        submissionOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        submissionsId,
                        submissionOptional.map(ExamSolutionSubmission::getId).get().longValue(),
                        "The returned submission id's is not the same as the requested"
                )
        );
        verifyOnlySubmissionSearch(submissionsId);
    }

    /**
     * Tests that an {@link ExamSolutionSubmission} is created (i.e is saved) when arguments are valid,
     * creating also all the corresponding {@link ExerciseSolution}s.
     *
     * @param exam            A mocked {@link Exam} (i.e the one to which the submission belongs).
     * @param exercise1       A mocked {@link Exercise} (i.e owned by the {@code exam}).
     * @param exercise2       Another mocked {@link Exercise} (i.e also owned by the {@code exam}).
     * @param authentication  A mocked {@link Authentication} that will hold a mocked principal.
     * @param securityContext A mocked {@link SecurityContext} to be retrieved from the {@link SecurityContextHolder}.
     */
    @Test
    void testExamIsCreatedUsingValidArguments(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise1") final Exercise exercise1,
            @Mock(name = "exercise1") final Exercise exercise2,
            @Mock(name = "authentication") final Authentication authentication,
            @Mock(name = "securityContext") final SecurityContext securityContext) {
        final var examId = TestHelper.validExamId();
        final var submitter = TestHelper.validOwner();
        final var exercises = List.of(exercise1, exercise2);
        when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        when(exercise1.getExam()).thenReturn(exam);
        when(exercise2.getExam()).thenReturn(exam);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(examSolutionSubmissionRepository.save(any(ExamSolutionSubmission.class))).then(i -> i.getArgument(0));
        when(examSolutionSubmissionRepository.existsSubmissionFor(exam, submitter)).thenReturn(false);
        when(exerciseRepository.getExamExercises(exam)).thenReturn(exercises);
        when(exerciseSolutionRepository.save(any(ExerciseSolution.class))).then(i -> i.getArgument(0));

        TestHelper.setupSecurityContext(submitter, authentication, securityContext);

        final var submission = solutionsManager.createExamSolutionSubmission(examId);
        Assertions.assertAll("ExamSolutionSubmission properties are not the expected",
                () -> Assertions.assertEquals(
                        exam,
                        submission.getExam(),
                        "There is a mismatch in the exam"
                ),
                () -> Assertions.assertEquals(
                        submitter,
                        submission.getSubmitter(),
                        "There is a mismatch in the submitter"
                )
        );
        verify(examRepository, only()).findById(examId);
        verify(exerciseRepository, only()).getExamExercises(exam);
        verify(examSolutionSubmissionRepository, times(1)).existsSubmissionFor(exam, submitter);
        verify(examSolutionSubmissionRepository, times(1)).save(any(ExamSolutionSubmission.class));
        verifyNoMoreInteractions(examSolutionSubmissionRepository);
        exercises.forEach(e -> verify(exerciseSolutionRepository, times(1))
                .save(argThat(inner -> inner.getExercise().equals(e)))
        );
        verifyNoMoreInteractions(exerciseSolutionRepository);
        verifyZeroInteractions(publisher);

        TestHelper.clearSecurityContext();
    }

    /**
     * Tests that solutions are submitted, sending the corresponding {@link ExamSolutionSubmittedEvent}.
     *
     * @param submission A mocked {@link ExamSolutionSubmission} (i.e the one being submitted)
     * @param exam       A mocked {@link Exam} (i.e the one to which the submission belongs).
     */
    @Test
    void testExamSolutionSubmission(
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "exam") final Exam exam) {
        final var submissionsId = TestHelper.validExerciseSolutionId();
        when(submission.getExam()).thenReturn(exam);
        doNothing().when(submission).submit();
        when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        when(examSolutionSubmissionRepository.findById(submissionsId)).thenReturn(Optional.of(submission));
        when(examSolutionSubmissionRepository.save(submission)).thenReturn(submission);
        doNothing().when(publisher).publishEvent(any(ExamSolutionSubmittedEvent.class));
        Assertions.assertDoesNotThrow(
                () -> solutionsManager.submitSolutions(submissionsId),
                "An unexpected exception is thrown when submitting an exam solution"
        );
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseSolutionRepository);
        verify(examSolutionSubmissionRepository, times(1)).findById(submissionsId);
        verify(examSolutionSubmissionRepository, times(1)).save(submission);
        verifyNoMoreInteractions(examSolutionSubmissionRepository);
        verifyZeroInteractions(exerciseSolutionRepository);
        verify(publisher, only()).publishEvent(
                argThat((final ExamSolutionSubmittedEvent event) -> event.getSubmission().equals(submission))
        );
    }


    // ================================================================================================================
    // Exercises Solutions
    // ================================================================================================================

    /**
     * Tests that the {@link List} of {@link ExerciseSolution}s belonging to a given {@link ExamSolutionSubmission}
     * is returned as expected.
     *
     * @param submission A mocked {@link ExamSolutionSubmission} (the owner of the {@link ExerciseSolution}s).
     * @param solutions  A mocked {@link List} of {@link ExerciseSolution}s owned by the {@link ExamSolutionSubmission}.
     */
    @Test
    void testGetExerciseSolutions(
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "solutions") final List<ExerciseSolution> solutions) {
        final var submissionId = TestHelper.validExamSolutionSubmissionId();
        when(examSolutionSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(exerciseSolutionRepository.getExerciseSolutions(submission)).thenReturn(solutions);
        Assertions.assertEquals(
                solutions,
                solutionsManager.getSolutionsForSubmission(submissionId),
                "The returned solutions list is not the one returned by the repository"
        );
        verify(examSolutionSubmissionRepository, only()).findById(submissionId);
        verify(exerciseSolutionRepository, only()).getExerciseSolutions(submission);
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests that searching for an {@link ExerciseSolution} that exists returns the expected {@link ExerciseSolution}.
     *
     * @param solution A mocked {@link ExerciseSolution}
     *                 (which is returned by {@link SolutionsManager#getSolution(long)}).
     */
    @Test
    void testSearchForExerciseThatExists(@Mock(name = "solution") final ExerciseSolution solution) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(solution.getId()).thenReturn(solutionId);
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        final var solutionOptional = solutionsManager.getSolution(solutionId);
        Assertions.assertAll("Searching for an exercise solution that exists is not working as expected",
                () -> Assertions.assertTrue(
                        solutionOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        solutionId,
                        solutionOptional.map(ExerciseSolution::getId).get().longValue(),
                        "The returned ExerciseSolution id's is not the same as the requested"
                )
        );
        verifyOnlySolutionSearch(solutionId);
    }

    /**
     * Tests that modifying an {@link ExerciseSolution} belonging to an {@link Exercise} of an in progress {@link Exam},
     * and an {@link ExamSolutionSubmission} that is not submitted is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one being modified).
     */
    @Test
    void testModifyExerciseWithValidArgumentsForUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "solution") final ExerciseSolution solution) {

        final var solutionId = TestHelper.validExerciseSolutionId();
        final var answer = TestHelper.validExerciseSolutionAnswer();
        when(exercise.getExam()).thenReturn(exam);
        when(solution.getExercise()).thenReturn(exercise);
        when(solution.getSubmission()).thenReturn(submission);
        when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        when(submission.getState()).thenReturn(ExamSolutionSubmission.State.UNPLACED);
        doNothing().when(solution).setAnswer(answer);
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        when(exerciseSolutionRepository.save(solution)).thenReturn(solution);
        Assertions.assertDoesNotThrow(
                () -> solutionsManager.modifySolution(solutionId, answer),
                "An unexpected exception was thrown"
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verify(submission, only()).getState();
        verify(solution, times(1)).getExercise();
        verify(solution, times(1)).setAnswer(answer);
        verifyNoMoreInteractions(solution);
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(examSolutionSubmissionRepository);
        verify(exerciseSolutionRepository, times(1)).findById(solutionId);
        verify(exerciseSolutionRepository, times(1)).save(solution);
        verifyNoMoreInteractions(exerciseSolutionRepository);
        verifyZeroInteractions(publisher);
    }
}