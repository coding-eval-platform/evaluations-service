package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ValidationConstants;
import ar.edu.itba.cep.evaluations_service.repositories.*;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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


    /**
     * Tests that searching for an {@link Exam} that exists returns the expected {@link Exam}.
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
                () -> {
                    final var exam = examManager.createExam(description, startingAt, duration);
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
        );
        Mockito.verify(examRepository, Mockito.atLeastOnce()).save(Mockito.any(Exam.class));
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ========================================
    // Valid values
    // ========================================

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
}
