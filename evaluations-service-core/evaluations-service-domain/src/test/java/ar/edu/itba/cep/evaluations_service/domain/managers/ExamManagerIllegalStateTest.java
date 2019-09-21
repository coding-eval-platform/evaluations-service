package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

/**
 * Test class for {@link ExamManager}, containing tests for the illegal state situations
 * (i.e how the manager behaves when operating with exams that are not in a valid state for operating with).
 */
@ExtendWith(MockitoExtension.class)
class ExamManagerIllegalStateTest extends AbstractExamManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository     A mocked {@link ExamRepository} passed to super class.
     * @param exerciseRepository A mocked {@link ExerciseRepository} passed to super class.
     * @param testCaseRepository A mocked {@link TestCaseRepository} passed to super class.
     * @param publisher          A mocked {@link ApplicationEventPublisher} passed to super class.
     */
    ExamManagerIllegalStateTest(
            @Mock(name = "examRepository") final ExamRepository examRepository,
            @Mock(name = "exerciseRepository") final ExerciseRepository exerciseRepository,
            @Mock(name = "testCaseRepository") final TestCaseRepository testCaseRepository,
            @Mock(name = "publisher") final ApplicationEventPublisher publisher) {
        super(examRepository, exerciseRepository, testCaseRepository, publisher);
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
     * @param exam     A mocked {@link Exam} (the one being started).
     * @param exercise A mocked {@link Exercise} (owned by the {@code exam}).
     * @param testCase A mocked {@link TestCase} (owned by the {@code exercise}).
     * @implNote In this test, the {@code exam} contains an {@link Exercise} with a private {@link TestCase} in order
     * to test only when the case in which {@link Exam#startExam()} throws the {@link IllegalEntityStateException}.
     */
    @Test
    void testExamIsNotStartedWhenIllegalEntityStateExceptionIsThrownByExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {

        final var examId = TestHelper.validExamId();
        doThrow(IllegalEntityStateException.class).when(exam).startExam();
        when(exerciseRepository.getExamExercises(exam)).thenReturn(List.of(exercise));
        when(testCaseRepository.getExercisePrivateTestCases(exercise)).thenReturn(List.of(testCase));
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManager.startExam(examId),
                "An IllegalEntityStateException is not being thrown when the Exam does" +
                        " (when starting an Exam)"
        );

        verify(exam, only()).startExam();
        verify(examRepository, only()).findById(examId);
        verify(exerciseRepository, only()).getExamExercises(exam);
        verify(testCaseRepository, only()).getExercisePrivateTestCases(exercise);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests the service behaviour when trying to start an {@link Exam} that contains an {@link Exercise} without
     * private {@link TestCase}s.
     *
     * @param exam     A mocked {@link Exam} (the one being started).
     * @param exercise A mocked {@link Exercise} (owned by the {@code exam}).
     */
    @Test
    void testExamIsNotStartedWhenExerciseDoesNotContainPrivateTestCases(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {

        final var examId = TestHelper.validExamId();
        when(exerciseRepository.getExamExercises(exam)).thenReturn(List.of(exercise));
        when(testCaseRepository.getExercisePrivateTestCases(exercise)).thenReturn(Collections.emptyList());
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManager.startExam(examId),
                "An IllegalEntityStateException is not being thrown when the Exam contains an Exercise" +
                        " without private Test Cases"
        );

        verifyZeroInteractions(exam);
        verify(examRepository, only()).findById(examId);
        verify(exerciseRepository, only()).getExamExercises(exam);
        verify(testCaseRepository, only()).getExercisePrivateTestCases(exercise);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests the service behaviour when trying to start an {@link Exam} that does not contain any {@link Exercise}.
     *
     * @param exam A mocked {@link Exam} (the one being started).
     */
    @Test
    void testExamIsNotStartedWhenItDoesNotContainAnyExercise(@Mock(name = "exam") final Exam exam) {

        final var examId = TestHelper.validExamId();
        when(exerciseRepository.getExamExercises(exam)).thenReturn(Collections.emptyList());
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManager.startExam(examId),
                "An IllegalEntityStateException is not being thrown" +
                        " when the Exam does not contain any Exercise"
        );

        verifyZeroInteractions(exam);
        verify(examRepository, only()).findById(examId);
        verify(exerciseRepository, only()).getExamExercises(exam);
        verifyZeroInteractions(testCaseRepository);
        verifyZeroInteractions(publisher);
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
     * Tests the service behaviour when {@link Exam#removeOwner(String)} throws an {@link IllegalEntityStateException}.
     *
     * @param exam A mocked {@link Exam} (the one to which an owner is being removed).
     */
    @Test
    void testExamIsNotSavedWhenAnIllegalEntityStateExceptionIsThrowOnRemoveAnOwner(
            @Mock(name = "exam") final Exam exam) {
        final var owner = TestHelper.validOwner();
        testExam(exam, (em, id) -> em.removeOwnerFromExam(id, owner), e -> e.removeOwner(owner),
                "An IllegalEntityStateException is not being thrown when the Exam does" +
                        " (when removing an Owner)");
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


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

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
     * Tests that modifying an exercise belonging to an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one whose being modified).
     */
    @Test
    void testModifyExerciseForInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testModifyExercise(exam, exercise, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that modifying an exercise belonging to an finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one being modified).
     */
    @Test
    void testModifyExerciseForFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        testModifyExercise(exam, exercise, Exam.State.FINISHED);
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
     * Tests that modifying a test case belonging to an exercise of an in progress exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose visibility is being tried to be changed).
     */
    @Test
    void testModifyTestCaseOfExerciseOfInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testModifyTestCase(exam, exercise, testCase, Exam.State.IN_PROGRESS);
    }

    /**
     * Tests that modifying a test case belonging to an exercise of a finished exam is not allowed.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case).
     * @param testCase A mocked {@link TestCase} (the one whose visibility is being tried to be changed).
     */
    @Test
    void testModifyTestCaseOfExerciseOfFinishedExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testModifyTestCase(exam, exercise, testCase, Exam.State.FINISHED);
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
                (manager, id) -> manager.createExercise(
                        id,
                        TestHelper.validExerciseQuestion(),
                        TestHelper.validLanguage(),
                        TestHelper.validSolutionTemplate(),
                        TestHelper.validAwardedScore()
                ),
                "Creating an exercise for an exam with " + state + " state is being allowed");
    }

    /**
     * Tests that modifying an exercise belonging to an exam with the given {@code state}
     * is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} being tried to be modified.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testModifyExercise(final Exam exam, final Exercise exercise, final Exam.State state) {
        testManagerExercise(exam, state, exercise,
                (manager, id) -> manager.modifyExercise(
                        id,
                        TestHelper.validExerciseQuestion(),
                        TestHelper.validLanguage(),
                        TestHelper.validSolutionTemplate(),
                        TestHelper.validAwardedScore()
                ),
                "Modifying an exercise belonging to an exam with " + state + " state" +
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
                        TestHelper.validTestCaseTimeout(),
                        TestHelper.validTestCaseList(),
                        TestHelper.validTestCaseList()
                ),
                "Creating a test case for an exercise belonging to an exam with " + state + " state" +
                        " is being allowed.");
    }

    /**
     * Tests that modifying a test case belonging to an exercise
     * of an exam with the given {@code state} is not allowed.
     *
     * @param exam     The {@link Exam} owning the exercise.
     * @param exercise The {@link Exercise} owning the test case.
     * @param testCase The {@link TestCase} whose visibility is being tried to be changed.
     * @param state    The {@link Exam.State} being tested.
     */
    private void testModifyTestCase(final Exam exam, final Exercise exercise, final TestCase testCase,
                                    final Exam.State state) {
        testManagerTestCase(exam, state, exercise, testCase,
                (manager, id) -> manager.modifyTestCase(
                        id,
                        TestHelper.validTestCaseVisibility(),
                        TestHelper.validTestCaseTimeout(),
                        TestHelper.validTestCaseList(),
                        TestHelper.validTestCaseList()
                ),
                "Modifying a test case that belongs to an exercise" +
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
        examActionVerification.accept(doThrow(IllegalEntityStateException.class).when(exam));
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManagerAction.accept(examManager, examId),
                message
        );
        examActionVerification.accept(verify(exam));
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
        when(exam.getState()).thenReturn(state);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
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
        when(exam.getState()).thenReturn(state);
        when(exercise.getExam()).thenReturn(exam);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManagerAction.accept(examManager, exerciseId),
                message
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
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
        when(exam.getState()).thenReturn(state);
        when(exercise.getExam()).thenReturn(exam);
        when(testCase.getExercise()).thenReturn(exercise);
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManagerAction.accept(examManager, testCaseId),
                message
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verify(testCase, only()).getExercise();
        verifyOnlyTestCaseSearch(testCaseId);
    }
}
