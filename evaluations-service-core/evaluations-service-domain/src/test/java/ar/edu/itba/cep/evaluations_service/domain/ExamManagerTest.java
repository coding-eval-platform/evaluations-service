package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ValidationConstants;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Test class for {@link ExamManager}.
 */
@ExtendWith(MockitoExtension.class)
class ExamManagerTest {

    /**
     * Amount of days (in a non leap year).
     */
    private final static int DAYS_IN_A_YEAR = 365;


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
     * Tests that searching for an {@link Exam} that exists returns the expected {@link Exam}.
     *
     * @param exam A mocked {@link Exam}.
     */
    @Test
    void testSearchForExamThatExists(@Mock final Exam exam) {
        final var id = validId();
        Mockito.when(exam.getId()).thenReturn(id);
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        final var examOptional = examManager.getExam(id);
        Assertions.assertAll("Searching for an exam that exists is not working as expected",
                () -> Assertions.assertTrue(
                        examOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        id,
                        examOptional.map(Exam::getId).get().longValue(),
                        "The returned Exam id's is not the same as the requested"
                )
        );
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that searching for an {@link Exam} that does not exist does not fail,
     * and returns an empty {@link Optional}.
     */
    @Test
    void testSearchForExamThatDoesNotExist() {
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertAll("Searching for an exam that does not exist is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> examManager.getExam(id),
                        "It throws an exception"
                ),
                () -> Assertions.assertTrue(
                        examManager.getExam(id).isEmpty(),
                        "The returned Optional is not empty."
                )
        );
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that an {@link Exam} is created (i.e is saved) when arguments are valid.
     */
    @Test
    void testExamIsCreatedUsingValidArguments() {
        final var description = validDescription();
        final var startingAt = validStartingMoment();
        final var duration = validDuration();
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
                        invalidDescription(),
                        invalidStartingAt(),
                        invalidDuration()
                ),
                "Creating an exam with invalid arguments is being allowed."
        );
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that an {@link Exam} is updated (i.e is saved) when arguments are valid.
     */
    @Test
    void testExamIsModifiedWithValidArguments() {
        final var exam = Mockito.spy(validExam());
        final var newDescription = validDescription();
        final var newStartingAt = validStartingMoment();
        final var newDuration = validDuration();
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertAll("Updating an exam with valid arguments does not work as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> examManager.modifyExam(id, newDescription, newStartingAt, newDuration),
                        "It throws an exception"
                ),
                () -> assertExamProperties(exam, newDescription, newStartingAt, newDuration)
        );
        Mockito.verify(examRepository, Mockito.atLeastOnce()).save(Mockito.any(Exam.class));
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
        final var id = validId();
        final var exam = Mockito.spy(validExam());
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> examManager.modifyExam(
                        id,
                        invalidDescription(),
                        invalidStartingAt(),
                        invalidDuration()
                ),
                "Creating an exam with invalid arguments is being allowed."
        );
        Mockito.verify(examRepository, Mockito.never()).save(Mockito.any(Exam.class));
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that trying to modify an {@link Exam} that does not exists throws a {@link NoSuchEntityException}.
     */
    @Test
    void testModifyNonExistenceExam() {
        assertThrowsNoSuchEntityException(
                examManager,
                validId(),
                (m, i) -> m.modifyExam(i, validDescription(), validStartingMoment(), validDuration()),
                examRepository,
                "Trying to modify an exam that does not exist does not throw a NoSuchEntityException"
        );
        Mockito.verify(examRepository, Mockito.never()).save(Mockito.any(Exam.class));
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that starting an upcoming {@link Exam} works as expected
     * (changes the state and then saves the exam instance).
     */
    @Test
    void testExamIsStartedWhenIsUpcoming() {
        final var exam = Mockito.spy(validExam());
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        examManager.startExam(id);
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
     * (throws a {@link IllegalEntityStateException}).
     */
    @Test
    void testExamIsNotStartedWhenIsInProgress() {
        final var exam = validExam();
        exam.startExam();
        final var spiedExam = Mockito.spy(exam);
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(spiedExam));
        try {
            examManager.startExam(id);
        } catch (final IllegalEntityStateException ignored) {
        } catch (final Throwable e) {
            Assertions.fail("The exception thrown is not an IllegalEntityStateException");
        }

        Mockito.verify(spiedExam, Mockito.times(1)).startExam();
        Mockito.verify(examRepository, Mockito.never()).save(spiedExam);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that starting a finished {@link Exam} works as expected
     * (throws a {@link IllegalEntityStateException}).
     */
    @Test
    void testExamIsNotStartedWhenIsFinished() {
        final var exam = validExam();
        exam.startExam();
        exam.finishExam();
        final var spiedExam = Mockito.spy(exam);
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(spiedExam));
        try {
            examManager.startExam(id);
        } catch (final IllegalEntityStateException ignored) {
        } catch (final Throwable e) {
            Assertions.fail("The exception thrown is not an IllegalEntityStateException");
        }
        Mockito.verify(spiedExam, Mockito.times(1)).startExam();
        Mockito.verify(examRepository, Mockito.never()).save(spiedExam);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that trying to start an {@link Exam} that does not exists throws a {@link NoSuchEntityException}.
     */
    @Test
    void testStartNonExistenceExam() {
        assertThrowsNoSuchEntityException(
                examManager,
                validId(),
                ExamManager::startExam,
                examRepository,
                "Trying to start an exam that does not exist does not throw a NoSuchEntityException"
        );
        Mockito.verify(examRepository, Mockito.never()).save(Mockito.any(Exam.class));
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that finishing an in progress {@link Exam} works as expected
     * (changes the state and then saves the exam instance).
     */
    @Test
    void testExamIsFinishedWhenIsInProgress() {
        final var exam = validExam();
        exam.startExam();
        final var spiedExam = Mockito.spy(exam);
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(spiedExam));
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        examManager.finishExam(id);
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
     * (throws a {@link IllegalEntityStateException}).
     */
    @Test
    void testExamIsNotFinishedWhenIsUpcoming() {
        final var exam = validExam();
        final var spiedExam = Mockito.spy(exam);
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(spiedExam));
        try {
            examManager.finishExam(id);
        } catch (final IllegalEntityStateException ignored) {
        } catch (final Throwable e) {
            Assertions.fail("The exception thrown is not an IllegalEntityStateException");
        }

        Mockito.verify(spiedExam, Mockito.times(1)).finishExam();
        Mockito.verify(examRepository, Mockito.never()).save(spiedExam);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that starting a finished {@link Exam} works as expected
     * (throws a {@link IllegalEntityStateException}).
     */
    @Test
    void testExamIsNotFinishedWhenIsFinished() {
        final var exam = validExam();
        exam.startExam();
        exam.finishExam();
        final var spiedExam = Mockito.spy(exam);
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(spiedExam));
        try {
            examManager.finishExam(id);
        } catch (final IllegalEntityStateException ignored) {
        } catch (final Throwable e) {
            Assertions.fail("The exception thrown is not an IllegalEntityStateException");
        }
        Mockito.verify(spiedExam, Mockito.times(1)).finishExam();
        Mockito.verify(examRepository, Mockito.never()).save(spiedExam);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that trying to finish an {@link Exam} that does not exists throws a {@link NoSuchEntityException}.
     */
    @Test
    void testFinishNonExistenceExam() {
        assertThrowsNoSuchEntityException(
                examManager,
                validId(),
                ExamManager::finishExam,
                examRepository,
                "Trying to finish an exam that does not exist does not throw a NoSuchEntityException"
        );
        Mockito.verify(examRepository, Mockito.never()).save(Mockito.any(Exam.class));
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that deleting an upcoming exam is performed as expected.
     *
     * @param exam A mocked {@link Exam}.
     */
    @Test
    void testDeleteOfUpcomingExam(@Mock final Exam exam) {
        final var id = validId();
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
     *
     * @param exam A mocked {@link Exam}.
     */
    @Test
    void testDeleteOfInProgressExam(@Mock final Exam exam) {
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManager.deleteExam(id),
                "Deleting an in progress exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(id);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that deleting a finished exam is not allowed.
     *
     * @param exam A mocked {@link Exam}
     */
    @Test
    void testDeleteOfFinishedExam(@Mock final Exam exam) {
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getState()).thenReturn(Exam.State.FINISHED);
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManager.deleteExam(id),
                "Deleting a finished exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(id);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that trying to delete an {@link Exam} that does not not exists throws a {@link NoSuchEntityException}.
     */
    @Test
    void testDeleteNonExistenceExam() {
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertDoesNotThrow(
                () -> examManager.deleteExam(id),
                "Trying to delete an exam that does not exist throws an exception"
        );
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

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
        final var id = validId();
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

    /**
     * Tests that trying to get {@link Exercise}s belonging to an {@link Exam} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testGetExercisesOfNonExistenceExam() {
        assertThrowsNoSuchEntityException(
                examManager,
                validId(),
                ExamManager::getExercises,
                examRepository,
                "Trying to get exercises" +
                        " belonging to an exam that does not exist does not throw a NoSuchEntityException"
        );
        Mockito.verify(examRepository, Mockito.never()).save(Mockito.any(Exam.class));
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    // TODO: more testing for clear exercises

    /**
     * Tests that clearing an upcoming exam exercises is performed as expected.
     *
     * @param exam A mocked {@link Exam}.
     */
    @Test
    void testClearExercisesOfUpcomingExam(@Mock final Exam exam) {
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Assertions.assertDoesNotThrow(
                () -> examManager.clearExercises(id),
                "Clearing exam's exercises throws an exception"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(id);
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
     *
     * @param exam A mocked {@link Exam}.
     */
    @Test
    void testClearExercisesOfInProgressExam(@Mock final Exam exam) {
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManager.clearExercises(id),
                "Clearing exercises of an in progress exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(id);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that clearing exercises of a finished exam is not allowed.
     *
     * @param exam A mocked {@link Exam}
     */
    @Test
    void testClearExercisesOfFinishedExam(@Mock final Exam exam) {
        final var id = validId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getState()).thenReturn(Exam.State.FINISHED);
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> examManager.clearExercises(id),
                "Clearing exercises of a finished exam is being allowed"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(id);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that trying to clear {@link Exercise}s belonging to an {@link Exam} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testClearExercisesOfNonExistenceExam() {
        assertThrowsNoSuchEntityException(
                examManager,
                validId(),
                ExamManager::clearExercises,
                examRepository,
                "Trying to clear exercises" +
                        " belonging to  an exam that does not exist does not throw a NoSuchEntityException"
        );
        Mockito.verify(examRepository, Mockito.never()).save(Mockito.any(Exam.class));
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================


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
     * throws an {@link NoSuchEntityException}.
     *
     * @param examManager       The {@link ExamManager} to be asserted.
     * @param id                The hypothetical id of the non existence {@link Exam}.
     * @param examManagerAction The action to be performed over the {@code examManager}, using the given {@code id}.
     * @param examRepository    The {@link ExamRepository} used to query the existence of the {@link Exam}.
     * @param message           The message to be displayed in case of assertion failure.
     */
    private static void assertThrowsNoSuchEntityException(
            final ExamManager examManager,
            final long id,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final ExamRepository examRepository,
            final String message) {
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> examManagerAction.accept(examManager, id),
                message
        );
    }


    // ========================================
    // Valid values
    // ========================================

    private static Exam validExam() {
        return new Exam(
                validDescription(),
                validStartingMoment(),
                validDuration()
        );
    }

    /**
     * @return A valid id.
     */
    private static long validId() {
        return Faker.instance().number().numberBetween(1L, Long.MAX_VALUE);
    }

    /**
     * @return A random description whose length is between the valid limits.
     */
    private static String validDescription() {
        return Faker.instance()
                .lorem()
                .characters(ValidationConstants.DESCRIPTION_MIN_LENGTH, ValidationConstants.DESCRIPTION_MAX_LENGTH);
    }

    /**
     * @return A random {@link LocalDateTime} in the future (between tomorrow and next year).
     */
    private static LocalDateTime validStartingMoment() {
        final var nextDayInstant = Instant.now().plus(Duration.ofDays(1));
        return Faker.instance()
                .date()
                .future(DAYS_IN_A_YEAR, TimeUnit.DAYS, Date.from(nextDayInstant))
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                ;
    }

    /**
     * @return A random {@link Duration} between 15 minutes and 3 hours.
     */
    private static Duration validDuration() {
        return Duration.ofMinutes(Faker.instance().number().numberBetween(15L, 240L));
    }


    // ========================================
    // Invalid values
    // ========================================

    /**
     * @return An invalid description.
     */
    private static String invalidDescription() {
        final var possibleValues = new LinkedList<String>();
        // Add a null value
        possibleValues.add(null);
        // Add a long description
        possibleValues.add(Faker.instance()
                .lorem()
                .fixedString(ValidationConstants.DESCRIPTION_MAX_LENGTH + 1));
        // Add a short description
        if (ValidationConstants.DESCRIPTION_MIN_LENGTH > 0) {
            possibleValues.add(
                    Faker.instance()
                            .lorem()
                            .fixedString(ValidationConstants.DESCRIPTION_MIN_LENGTH - 1)
            );
        }
        final var index = Faker.instance()
                .number()
                .numberBetween(0, possibleValues.size());
        return possibleValues.get(index);
    }


    /**
     * @return An invalid {@link LocalDateTime} starting moment.
     */
    private static LocalDateTime invalidStartingAt() {
        final var possibleValues = new LinkedList<LocalDateTime>();
        // Add a null value
        possibleValues.add(null);
        // Add a past date
        final var previousDayInstant = Instant.now().minus(Duration.ofDays(1));
        possibleValues.add(
                Faker.instance()
                        .date()
                        .past(DAYS_IN_A_YEAR, TimeUnit.DAYS, Date.from(previousDayInstant))
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
        final var index = Faker.instance()
                .number()
                .numberBetween(0, possibleValues.size());
        return possibleValues.get(index);
    }

    /**
     * @return An invalid duration.
     */
    private static Duration invalidDuration() {
        return null;
    }
}
