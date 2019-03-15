package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Test class for {@link ExamManager}.
 */
@ExtendWith(MockitoExtension.class)
class ExamManagerTest {

    // ================================================================================================================
    // Mocks
    // ================================================================================================================

    /**
     * A mocked {@link ExamRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final ExamRepository examRepository;
    /**
     * A mocked {@link ExerciseRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final ExerciseRepository exerciseRepository;
    /**
     * A mocked {@link TestCaseRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final TestCaseRepository testCaseRepository;
    /**
     * A mocked {@link ExamRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final ExerciseSolutionRepository exerciseSolutionRepository;
    /**
     * A mocked {@link ExerciseSolutionResultRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final ExerciseSolutionResultRepository exerciseSolutionResultRepository;


    // ================================================================================================================
    // Exam Manager
    // ================================================================================================================

    /**
     * The {@link ExamManager} being tested.
     */
    private final ExamManager examManager;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param examRepository                   A mocked {@link ExamRepository}
     *                                         that is injected to the {@link ExamManager}.
     * @param exerciseRepository               A mocked {@link ExerciseRepository}
     *                                         that is injected to the {@link ExamManager}.
     * @param testCaseRepository               A mocked {@link TestCaseRepository}
     *                                         that is injected to the {@link ExamManager}.
     * @param exerciseSolutionRepository       A mocked {@link ExamRepository}
     *                                         that is injected to the {@link ExamManager}.
     * @param exerciseSolutionResultRepository A mocked {@link ExerciseSolutionResultRepository}
     *                                         that is injected to the {@link ExamManager}.
     */
    ExamManagerTest(
            @Mock final ExamRepository examRepository,
            @Mock final ExerciseRepository exerciseRepository,
            @Mock final TestCaseRepository testCaseRepository,
            @Mock final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock final ExerciseSolutionResultRepository exerciseSolutionResultRepository) {
        this.examRepository = examRepository;
        this.exerciseRepository = exerciseRepository;
        this.testCaseRepository = testCaseRepository;
        this.exerciseSolutionRepository = exerciseSolutionRepository;
        this.exerciseSolutionResultRepository = exerciseSolutionResultRepository;
        this.examManager = new ExamManager(
                examRepository,
                exerciseRepository,
                testCaseRepository,
                exerciseSolutionRepository,
                exerciseSolutionResultRepository
        );
    }


    // ================================================================================================================
    // Exams
    // ================================================================================================================

    /**
     * Tests that an {@link Exam} is not created (i.e {@link ExamRepository#save(Object)} is not called)
     * when arguments are not valid.
     */
    @Test
    void testExamCreationWithInvalidArguments() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.createExam(
                        TestHelper.invalidExamDescription(),
                        TestHelper.invalidExamStartingAt(),
                        TestHelper.invalidExamDuration()
                ),
                "Creating an exam with invalid arguments is being allowed."
        );
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================
    // Modify exam
    // ================================

    /**
     * Tests that an {@link Exam} is not updated (i.e {@link ExamRepository#save(Object)} is not called)
     * when arguments are not valid.
     */
    @Test
    void testExamModificationWithInvalidArguments() {
        final var examId = TestHelper.validExamId();
        final var exam = Mockito.spy(TestHelper.validExam());
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.modifyExam(
                        examId,
                        TestHelper.invalidExamDescription(),
                        TestHelper.invalidExamStartingAt(),
                        TestHelper.invalidExamDuration()
                ),
                "Creating an exam with invalid arguments is being allowed."
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    // TODO: test modification with in progress and finished


    // ================================
    // Start exam
    // ================================

    /**
     * Tests that starting an in progress {@link Exam} works as expected
     * (throws an {@link IllegalEntityStateException}).
     */
    @Test
    void testExamIsNotStartedWhenIsInProgress() {
        final var examId = TestHelper.validExamId();
        final var exam = TestHelper.validExam();
        exam.startExam();
        final var spiedExam = Mockito.spy(exam);
        assertIllegalExamState(
                examManager,
                examId,
                ExamManager::startExam,
                spiedExam,
                examRepository,
                "Starting an in progress exam is being allowed"
        );
        Mockito.verify(spiedExam, Mockito.times(1)).startExam();
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that starting a finished {@link Exam} works as expected
     * (throws an {@link IllegalEntityStateException}).
     */
    @Test
    void testExamIsNotStartedWhenIsFinished() {
        final var examId = TestHelper.validExamId();
        final var exam = TestHelper.validExam();
        exam.startExam();
        exam.finishExam();
        final var spiedExam = Mockito.spy(exam);
        assertIllegalExamState(
                examManager,
                examId,
                ExamManager::startExam,
                spiedExam,
                examRepository,
                "Starting a finished exam is being allowed"
        );
        Mockito.verify(spiedExam, Mockito.times(1)).startExam();
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================
    // Finish exam
    // ================================

    /**
     * Tests that starting an in progress {@link Exam} works as expected
     * (throws an {@link IllegalEntityStateException}).
     */
    @Test
    void testExamIsNotFinishedWhenIsUpcoming() {
        final var examId = TestHelper.validExamId();
        final var exam = TestHelper.validExam();
        final var spiedExam = Mockito.spy(exam);
        assertIllegalExamState(
                examManager,
                examId,
                ExamManager::finishExam,
                spiedExam,
                examRepository,
                "Finishing an upcoming exam is being allowed"
        );
        Mockito.verify(spiedExam, Mockito.times(1)).finishExam();
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that starting a finished {@link Exam} works as expected
     * (throws an {@link IllegalEntityStateException}).
     */
    @Test
    void testExamIsNotFinishedWhenIsFinished() {
        final var examId = TestHelper.validExamId();
        final var exam = TestHelper.validExam();
        exam.startExam();
        exam.finishExam();
        final var spiedExam = Mockito.spy(exam);
        assertIllegalExamState(
                examManager,
                examId,
                ExamManager::finishExam,
                spiedExam,
                examRepository,
                "Finishing a finished exam is being allowed"
        );
        Mockito.verify(spiedExam, Mockito.times(1)).finishExam();
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================
    // Delete exam
    // ================================

    /**
     * Tests that deleting an in progress exam is not allowed.
     */
    @Test
    void testDeleteOfInProgressExam() {
        final var examId = TestHelper.validExamId();
        assertIllegalExamState(
                examManager,
                examId,
                ExamManager::deleteExam,
                Exam.State.IN_PROGRESS,
                examRepository,
                "Deleting an in progress exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that deleting a finished exam is not allowed.
     */
    @Test
    void testDeleteOfFinishedExam() {
        final var examId = TestHelper.validExamId();
        assertIllegalExamState(
                examManager,
                examId,
                ExamManager::deleteExam,
                Exam.State.FINISHED,
                examRepository,
                "Deleting a finished exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================
    // Get exam exercises
    // ================================


    // ================================
    // Clear exam exercises
    // ================================

    /**
     * Tests that clearing exercises of an in progress exam is not allowed.
     */
    @Test
    void testClearExercisesOfInProgressExam() {
        final var examId = TestHelper.validExamId();
        assertIllegalExamState(
                examManager,
                examId,
                ExamManager::clearExercises,
                Exam.State.IN_PROGRESS,
                examRepository,
                "Clearing exercises of an in progress exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that clearing exercises of a finished exam is not allowed.
     */
    @Test
    void testClearExercisesOfFinishedExam() {
        final var examId = TestHelper.validExamId();
        assertIllegalExamState(
                examManager,
                examId,
                ExamManager::deleteExam,
                Exam.State.FINISHED,
                examRepository,
                "Clearing exercises of a finished exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    // ================================
    // Create exercise
    // ================================

    @Test
    void testCreateExerciseForInProgressExam() {
        final var examId = TestHelper.validExamId();
        assertIllegalExamState(
                examManager,
                examId,
                (manager, id) -> manager.createExercise(id, TestHelper.validExerciseQuestion()),
                Exam.State.IN_PROGRESS,
                examRepository,
                "Creating an exercise for an in progress exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    @Test
    void testCreateExerciseForFinishedExam() {
        final var examId = TestHelper.validExamId();
        assertIllegalExamState(
                examManager,
                examId,
                (manager, id) -> manager.createExercise(id, TestHelper.validExerciseQuestion()),
                Exam.State.FINISHED,
                examRepository,
                "Creating an exercise for a finished exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Custom assertions
    // ========================================

    /**
     * Asserts that executing the given {@code examManagerAction} using the given {@code examManager} and {@code id}
     * throws an {@link IllegalEntityStateException} because the given {@code state} is not valid for executing
     * the said action.
     *
     * @param examManager       The {@link ExamManager} to be asserted.
     * @param id                The hypothetical id of the non existence {@link Exam}.
     * @param examManagerAction The action to be performed over the {@code examManager}, using the given {@code id}.
     * @param state             The {@link Exam.State} to be tested.
     * @param examRepository    The {@link ExamRepository} used to query the existence of the {@link Exam}.
     * @param message           The message to be displayed in case of assertion failure.
     */
    private static void assertIllegalExamState(
            final ExamManager examManager,
            final long id,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final Exam.State state,
            final ExamRepository examRepository,
            final String message) {
        final var exam = Mockito.mock(Exam.class);
        Mockito.when(exam.getState()).thenReturn(state);
        assertIllegalExamState(examManager, id, examManagerAction, exam, examRepository, message);
    }

    /**
     * Asserts that executing the given {@code examManagerAction} using the given {@code examManager} and {@code id}
     * throws an {@link IllegalEntityStateException} because the given {@code exam} is not in a valid state
     * for executing the said action.
     *
     * @param examManager       The {@link ExamManager} to be asserted.
     * @param id                The hypothetical id of the non existence {@link Exam}.
     * @param examManagerAction The action to be performed over the {@code examManager}, using the given {@code id}.
     * @param exam              The {@link Exam} being affected by the {@code examManagerAction}.
     * @param examRepository    The {@link ExamRepository} used to query the existence of the {@link Exam}.
     * @param message           The message to be displayed in case of assertion failure.
     */
    private static void assertIllegalExamState(
            final ExamManager examManager,
            final long id,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final Exam exam,
            final ExamRepository examRepository,
            final String message) {
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManagerAction.accept(examManager, id),
                message
        );
    }
}
