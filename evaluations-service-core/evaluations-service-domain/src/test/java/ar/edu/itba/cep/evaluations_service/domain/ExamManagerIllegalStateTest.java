package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Test class for {@link ExamManager}, containing tests for the illegal state situations
 * (i.e how the manager behaves when operating with exams that are not in a valid state for operating with).
 */
@ExtendWith(MockitoExtension.class)
class ExamManagerIllegalStateTest extends AbstractExamManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository             A mocked {@link ExamRepository} passed to super class.
     * @param exerciseRepository         A mocked {@link ExerciseRepository} passed to super class.
     * @param testCaseRepository         A mocked {@link TestCaseRepository} passed to super class.
     * @param exerciseSolutionRepository A mocked {@link ExamRepository} passed to super class.
     * @param exerciseSolResultRep       A mocked {@link ExerciseSolutionResultRepository} passed to super class.
     */
    ExamManagerIllegalStateTest(
            @Mock(name = "examRep") final ExamRepository examRepository,
            @Mock(name = "exerciseRep") final ExerciseRepository exerciseRepository,
            @Mock(name = "testCaseRep") final TestCaseRepository testCaseRepository,
            @Mock(name = "exerciseSolutionRep") final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock(name = "exerciseSolutionResultRep") final ExerciseSolutionResultRepository exerciseSolResultRep) {
        super(examRepository,
                exerciseRepository,
                testCaseRepository,
                exerciseSolutionRepository,
                exerciseSolResultRep);
    }


    // ================================================================================================================
    // Exams
    // ================================================================================================================

    /**
     * Tests the service behaviour when {@link Exam#update(String, LocalDateTime, Duration)}
     * throws an {@link IllegalEntityStateException}.
     * <p>
     * A mocked {@link Exam} (the one being modified).
     */
    @Test
    void testExamIsNotUpdatedWhenIllegalEntityStateExceptionIsThrown(@Mock(name = "exam") final Exam exam) {
        final var newDescription = TestHelper.validExamDescription();
        final var newStartingAt = TestHelper.validExamStartingMoment();
        final var newDuration = TestHelper.validExamDuration();
        testExam(
                exam,
                (manager, id) -> manager.modifyExam(id, newDescription, newStartingAt, newDuration),
                e -> e.update(newDescription, newStartingAt, newDuration),
                "An IllegalEntityStateException is not being thrown when the Exam does" +
                        " (when updating an Exam)");
    }

    /**
     * Tests the service behaviour when {@link Exam#startExam()} throws an {@link IllegalEntityStateException}.
     *
     * @param exam A mocked {@link Exam} (the one being started).
     */
    @Test
    void testExamIsNotStartedWhenIllegalEntityStateExceptionIsThrown(@Mock(name = "exam") final Exam exam) {
        testExam(exam, ExamManager::startExam, Exam::startExam,
                "An IllegalEntityStateException is not being thrown when the Exam does" +
                        " (when starting an Exam)");
    }

    /**
     * Tests the service behaviour when {@link Exam#finishExam()} throws an {@link IllegalEntityStateException}.
     *
     * @param exam A mocked {@link Exam} (the one being finished).
     */
    @Test
    void testExamIsNotFinishedWhenIllegalEntityStateExceptionIsThrown(@Mock(name = "exam") final Exam exam) {
        testExam(exam, ExamManager::finishExam, Exam::finishExam,
                "An IllegalEntityStateException is not being thrown when the Exam does" +
                        " (when finishing an Exam)");
    }

    /**
     * Tests that deleting an in progress exam is not allowed.
     * <p>
     * A mocked {@link Exam} (the one being deleted).
     */
    @Test
    void testDeleteOfInProgressExam(@Mock(name = "exam") final Exam exam) {
        testDeleteExam(exam, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that deleting a finished exam is not allowed.
     *
     * @param exam A mocked {@link Exam} (the one being deleted).
     */
    @Test
    void testDeleteOfFinishedExam(@Mock(name = "exam") final Exam exam) {
        testDeleteExam(exam, Exam.State.FINISHED);
    }

    /**
     * Tests that clearing exercises of an in progress exam is not allowed.
     *
     * @param exam A mocked {@link Exam} (the one whose exercises are being cleared).
     */
    @Test
    void testClearExercisesOfInProgressExam(@Mock(name = "exam") final Exam exam) {
        testClearExercises(exam, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that clearing exercises of a finished exam is not allowed.
     *
     * @param exam A mocked {@link Exam} (the one whose exercises are being cleared).
     */
    @Test
    void testClearExercisesOfFinishedExam(@Mock(name = "exam") final Exam exam) {
        testClearExercises(exam, Exam.State.FINISHED);
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    /**
     * Tests that creating an exercise for an in progress exam is not allowed.
     *
     * @param exam A mocked {@link Exam} (the one for which an exercise is being created).
     */
    @Test
    void testCreateExerciseForInProgressExam(@Mock(name = "exam") final Exam exam) {
        testCreateExercise(exam, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that creating an exercise for a finished exam is not allowed.
     *
     * @param exam A mocked {@link Exam} (the one for which an exercise is being created).
     */
    @Test
    void testCreateExerciseForFinishedExam(@Mock(name = "exam") final Exam exam) {
        testCreateExercise(exam, Exam.State.FINISHED);
    }

    /**
     * Tests that changing the question of an exercise belonging to an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one whose question is being changed).
     */
    @Test
    void testChangeExerciseQuestionForInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testChangeExerciseQuestion(exam, exercise, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that changing the question of an exercise belonging to an finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one whose question is being changed).
     */
    @Test
    void testChangeExerciseQuestionForFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testChangeExerciseQuestion(exam, exercise, Exam.State.FINISHED);
    }

    /**
     * Tests that deleting an exercise belonging to an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one being deleted).
     */
    @Test
    void testDeleteExerciseForInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testDeleteExercise(exam, exercise, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that deleting an exercise belonging to a finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one being deleted).
     */
    @Test
    void testDeleteExerciseForFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testDeleteExercise(exam, exercise, Exam.State.FINISHED);
    }


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    /**
     * Tests that creating a test case for an exercise belonging to an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one for which the test case is being created).
     */
    @Test
    void testCreateTestCaseForExerciseOfInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testCreateTestCase(exam, exercise, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that creating a test case for an exercise belonging to a finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one for which the test case is being created).
     */
    @Test
    void testCreateTestCaseForExerciseOfFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testCreateTestCase(exam, exercise, Exam.State.FINISHED);
    }

    /**
     * Tests that changing the visibility of a test case belonging to an exercise of an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose visibility is being tried to be changed).
     */
    @Test
    void testChangeVisibilityForTestCaseOfExerciseOfInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testChangeVisibility(exam, exercise, testCase, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that changing the visibility of a test case belonging to an exercise of a finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose visibility is being tried to be changed).
     */
    @Test
    void testChangeVisibilityForTestCaseOfExerciseOfFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testChangeVisibility(exam, exercise, testCase, Exam.State.FINISHED);
    }

    /**
     * Tests that changing the inputs of a test case belonging to an exercise of an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose inputs are being tried to be changed).
     */
    @Test
    void testChangeInputsForTestCaseOfExerciseOfInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testChangeInputs(exam, exercise, testCase, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that changing the inputs of a test case belonging to an exercise of a finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose inputs are being tried to be changed).
     */
    @Test
    void testChangeInputsForTestCaseOfExerciseOfFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testChangeInputs(exam, exercise, testCase, Exam.State.FINISHED);
    }

    /**
     * Tests that changing the expected outputs
     * of a test case belonging to an exercise of an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose expected outputs are being tried to be changed).
     */
    @Test
    void testChangeExpectedOutputsForTestCaseOfExerciseOfInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testChangeExpectedOutputs(exam, exercise, testCase, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that changing the expected outputs
     * of a test case belonging to an exercise of a finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose expected outputs are being tried to be changed).
     */
    @Test
    void testChangeExpectedOutputsForTestCaseOfExerciseOfFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testChangeExpectedOutputs(exam, exercise, testCase, Exam.State.FINISHED);
    }

    /**
     * Tests that clearing the inputs of a test case belonging to an exercise of an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose inputs are being tried to be cleared).
     */
    @Test
    void testClearInputsForTestCaseOfExerciseOfInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testClearInputs(exam, exercise, testCase, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that clearing the inputs of a test case belonging to an exercise of a finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose inputs are being tried to be cleared).
     */
    @Test
    void testClearInputsForTestCaseOfExerciseOfFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testClearInputs(exam, exercise, testCase, Exam.State.FINISHED);
    }

    /**
     * Tests that clearing the expected outputs
     * of a test case belonging to an exercise of an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose expected outputs are being tried to be cleared).
     */
    @Test
    void testClearExpectedOutputsForTestCaseOfExerciseOfInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testClearExpectedOutputs(exam, exercise, testCase, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that clearing the expected outputs
     * of a test case belonging to an exercise of a finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose expected outputs are being tried to be cleared).
     */
    @Test
    void testClearExpectedOutputsForTestCaseOfExerciseOfFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testClearExpectedOutputs(exam, exercise, testCase, Exam.State.FINISHED);
    }

    /**
     * Tests that deleting a test case belonging to an exercise of an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one being tried to be deleted).
     */
    @Test
    void testDeleteTestCaseOfExerciseOfInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testDeleteTestCase(exam, exercise, testCase, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that deleting a test case belonging to an exercise of an finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one being tried to be deleted).
     */
    @Test
    void testDeleteTestCaseOfExerciseOfFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testDeleteTestCase(exam, exercise, testCase, Exam.State.FINISHED);
    }


    // ================================================================================================================
    // Solutions
    // ================================================================================================================

    /**
     * Tests that creating a solution for an exercise belonging to an upcoming exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one for which the solution is being created).
     */
    @Test
    void testCreateSolutionForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testExerciseSolutionCreation(exam, exercise, Exam.State.UPCOMING);
    }

    /**
     * Tests that creating a solution for an exercise belonging to a finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one for which the solution is being created).
     */
    @Test
    void testCreateSolutionForExerciseOfFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testExerciseSolutionCreation(exam, exercise, Exam.State.FINISHED);
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Abstract tests
    // ========================================

    /**
     * Tests that deleting an exam with the given {@code state} is not allowed.
     *
     * @param exam  The {@link Exam} being tried to be deleted.
     * @param state The {@link Exam.State} being tested.
     */
    private void testDeleteExam(final Exam exam, final Exam.State state) {
        testManagerForExam(exam, state, ExamManager::deleteExam,
                "Deleting an exam with " + state + " state is being allowed");
    }

    /**
     * Tests that clearing exercises of an exam with the given {@code state} is not allowed.
     *
     * @param exam  The {@link Exam} whose exercises are being tried to be deleted.
     * @param state The {@link Exam.State} being tested.
     */
    private void testClearExercises(final Exam exam, final Exam.State state) {
        testManagerForExam(exam, state, ExamManager::clearExercises,
                "Clearing exercises of an exam with " + state + " state is being allowed");
    }

    /**
     * Tests that creating an exercise for an exam with the given {@code state} is not allowed.
     *
     * @param exam  The {@link Exam} for which an exercise is being tried to be created.
     * @param state The {@link Exam.State} being tested.
     */
    private void testCreateExercise(final Exam exam, final Exam.State state) {
        testManagerForExam(exam, state,
                (manager, id) -> manager.createExercise(id, TestHelper.validExerciseQuestion()),
                "Creating an exercise for an exam with " + state + " state is being allowed");
    }

    /**
     * Tests that changing the question of an exercise belonging to an exam with the given {@code state}
     * is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} whose question is being tried to be changed.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testChangeExerciseQuestion(final Exam exam, final Exercise exercise, final Exam.State state) {
        testManagerExercise(exam, state, exercise,
                (manager, id) -> manager.changeExerciseQuestion(id, TestHelper.validExerciseQuestion()),
                "Changing the question for an exercise belonging to an exam with " + state + " state" +
                        " is being allowed.");
    }

    /**
     * Tests that deleting an exercise belonging to an exam with the given {@code state}
     * * is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise}  being tried to be deleted.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testDeleteExercise(final Exam exam, final Exercise exercise, final Exam.State state) {
        testManagerExercise(exam, state, exercise,
                ExamManager::deleteExercise,
                "Deleting an exercise belonging to an exam with " + state + " state" +
                        " is being allowed.");
    }

    /**
     * Tests that creating a test case for an exercise belonging to an exam with the given {@code state}
     * is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} for which a test case is being tried to be created.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testCreateTestCase(final Exam exam, final Exercise exercise, final Exam.State state) {
        testManagerExercise(exam, state, exercise,
                (manager, id) -> manager.createTestCase(
                        id,
                        TestHelper.validTestCaseVisibility(),
                        TestHelper.validTestCaseList(),
                        TestHelper.validTestCaseList()
                ),
                "Creating a test case for an exercise belonging to an exam with " + state + " state" +
                        " is being allowed.");
    }


    /**
     * Tests that changing the visibility of a test case belonging to an exercise
     * of an exam with the given {@code state} is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} owning the test case.
     * @param testCase The {@link TestCase} whose visibility is being tried to be changed.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testChangeVisibility(final Exam exam, final Exercise exercise, final TestCase testCase,
                                      final Exam.State state) {
        testManagerTestCase(exam, state, exercise, testCase,
                (manager, id) -> manager.changeVisibility(id, TestHelper.validTestCaseVisibility()),
                "Changing the visibility of a test case that belongs to an exercise" +
                        " of an exam with " + state + " state is being allowed");
    }

    /**
     * Tests that changing the inputs of a test case belonging to an exercise
     * of an exam with the given {@code state} is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} owning the test case.
     * @param testCase The {@link TestCase} whose inputs are being tried to be changed.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testChangeInputs(final Exam exam, final Exercise exercise, final TestCase testCase,
                                  final Exam.State state) {
        testManagerTestCase(exam, state, exercise, testCase,
                (manager, id) -> manager.changeInputs(id, TestHelper.validTestCaseList()),
                "Changing the inputs of a test case that belongs to an exercise" +
                        " of an exam with " + state + " state is being allowed");
    }

    /**
     * Tests that changing the expected outputs of a test case belonging to an exercise
     * of an exam with the given {@code state} is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} owning the test case.
     * @param testCase The {@link TestCase} whose expected outputs are being tried to be changed.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testChangeExpectedOutputs(final Exam exam, final Exercise exercise, final TestCase testCase,
                                           final Exam.State state) {
        testManagerTestCase(exam, state, exercise, testCase,
                (manager, id) -> manager.changeExpectedOutputs(id, TestHelper.validTestCaseList()),
                "Changing the expected outputs of a test case that belongs to an exercise" +
                        " of an exam with " + state + " state is being allowed");
    }

    /**
     * Tests that clearing the inputs of a test case belonging to an exercise
     * of an exam with the given {@code state} is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} owning the test case.
     * @param testCase The {@link TestCase} whose inputs are being tried to be cleared.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testClearInputs(final Exam exam, final Exercise exercise, final TestCase testCase,
                                 final Exam.State state) {
        testManagerTestCase(exam, state, exercise, testCase,
                ExamManager::clearInputs,
                "Clearing the inputs of a test case that belongs to an exercise" +
                        " of an exam with " + state + " state is being allowed");
    }

    /**
     * Tests that clearing the expected outputs of a test case belonging to an exercise
     * of an exam with the given {@code state} is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} owning the test case.
     * @param testCase The {@link TestCase} whose expected outputs are being tried to be cleared.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testClearExpectedOutputs(final Exam exam, final Exercise exercise, final TestCase testCase,
                                          final Exam.State state) {
        testManagerTestCase(exam, state, exercise, testCase,
                ExamManager::clearOutputs,
                "Clearing the expected outputs of a test case that belongs to an exercise" +
                        " of an exam with " + state + " state is being allowed");
    }


    /**
     * Tests that deleting a test case belonging to an exercise of an exam with the given {@code state} is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} owning the test case.
     * @param testCase The {@link TestCase} being tried to be deleted.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testDeleteTestCase(final Exam exam, final Exercise exercise, final TestCase testCase,
                                    final Exam.State state) {
        testManagerTestCase(exam, state, exercise, testCase,
                ExamManager::deleteTestCase,
                "Deleting a test case that belongs to an exercise" +
                        " of an exam with " + state + " state is being allowed");
    }


    /**
     * Tests that creating a solution for an exercise belonging to an exam with the given {@code state}
     * is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} for which a solution is being tried to be created.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testExerciseSolutionCreation(final Exam exam, final Exercise exercise, final Exam.State state) {
        final var exerciseId = TestHelper.validExerciseId();
        Mockito.when(exam.getState()).thenReturn(state);
        Mockito.when(exercise.getExam()).thenReturn(exam);
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManager.createExerciseSolution(exerciseId, TestHelper.validExerciseSolutionAnswer()),
                "Creating a solution for an exercise that belongs to an exam" +
                        " with " + state + " state is being allosed"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).getExam();
        verifyOnlyExerciseSearch(exerciseId);
    }


    /**
     * Performs a test over the {@link ExamManager} action that involves directly interacting with an {@link Exam},
     * when this throws an {@link IllegalEntityStateException}.
     *
     * @param exam                   The {@link Exam} being accessed.
     * @param examManagerAction      The action being tested.
     * @param examActionVerification A {@link Consumer} that takes the {@link Exam}
     *                               and performs the action being tested.
     * @param message                A message to be displayed in case of failure.
     */
    private void testExam(
            final Exam exam,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final Consumer<Exam> examActionVerification,
            final String message) {
        final var examId = TestHelper.validExamId();
        examActionVerification.accept(Mockito.doThrow(IllegalEntityStateException.class).when(exam));
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManagerAction.accept(examManager, examId),
                message
        );
        examActionVerification.accept(Mockito.verify(exam));
        verifyOnlyExamSearch(examId);
    }

    /**
     * Performs a test over the {@link ExamManager} action that involves only accessing an {@link Exam}.
     *
     * @param exam              The {@link Exam} being accessed.
     * @param state             The {@link Exam.State} being tested.
     * @param examManagerAction The action being tested.
     * @param message           A message to be displayed in case of failure.
     */
    private void testManagerForExam(
            final Exam exam,
            final Exam.State state,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        final var examId = TestHelper.validExamId();
        Mockito.when(exam.getState()).thenReturn(state);
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManagerAction.accept(examManager, examId),
                message
        );
        verifyOnlyExamSearch(examId);
    }

    /**
     * Performs a test over the {@link ExamManager} action that involves only accessing an {@link Exercise}.
     *
     * @param exam              The {@link Exam} that owns the exercise.
     * @param state             The {@link Exam.State} being tested.
     * @param exercise          The {@link Exercise} being accessed.
     * @param examManagerAction The action being tested.
     * @param message           A message to be displayed in case of failure.
     */
    private void testManagerExercise(
            final Exam exam,
            final Exam.State state,
            final Exercise exercise,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        final var exerciseId = TestHelper.validExerciseId();
        Mockito.when(exam.getState()).thenReturn(state);
        Mockito.when(exercise.getExam()).thenReturn(exam);
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManagerAction.accept(examManager, exerciseId),
                message
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).getExam();
        verifyOnlyExerciseSearch(exerciseId);
    }

    /**
     * Performs a test over the {@link ExamManager} action that involves only accessing a {@link TestCase}.
     *
     * @param exam              The {@link Exam} that owns the exercise.
     * @param state             The {@link Exam.State} being tested.
     * @param exercise          The {@link Exercise} that owns the test case.
     * @param testCase          The {@link TestCase} being accessed.
     * @param examManagerAction The action being tested.
     * @param message           A message to be displayed in case of failure.
     */
    private void testManagerTestCase(
            final Exam exam,
            final Exam.State state,
            final Exercise exercise,
            final TestCase testCase,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        final var testCaseId = TestHelper.validTestCaseId();
        Mockito.when(exam.getState()).thenReturn(state);
        Mockito.when(exercise.getExam()).thenReturn(exam);
        Mockito.when(testCase.getExercise()).thenReturn(exercise);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManagerAction.accept(examManager, testCaseId),
                message
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).getExam();
        Mockito.verify(testCase, Mockito.only()).getExercise();
        verifyOnlyTestCaseSearch(testCaseId);
    }
}
