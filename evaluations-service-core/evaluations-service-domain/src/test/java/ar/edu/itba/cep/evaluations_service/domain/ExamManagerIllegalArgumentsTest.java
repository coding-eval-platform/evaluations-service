package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.messages_sender.ExecutorServiceCommandProxy;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

/**
 * Test class for {@link ExamManager}, containing tests for the illegal arguments situations
 * (i.e how the manager behaves when operating with invalid values).
 */
@ExtendWith(MockitoExtension.class)
class ExamManagerIllegalArgumentsTest extends AbstractExamManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository              A mocked {@link ExamRepository} passed to super class.
     * @param exerciseRepository          A mocked {@link ExerciseRepository} passed to super class.
     * @param testCaseRepository          A mocked {@link TestCaseRepository} passed to super class.
     * @param exerciseSolutionRepository  A mocked {@link ExamRepository} passed to super class.
     * @param executorServiceCommandProxy A mocked {@link ExecutorServiceCommandProxy} passed to super class.
     */
    ExamManagerIllegalArgumentsTest(
            @Mock(name = "examRep") final ExamRepository examRepository,
            @Mock(name = "exerciseRep") final ExerciseRepository exerciseRepository,
            @Mock(name = "testCaseRep") final TestCaseRepository testCaseRepository,
            @Mock(name = "exerciseSolutionRep") final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock(name = "executorServiceCommandProxy") final ExecutorServiceCommandProxy executorServiceCommandProxy) {
        super(examRepository,
                exerciseRepository,
                testCaseRepository,
                exerciseSolutionRepository,
                executorServiceCommandProxy);
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
        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
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
        Mockito.doThrow(IllegalArgumentException.class).when(exam).update(newDescription, newStartingAt, newDuration);
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.modifyExam(examId, newDescription, newStartingAt, newDuration),
                "Using invalid arguments when updating an Exam did not throw an IllegalArgumentException"
        );
        Mockito.verify(exam, Mockito.only()).update(newDescription, newStartingAt, newDuration);
        verifyOnlyExamSearch(examId);
        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
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
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.createExercise(
                        examId,
                        TestHelper.invalidExerciseQuestion()
                ),
                "Using invalid arguments when creating an Exercise did not throw an IllegalArgumentException"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        verifyOnlyExamSearch(examId);
        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
    }

    /**
     * Tests that changing the question of an exercise with an invalid value is not performed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one whose question is being changed).
     */
    @Test
    void testChangeExerciseQuestionWithInvalidQuestionForUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var exerciseId = TestHelper.validExerciseId();
        final var newQuestion = TestHelper.invalidExerciseQuestion();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.getExam()).thenReturn(exam);
        Mockito.doThrow(IllegalArgumentException.class).when(exercise).setQuestion(newQuestion);
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.changeExerciseQuestion(exerciseId, newQuestion),
                "Using an invalid value when changing an Exercise's question" +
                        " did not throw an IllegalArgumentException"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.times(1)).getExam();
        Mockito.verify(exercise, Mockito.times(1)).setQuestion(newQuestion);
        Mockito.verifyNoMoreInteractions(exercise);
        verifyOnlyExerciseSearch(exerciseId);
        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
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
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.getExam()).thenReturn(exam);
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.createTestCase(
                        exerciseId,
                        TestHelper.invalidTestCaseVisibility(),
                        TestHelper.invalidTestCaseList(),
                        TestHelper.invalidTestCaseList()
                ),
                "Using invalid arguments when creating a TestCase did not throw an IllegalArgumentException"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).getExam();
        verifyOnlyExerciseSearch(exerciseId);
        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
    }

    /**
     * Tests that changing the visibility of a test case with an invalid value is not performed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one whose visibility is being changed).
     */
    @Test
    void testChangeVisibilityWithInvalidValueForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        final var newVisibility = TestHelper.invalidTestCaseVisibility();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.getExam()).thenReturn(exam);
        Mockito.when(testCase.getExercise()).thenReturn(exercise);
        Mockito.doThrow(IllegalArgumentException.class).when(testCase).setVisibility(newVisibility);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.changeVisibility(testCaseId, newVisibility),
                "Using an invalid value when changing a TestCase's visibility" +
                        " did not throw an IllegalArgumentException"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).getExam();
        Mockito.verify(testCase, Mockito.times(1)).getExercise();
        Mockito.verify(testCase, Mockito.times(1)).setVisibility(newVisibility);
        Mockito.verifyNoMoreInteractions(testCase);
        verifyOnlyTestCaseSearch(testCaseId);
        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
    }

    /**
     * Tests that changing the inputs of a test case with an invalid value is not performed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one whose inputs are being changed).
     */
    @Test
    void testChangeInputsWithInvalidValueForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        final var newInputs = TestHelper.invalidTestCaseList();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.getExam()).thenReturn(exam);
        Mockito.when(testCase.getExercise()).thenReturn(exercise);
        Mockito.doThrow(IllegalArgumentException.class).when(testCase).setInputs(newInputs);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.changeInputs(testCaseId, newInputs),
                "Using an invalid value when changing a TestCase's inputs" +
                        " did not throw an IllegalArgumentException"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).getExam();
        Mockito.verify(testCase, Mockito.times(1)).getExercise();
        Mockito.verify(testCase, Mockito.times(1)).setInputs(newInputs);
        Mockito.verifyNoMoreInteractions(testCase);
        verifyOnlyTestCaseSearch(testCaseId);
        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
    }

    /**
     * Tests that changing the expected outputs of a test case with an invalid value is not performed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one whose expected outputs are being changed).
     */
    @Test
    void testChangeExpectedOutputsWithInvalidValueForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        final var newExpectedOutputs = TestHelper.invalidTestCaseList();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.getExam()).thenReturn(exam);
        Mockito.when(testCase.getExercise()).thenReturn(exercise);
        Mockito.doThrow(IllegalArgumentException.class).when(testCase).setExpectedOutputs(newExpectedOutputs);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.changeExpectedOutputs(testCaseId, newExpectedOutputs),
                "Using an invalid value when changing a TestCase's expected outputs" +
                        " did not throw an IllegalArgumentException"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).getExam();
        Mockito.verify(testCase, Mockito.times(1)).getExercise();
        Mockito.verify(testCase, Mockito.times(1)).setExpectedOutputs(newExpectedOutputs);
        Mockito.verifyNoMoreInteractions(testCase);
        verifyOnlyTestCaseSearch(testCaseId);
        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
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
        final var answer = TestHelper.invalidExerciseSolutionAnswer();
        final var exerciseId = TestHelper.validExerciseId();
        Mockito.when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        Mockito.when(exercise.getExam()).thenReturn(exam);
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.createExerciseSolution(
                        exerciseId,
                        answer
                ),
                "Using invalid arguments when creating an ExerciseSolution" +
                        " did not throw an IllegalArgumentException"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).getExam();
        verifyOnlyExerciseSearch(exerciseId);
        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
    }


    // ================================================================================================================
    // Solution Results
    // ================================================================================================================

    // TODO: move
//    /**
//     * Tests the processing of an execution result when using {@code null} for both {@code stdOut} and {@code stdErr}.
//     */
//    @Test
//    void testProcessExecutionWithNullStdOutAndNullStdErr() {
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> examManager.processExecution(
//                        TestHelper.validExerciseSolutionId(),
//                        TestHelper.validTestCaseId(),
//                        TestHelper.validExerciseSolutionExitCode(),
//                        null,
//                        null
//                ),
//                "Using null stdOut or null stdErr is being allowed"
//        );
//        verifyNoInteractionWithAnyMockedRepository();
//        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
//    }
//
    // TODO: move
//    /**
//     * Tests the processing of an execution result when using {@code null} {@code stdOut}.
//     */
//    @Test
//    void testProcessExecutionWithNullStdOut() {
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> examManager.processExecution(
//                        TestHelper.validExerciseSolutionId(),
//                        TestHelper.validTestCaseId(),
//                        TestHelper.validExerciseSolutionExitCode(),
//                        null,
//                        TestHelper.validExerciseSolutionResultList()
//                ),
//                "Using null stdOut is being allowed"
//        );
//        verifyNoInteractionWithAnyMockedRepository();
//        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
//    }
//
    // TODO: move
//    /**
//     * Tests the processing of an execution result when using {@code null} {@code stdErr}.
//     */
//    @Test
//    void testProcessExecutionWithNullStdErr() {
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> examManager.processExecution(
//                        TestHelper.validExerciseSolutionId(),
//                        TestHelper.validTestCaseId(),
//                        TestHelper.validExerciseSolutionExitCode(),
//                        TestHelper.validExerciseSolutionResultList(),
//                        null
//                ),
//                "Using null stdOut is being allowed"
//        );
//        verifyNoInteractionWithAnyMockedRepository();
//        Mockito.verifyZeroInteractions(executorServiceCommandProxy);
//    }
}
