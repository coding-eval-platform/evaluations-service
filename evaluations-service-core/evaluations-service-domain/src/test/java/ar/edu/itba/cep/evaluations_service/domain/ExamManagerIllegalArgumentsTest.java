package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult;
import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutorServiceCommandMessageProxy;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Test class for {@link ExamManager}, containing tests for the illegal arguments situations
 * (i.e how the manager behaves when operating with invalid values).
 */
@ExtendWith(MockitoExtension.class)
class ExamManagerIllegalArgumentsTest extends AbstractExamManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository             A mocked {@link ExamRepository} passed to super class.
     * @param exerciseRepository         A mocked {@link ExerciseRepository} passed to super class.
     * @param testCaseRepository         A mocked {@link TestCaseRepository} passed to super class.
     * @param exerciseSolutionRepository A mocked {@link ExerciseSolutionRepository} passed to super class.
     * @param exerciseSolResultRep       A mocked {@link ExerciseSolutionResultRepository} passed to super class.
     * @param executorServiceProxy       A mocked {@link ExecutorServiceCommandMessageProxy} passed to super class.
     */
    ExamManagerIllegalArgumentsTest(
            @Mock(name = "examRep") final ExamRepository examRepository,
            @Mock(name = "exerciseRep") final ExerciseRepository exerciseRepository,
            @Mock(name = "testCaseRep") final TestCaseRepository testCaseRepository,
            @Mock(name = "exerciseSolutionRep") final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock(name = "exerciseSolutionResultRep") final ExerciseSolutionResultRepository exerciseSolResultRep,
            @Mock(name = "executorServiceProxy") final ExecutorServiceCommandMessageProxy executorServiceProxy) {
        super(examRepository,
                exerciseRepository,
                testCaseRepository,
                exerciseSolutionRepository,
                exerciseSolResultRep,
                executorServiceProxy);
    }


    // ================================================================================================================
    // Exams
    // ================================================================================================================

    /**
     * Tests that an {@link Exam} is not created (i.e is not saved) when arguments are not valid.
     */
    @Test
    void testExamIsNotCreatedUsingInvalidArguments() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.createExam(
                        TestHelper.invalidExamDescription(),
                        TestHelper.invalidExamStartingAt(),
                        TestHelper.invalidExamDuration()
                ),
                "Using invalid arguments when creating an Exam did not throw an IllegalArgumentException"
        );
        verifyNoInteractionWithAnyMockedRepository();
        verifyZeroInteractions(executorServiceCommandMessageProxy);
    }


    /**
     * Tests that an {@link Exam} is not updated (i.e is saved) when arguments are invalid.
     *
     * @param exam A mocked {@link Exam} (the one being updated).
     */
    @Test
    void testExamIsNotModifiedWithInvalidArguments(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        final var newDescription = TestHelper.invalidExamDescription();
        final var newStartingAt = TestHelper.invalidExamStartingAt();
        final var newDuration = TestHelper.invalidExamDuration();
        doThrow(IllegalArgumentException.class).when(exam).update(newDescription, newStartingAt, newDuration);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.modifyExam(examId, newDescription, newStartingAt, newDuration),
                "Using invalid arguments when updating an Exam did not throw an IllegalArgumentException"
        );
        verify(exam, only()).update(newDescription, newStartingAt, newDuration);
        verifyOnlyExamSearch(examId);
        verifyZeroInteractions(executorServiceCommandMessageProxy);
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    /**
     * Tests that creating an exercise for an upcoming exam is not performed with invalid arguments.
     *
     * @param exam A mocked {@link Exam} (the future owner of the {@link Exercise}).
     */
    @Test
    void testCreateExerciseWithInvalidArgumentsForUpcomingExam(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.createExercise(
                        examId,
                        TestHelper.invalidExerciseQuestion(),
                        TestHelper.invalidLanguage(),
                        TestHelper.validSolutionTemplate(), // There is no invalid value for the solution template.
                        TestHelper.nonPositiveAwardedScore()
                ),
                "Using invalid arguments when creating an Exercise did not throw an IllegalArgumentException"
        );
        verify(exam, only()).getState();
        verifyOnlyExamSearch(examId);
        verifyZeroInteractions(executorServiceCommandMessageProxy);
    }

    /**
     * Tests that modifying an exercise with an invalid value is not performed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one being modified).
     */
    @Test
    void testModifyExerciseWithInvalidArgumentsForUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var exerciseId = TestHelper.validExerciseId();
        final var question = TestHelper.invalidExerciseQuestion();
        final var language = TestHelper.invalidLanguage();
        final var solutionTemplate = TestHelper.validSolutionTemplate(); // There is no invalid value for this.
        final var awardedScore = TestHelper.nonPositiveAwardedScore();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(exercise.getExam()).thenReturn(exam);
        doThrow(IllegalArgumentException.class).when(exercise).update(question, language, solutionTemplate, awardedScore);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.modifyExercise(
                        exerciseId,
                        question,
                        language,
                        solutionTemplate,
                        awardedScore
                ),
                "Using an invalid value when modifying an Exercise" +
                        " did not throw an IllegalArgumentException"
        );
        verify(exam, only()).getState();
        verify(exercise, times(1)).getExam();
        verify(exercise, times(1)).update(question, language, solutionTemplate, awardedScore);
        verifyNoMoreInteractions(exercise);
        verifyOnlyExerciseSearch(exerciseId);
        verifyZeroInteractions(executorServiceCommandMessageProxy);
    }


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    /**
     * Tests that creating a test case for an exercise of an upcoming exam is not performed with invalid arguments.
     *
     * @param exam     A mocked {@link Exam} (the owner of the {@link Exercise}s).
     * @param exercise A mocked {@link Exercise} (the future owner of the {@link TestCase}).
     */
    @Test
    void testCreateTestCaseWithInvalidArgumentsForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var exerciseId = TestHelper.validExerciseId();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(exercise.getExam()).thenReturn(exam);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.createTestCase(
                        exerciseId,
                        TestHelper.invalidTestCaseVisibility(),
                        TestHelper.invalidTestCaseTimeout(),
                        TestHelper.invalidTestCaseList(),
                        TestHelper.invalidTestCaseList()
                ),
                "Using invalid arguments when creating a TestCase did not throw an IllegalArgumentException"
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verifyOnlyExerciseSearch(exerciseId);
        verifyZeroInteractions(executorServiceCommandMessageProxy);
    }

    /**
     * Tests that modifying a test case with an invalid value is not performed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one being modified).
     */
    @Test
    void testModifyExerciseWithInvalidArgumentsForUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {

        final var testCaseId = TestHelper.validTestCaseId();

        final var newVisibility = TestHelper.invalidTestCaseVisibility();
        final var newTimeout = TestHelper.validTestCaseTimeout();
        final var newInputs = TestHelper.invalidTestCaseList();
        final var newExpectedOutputs = TestHelper.invalidTestCaseList();

        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(exercise.getExam()).thenReturn(exam);
        when(testCase.getExercise()).thenReturn(exercise);
        doThrow(IllegalArgumentException.class).when(testCase).update(
                newVisibility,
                newTimeout,
                newInputs,
                newExpectedOutputs
        );
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));


        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.modifyTestCase(testCaseId, newVisibility, newTimeout, newInputs, newExpectedOutputs),
                "Using an invalid value when modifying a TestCase did not throw an IllegalArgumentException"
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verify(testCase, times(1)).getExercise();
        verify(testCase, times(1)).update(
                newVisibility,
                newTimeout,
                newInputs,
                newExpectedOutputs
        );
        verifyNoMoreInteractions(testCase);
        verifyOnlyTestCaseSearch(testCaseId);
        verifyZeroInteractions(executorServiceCommandMessageProxy);
    }


    // ================================================================================================================
    // Solutions
    // ================================================================================================================

    /**
     * Tests that creating a solution for an exercise belonging to an in progress exam is performed as expected.
     *
     * @param exam     A mocked {@link Exercise} (the owner of the {@link Exercise}s).
     * @param exercise A mocked {@link Exercise} (the future owner of the {@link ExerciseSolution}).
     */
    @Test
    void testCreateSolutionForExerciseBelongingToInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var exerciseId = TestHelper.validExerciseId();
        when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        when(exercise.getExam()).thenReturn(exam);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.createExerciseSolution(
                        exerciseId,
                        TestHelper.invalidExerciseSolutionAnswer()
                ),
                "Using invalid arguments when creating an ExerciseSolution" +
                        " did not throw an IllegalArgumentException"
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verifyOnlyExerciseSearch(exerciseId);
        verifyZeroInteractions(executorServiceCommandMessageProxy);
    }


    // ================================================================================================================
    // Solution Results
    // ================================================================================================================

    /**
     * Tests the processing of an execution result when using a {@code null} {@link ExecutionResult}.
     */
    @Test
    void testProcessExecutionWithNullExecutionResult() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.processExecution(
                        TestHelper.validExerciseSolutionId(),
                        TestHelper.validTestCaseId(),
                        null
                ),
                "Using null stdout or null stderr is being allowed"
        );
        verifyNoInteractionWithAnyMockedRepository();
    }
}
