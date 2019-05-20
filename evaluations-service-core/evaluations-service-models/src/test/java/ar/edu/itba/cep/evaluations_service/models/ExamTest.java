package ar.edu.itba.cep.evaluations_service.models;

import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Test class for {@link Exam}s
 */
class ExamTest {

    /**
     * Amount of days (in a non leap year).
     */
    private final static int DAYS_IN_A_YEAR = 365;


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests that creating an {@link Exam} with valid values can be performed without any exception being thrown.
     */
    @Test
    void testAcceptableArguments() {
        Assertions.assertDoesNotThrow(ExamTest::createExam,
                "An exam is not being created with acceptable arguments.");
    }

    /**
     * Tests that updating an {@link Exam} with valid values works as expected.
     */
    @Test
    void testValidArgumentsUpdate() {
        Assertions.assertAll("Updating with acceptable arguments is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExam().update(validDescription(), validStartingMoment(), validDuration()),
                        "It throws an exception"
                ),
                () -> {
                    final var exam = createExam();
                    final var description = validDescription();
                    final var startingAt = validStartingMoment();
                    final var duration = validDuration();
                    exam.update(description, startingAt, duration);
                    Assertions.assertAll("Is not being set (does not change the Exam value)",
                            () -> Assertions.assertEquals(
                                    description,
                                    exam.getDescription(),
                                    "Description mismatch"),
                            () -> Assertions.assertEquals(
                                    startingAt,
                                    exam.getStartingAt(),
                                    "Starting moment mismatch"),
                            () -> Assertions.assertEquals(
                                    duration,
                                    exam.getDuration(),
                                    "Duration mismatch")
                    );
                }
        );
    }

    /**
     * Tests that setting a valid description to an {@link Exam} works as expected.
     */
    @Test
    void testSetValidDescription() {
        Assertions.assertAll("Setting a valid description is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExam().setDescription(validDescription()),
                        "It throws an exception"
                ),
                () -> {
                    final var exam = createExam();
                    final var description = validDescription();
                    exam.setDescription(description);
                    Assertions.assertEquals(
                            description,
                            exam.getDescription(),
                            "Is not being set (does not change the Exam value)"
                    );
                }
        );
    }

    /**
     * Tests that setting a valid starting at moment to an {@link Exam} works as expected.
     */
    @Test
    void testSetValidStartingAt() {
        Assertions.assertAll("Setting a valid starting at moment is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExam().setStartingAt(validStartingMoment()),
                        "It throws an exception"
                ),
                () -> {
                    final var exam = createExam();
                    final var startingAt = validStartingMoment();
                    exam.setStartingAt(startingAt);
                    Assertions.assertEquals(
                            startingAt,
                            exam.getStartingAt(),
                            "Is not being set (does not change the Exam value)"
                    );
                }
        );
    }

    /**
     * Tests that setting a valid duration to an {@link Exam} works as expected.
     */
    @Test
    void testSetValidDuration() {
        Assertions.assertAll("Setting a valid duration is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExam().setDuration(validDuration()),
                        "It throws an exception"
                ),
                () -> {
                    final var exam = createExam();
                    final var duration = validDuration();
                    exam.setDuration(duration);
                    Assertions.assertEquals(
                            duration,
                            exam.getDuration(),
                            "Is not being set (does not change the Exam value)"
                    );
                }
        );
    }


    // ================================================================================================================
    // Behaviour testing
    // ================================================================================================================

    /**
     * Tests internal values of an {@link Exam} when it is created (and no change of state occurs).
     */
    @Test
    void testNewExamValues() {
        final var exam = createExam();
        Assertions.assertAll("Exam is not being created as expected",
                () -> Assertions.assertSame(Exam.State.UPCOMING, exam.getState(), "Not in UPCOMING state"),
                () -> Assertions.assertNull(exam.getActualStartingMoment(), "Not null actual starting moment"),
                () -> Assertions.assertNull(exam.getActualDuration(), "Not null actual duration")
        );
    }

    /**
     * Tests that an {@link Exam} can be modified when it is created (and no change of state occurs).
     */
    @Test
    void testNewExamModificationsBehaviour() {
        final var exam = createExam();
        Assertions.assertAll("Exams are not being able to be modified in UPCOMING state",
                () -> Assertions.assertDoesNotThrow(
                        () -> exam.setDescription(validDescription()),
                        "Cannot change description"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> exam.setStartingAt(validStartingMoment()),
                        "Cannot change startingAt"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> exam.setDuration(validDuration()),
                        "Cannot change duration"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> exam.update(validDescription(), validStartingMoment(), validDuration()),
                        "Cannot perform complete update"
                )
        );
    }

    /**
     * Tests change of state behaviour when an {@link Exam} is created
     * (and no change of state occurs, so it is in {@link Exam.State#UPCOMING} state).
     */
    @Test
    void testChangeOfStateWhenUpcoming() {
        Assertions.assertAll("Change of states is not working as expected when state is UPCOMING",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExam().startExam(),
                        "Starting an upcoming exam is throwing an exception"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> createExam().finishExam(),
                        "Finishing an upcoming exam is being allowed"
                )
        );
    }

    /**
     * Tests internal values of an {@link Exam} that has being started
     * (i.e its state is {@link Exam.State#IN_PROGRESS}).
     */
    @Test
    void testInProgressExamValues() {
        final var exam = createExam();
        final var description = exam.getDescription();
        final var startingAt = exam.getStartingAt();
        final var duration = exam.getDuration();
        exam.startExam();
        Assertions.assertAll("Starting an upcoming Exam is not working as expected",
                () -> Assertions.assertEquals(description, exam.getDescription(), "The Description is changed"),
                () -> Assertions.assertEquals(startingAt, exam.getStartingAt(), "The startingAt value is changed"),
                () -> Assertions.assertEquals(duration, exam.getDuration(), "Duration is changed"),
                () -> Assertions.assertSame(Exam.State.IN_PROGRESS, exam.getState(), "Not in IN_PROGRESS state"),
                () -> Assertions.assertNotNull(exam.getActualStartingMoment(), "Null actual starting moment"),
                () -> Assertions.assertNull(exam.getActualDuration(), "Not null actual duration")
        );
    }

    /**
     * Tests that an {@link Exam} cannot be modified when it has started
     * (i.e its state is {@link Exam.State#IN_PROGRESS}).
     */
    @Test
    void testInProgressExamModificationsBehaviour() {
        final var exam = createExam();
        exam.startExam();
        Assertions.assertAll("Exams are being allowed to be modified in IN_PROGRESS state",
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> exam.setDescription(validDescription()),
                        "Change of description is being allowed"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> exam.setStartingAt(validStartingMoment()),
                        "Change of startingAt is being allowed"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> exam.setDuration(validDuration()),
                        "Change of duration is being allowed"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> exam.update(validDescription(), validStartingMoment(), validDuration()),
                        "Complete update is being allowed"
                )
        );
    }

    /**
     * Tests change of state behaviour when an {@link Exam} is started
     * (i.e its state is {@link Exam.State#IN_PROGRESS}))
     */
    @Test
    void testChangeOfStateWhenInProgress() {
        Assertions.assertAll("Change of states is not working as expected when state is IN_PROGRESS",
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> {
                            final var exam = createExam();
                            exam.startExam();
                            exam.startExam();
                        },
                        "Starting an in progress exam is being allowed"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> {
                            final var exam = createExam();
                            exam.startExam();
                            exam.finishExam();
                        },
                        "Finishing an in progress exam is throwing an exception"
                )
        );
    }

    /**
     * Tests internal values of an {@link Exam} that has finished
     * (i.e its state is {@link Exam.State#FINISHED}).
     */
    @Test
    void testFinishedExamValues() {
        final var exam = createExam();
        exam.startExam();
        exam.finishExam();
        Assertions.assertAll("Finishing an Exam is not producing the desirable effect",
                () -> Assertions.assertSame(Exam.State.FINISHED, exam.getState(), "Not in FINISHED state"),
                () -> Assertions.assertNotNull(exam.getActualStartingMoment(), "Null actual starting moment"),
                () -> Assertions.assertNotNull(exam.getActualDuration(), "Null actual duration")
        );
    }

    /**
     * Tests that an {@link Exam} cannot be modified when it has finished
     * (i.e its state is {@link Exam.State#FINISHED}).
     */
    @Test
    void testFinishedExamModificationsBehaviour() {
        final var exam = createExam();
        exam.startExam();
        exam.finishExam();
        Assertions.assertAll("Exams are being allowed to be modified in FINISHED state",
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> exam.setDescription(validDescription()),
                        "Change of description is being allowed"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> exam.setStartingAt(validStartingMoment()),
                        "Change of startingAt is being allowed"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> exam.setDuration(validDuration()),
                        "Change of duration is being allowed"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> exam.update(validDescription(), validStartingMoment(), validDuration()),
                        "Complete update is being allowed"
                )
        );
    }

    /**
     * Tests change of state behaviour when an {@link Exam} has finished
     * (i.e its state is {@link Exam.State#FINISHED}).
     */
    @Test
    void testChangeOfStateWhenFinished() {
        Assertions.assertAll("Change of states is not working as expected when state is FINISHED",
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> {
                            final var exam = createExam();
                            exam.startExam();
                            exam.finishExam();
                            exam.startExam();
                        },
                        "Starting a finished exam is being allowed"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> {
                            final var exam = createExam();
                            exam.startExam();
                            exam.finishExam();
                            exam.finishExam();
                        },
                        "Finishing a finished exam is being allowed"
                )
        );
    }


    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    // ================================
    // Creation
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exam} with a null description.
     */
    @Test
    void testNullDescriptionOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exam(null, validStartingMoment(), validDuration()),
                "Creating an exam with a null description is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exam} with a too short description.
     */
    @Test
    void testShortDescriptionOnCreation() {
        shortDescription().ifPresent(
                shortDescription -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new Exam(shortDescription, validStartingMoment(), validDuration()),
                        "Creating an exam with a too short description is being allowed."
                )
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exam} with a too long description.
     */
    @Test
    void testLongDescriptionOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exam(longDescription(), validStartingMoment(), validDuration()),
                "Creating an exam with a too long description is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exam} with a null starting at {@link LocalDateTime}.
     */
    @Test
    void testNullStartingAtOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exam(validDescription(), null, validDuration()),
                "Creating an exam with a null starting at local date time is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exam} with a past staring at {@link LocalDateTime}.
     */
    @Test
    void testPastStartingAtOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exam(validDescription(), pastStartingMoment(), validDuration()),
                "Creating an exam with a past starting at local date time is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exam} with a null duration.
     */
    @Test
    void testNullDurationOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exam(validDescription(), validStartingMoment(), null),
                "Creating an exam with a null duration is being allowed."
        );
    }


    // ================================
    // Update
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exam} with a null description.
     */
    @Test
    void testNullDescriptionOnUpdate() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.update(null, validStartingMoment(), validDuration()),
                "Updating an exam with a null description is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exam} with a too short description.
     */
    @Test
    void testShortDescriptionOnUpdate() {
        final var exam = createExam();
        shortDescription().ifPresent(
                shortDescription -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> exam.update(shortDescription, validStartingMoment(), validDuration()),
                        "Updating an exam with a too short description is being allowed."
                )
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exam} with a too long description.
     */
    @Test
    void testLongDescriptionOnUpdate() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.update(longDescription(), validStartingMoment(), validDuration()),
                "Updating an exam with a too long description is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exam} with a null starting at {@link LocalDateTime}.
     */
    @Test
    void testNullStartingAtOnUpdate() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.update(validDescription(), null, validDuration()),
                "Updating an exam with a null starting at local date time is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exam} with a past starting at {@link LocalDateTime}.
     */
    @Test
    void testPastStartingAtOnUpdate() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.update(validDescription(), pastStartingMoment(), validDuration()),
                "Updating an exam with a past starting at local date time is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exam} with a null duration.
     */
    @Test
    void testNullDurationOnUpdate() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.update(validDescription(), validStartingMoment(), null),
                "Updating an exam with a null duration is being allowed."
        );
    }


    // ================================
    // Setters
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a null description to an {@link Exam}.
     */
    @Test
    void testSetNullDescription() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.setDescription(null),
                "Setting a null description is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a too short description to an {@link Exam}.
     */
    @Test
    void testSetShortDescription() {
        final var exam = createExam();
        shortDescription().ifPresent(
                shortDescription -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> exam.setDescription(shortDescription),
                        "Setting a too short description is being allowed."
                )
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a too long description to an {@link Exam}.
     */
    @Test
    void testSetLongDescription() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.setDescription(longDescription()),
                "Setting a too long description is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a null starting at {@link LocalDateTime} to an {@link Exam}.
     */
    @Test
    void testSetNullStartingAt() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.setStartingAt(null),
                "Setting a null starting at local date time is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a past staring at {@link LocalDateTime} to an {@link Exam}.
     */
    @Test
    void testSetPastStartingAt() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.setStartingAt(pastStartingMoment()),
                "Setting a past starting at local date time is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a null duration to an {@link Exam}.
     */
    @Test
    void testSetNullDuration() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.setDuration(null),
                "Setting a null duration is being allowed."
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Creation of objects
    // ========================================

    /**
     * Creates a valid {@link Exam}.
     *
     * @return An {@link Exam}.
     */
    private static Exam createExam() {
        return new Exam(
                validDescription(),
                validStartingMoment(),
                validDuration()
        );
    }


    // ========================================
    // Valid values
    // ========================================

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
     * @return An {@link Optional} containing a description whose length is below the valid limit
     * if there is such limit (i.e th min length is positive). Otherwise, an empty {@link Optional} is returned.
     */
    private static Optional<String> shortDescription() {
        if (ValidationConstants.DESCRIPTION_MIN_LENGTH > 0) {
            final var description = Faker.instance()
                    .lorem()
                    .fixedString(ValidationConstants.DESCRIPTION_MIN_LENGTH - 1);
            return Optional.of(description);
        }
        return Optional.empty();
    }

    /**
     * @return A description that is invalid to be used in an {@link Exam} because it is too long.
     */
    private static String longDescription() {
        return Faker.instance()
                .lorem()
                .fixedString(ValidationConstants.DESCRIPTION_MAX_LENGTH + 1);
    }

    /**
     * @return A {@link LocalDateTime} that is invalid to be used in an {@link Exam} as a starting moment
     * because it is a past moment.
     */
    private static LocalDateTime pastStartingMoment() {
        final var previousDayInstant = Instant.now().minus(Duration.ofDays(1));
        return Faker.instance()
                .date()
                .past(DAYS_IN_A_YEAR, TimeUnit.DAYS, Date.from(previousDayInstant))
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                ;
    }
}
