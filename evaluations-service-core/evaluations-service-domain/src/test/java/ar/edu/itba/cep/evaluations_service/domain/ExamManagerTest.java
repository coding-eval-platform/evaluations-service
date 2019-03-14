package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
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
import java.util.List;
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

    // ================================
    // Get exam
    // ================================

    /**
     * Tests that searching for an {@link Exam} that exists returns the expected {@link Exam}.
     *
     * @param exam A mocked {@link Exam}.
     */
    @Test
    void testSearchForExamThatExists(@Mock final Exam exam) {
        final var examId = TestHelper.validExamId();
        Mockito.when(exam.getId()).thenReturn(examId);
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        final var examOptional = examManager.getExam(examId);
        Assertions.assertAll("Searching for an exam that exists is not working as expected",
                () -> Assertions.assertTrue(
                        examOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        examId,
                        examOptional.map(Exam::getId).get().longValue(),
                        "The returned Exam id's is not the same as the requested"
                )
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================
    // Create Exam
    // ================================

    /**
     * Tests that an {@link Exam} is created (i.e is saved) when arguments are valid.
     */
    @Test
    void testExamIsCreatedUsingValidArguments() {
        final var description = TestHelper.validExamDescription();
        final var startingAt = TestHelper.validExamStartingMoment();
        final var duration = TestHelper.validExamDuration();
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertAll("Creating an exam with valid arguments does not work as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> examManager.createExam(description, startingAt, duration),
                        "It throws an exception"
                ),
                () -> assertExamProperties(
                        examManager.createExam(description, startingAt, duration),
                        description,
                        startingAt,
                        duration
                )
        );
        Mockito.verify(examRepository, Mockito.atLeastOnce()).save(Mockito.any(Exam.class));
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

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
     * Tests that an {@link Exam} is updated (i.e is saved) when arguments are valid.
     */
    @Test
    void testExamIsModifiedWithValidArguments() {
        final var exam = Mockito.spy(TestHelper.validExam());
        final var newDescription = TestHelper.validExamDescription();
        final var newStartingAt = TestHelper.validExamStartingMoment();
        final var newDuration = TestHelper.validExamDuration();
        final var examId = TestHelper.validExamId();
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertAll("Updating an exam with valid arguments does not work as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> examManager.modifyExam(examId, newDescription, newStartingAt, newDuration),
                        "It throws an exception"
                ),
                () -> assertExamProperties(exam, newDescription, newStartingAt, newDuration)
        );
        Mockito.verify(examRepository, Mockito.atLeastOnce()).findById(examId);
        Mockito.verify(examRepository, Mockito.atLeastOnce()).save(Mockito.any(Exam.class));
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

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
     * Tests that starting an upcoming {@link Exam} works as expected
     * (changes the state and then saves the exam instance).
     */
    @Test
    void testExamIsStartedWhenIsUpcoming() {
        final var exam = Mockito.spy(TestHelper.validExam());
        final var examId = TestHelper.validExamId();
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        examManager.startExam(examId);
        Assertions.assertSame(
                Exam.State.IN_PROGRESS,
                exam.getState(),
                "Change of state to \"IN_PROGRESS\" is not being performed"
        );
        Mockito.verify(exam, Mockito.times(1)).startExam();
        Mockito.verify(examRepository, Mockito.atLeastOnce()).save(exam);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


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
     * Tests that finishing an in progress {@link Exam} works as expected
     * (changes the state and then saves the exam instance).
     */
    @Test
    void testExamIsFinishedWhenIsInProgress() {
        final var exam = TestHelper.validExam();
        exam.startExam();
        final var spiedExam = Mockito.spy(exam);
        final var examId = TestHelper.validExamId();
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(spiedExam));
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        examManager.finishExam(examId);
        Assertions.assertSame(
                Exam.State.FINISHED,
                spiedExam.getState(),
                "Change of state to \"FINISHED\" is not being performed"
        );
        Mockito.verify(spiedExam, Mockito.times(1)).finishExam();
        Mockito.verify(examRepository, Mockito.atLeastOnce()).save(spiedExam);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

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
     * Tests that deleting an upcoming exam is performed as expected.
     *
     * @param exam A mocked {@link Exam}.
     */
    @Test
    void testDeleteOfUpcomingExam(@Mock final Exam exam) {
        final var id = TestHelper.validExamId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Assertions.assertDoesNotThrow(
                () -> examManager.deleteExam(id),
                "Deleting an exam throws an exception"
        );
        Mockito.verify(examRepository, Mockito.atLeastOnce()).findById(id);
        Mockito.verify(examRepository, Mockito.times(1)).delete(exam);
        Mockito.verify(exerciseRepository, Mockito.times(1)).deleteExamExercises(exam);
        Mockito.verify(testCaseRepository, Mockito.times(1)).deleteExamTestCases(exam);
        Mockito.verifyNoMoreInteractions(
                examRepository,
                exerciseRepository,
                testCaseRepository
        );
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

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

    /**
     * Tests that the {@link List} of {@link Exercise}s belonging to a given {@link Exam} is returned as expected.
     *
     * @param exam            A mocked {@link Exam} (the owner of the {@link Exercise}s).
     * @param mockedExercises A mocked {@link List} of {@link Exercise}s owned by the {@link Exam}.
     */
    @Test
    void testGetExamExercises(
            @Mock final Exam exam,
            @Mock final List<Exercise> mockedExercises) {
        final var id = TestHelper.validExamId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exerciseRepository.getExamExercises(exam)).thenReturn(mockedExercises);
        final var exercises = examManager.getExercises(id);
        Assertions.assertEquals(
                mockedExercises,
                exercises,
                "The returned exercises list is not the one returned by the repository"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(id);
        Mockito.verify(exerciseRepository, Mockito.only()).getExamExercises(exam);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================
    // Clear exam exercises
    // ================================

    /**
     * Tests that clearing an upcoming exam exercises is performed as expected.
     *
     * @param exam A mocked {@link Exam}.
     */
    @Test
    void testClearExercisesOfUpcomingExam(@Mock final Exam exam) {
        final var examId = TestHelper.validExamId();
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Assertions.assertDoesNotThrow(
                () -> examManager.clearExercises(examId),
                "Clearing exam's exercises throws an exception"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verify(exerciseRepository, Mockito.times(1)).deleteExamExercises(exam);
        Mockito.verify(testCaseRepository, Mockito.times(1)).deleteExamTestCases(exam);
        Mockito.verifyNoMoreInteractions(
                examRepository,
                exerciseRepository,
                testCaseRepository
        );
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

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

    void testCreateExerciseForUpcomingExam() {
        // TODO: implement
    }

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
     * Asserts that the given {@code exam} properties are equal as the given
     * {@code description}, {@code startingAt} and {@code duration}.
     *
     * @param exam        The {@link Exam} to be asserted.
     * @param description The expected description.
     * @param startingAt  The expected {@link LocalDateTime} starting moment.
     * @param duration    The expected duration.
     */
    private static void assertExamProperties(final Exam exam,
                                             final String description,
                                             final LocalDateTime startingAt,
                                             final Duration duration) {
        Assertions.assertAll("Exam properties are not the expected",
                () -> Assertions.assertEquals(
                        description,
                        exam.getDescription(),
                        "There is a mismatch in the description"
                ),
                () -> Assertions.assertEquals(
                        startingAt,
                        exam.getStartingAt(),
                        "There is a mismatch in the starting moment"
                ),
                () -> Assertions.assertEquals(
                        duration,
                        exam.getDuration(),
                        "There is a mismatch in the duration"
                )
        );
    }

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
