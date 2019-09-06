package ar.edu.itba.cep.evaluations_service.models;

import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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
     * Tests that adding an owner to an {@link Exam} (using valid values) works as expected.
     */
    @Test
    void testValidOwnerWhenAdding() {
        Assertions.assertAll(
                "Adding an owner is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExam().addOwner(validOwner()),
                        "It throws an exception"
                ),
                () -> {
                    final var exam = createExam();
                    final var creator = exam.getOwners().stream().findFirst().orElseThrow(IllegalStateException::new);
                    final var owner1 = validOwner();
                    final var owner2 = validOwner();
                    final var owner3 = validOwner();
                    exam.addOwner(owner1);
                    exam.addOwner(owner2);
                    exam.addOwner(owner3);
                    Assertions.assertTrue(
                            exam.getOwners().containsAll(Set.of(creator, owner1, owner2, owner3)),
                            "There is a mismatch in the owners"
                    );
                }
        );
    }

    /**
     * Tests how the {@link Exam#removeOwner(String)} method behaves when using valid and invalid values.
     */
    @Test
    void testValidAndInvalidOwnerWhenRemoving() {
        Assertions.assertAll(
                "Removing an owner is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> {
                            final var exam = createExam();
                            exam.removeOwner(validOwner());
                        },
                        "Throws an exception when using valid owners that are not contained in the owners set"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> {
                            final var exam = createExam();
                            final var owner = validOwner();
                            exam.addOwner(owner);
                            exam.removeOwner(owner);
                        },
                        "Throws an exception when using valid owners that are contained in the owners set"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> createExam().removeOwner(invalidOwner()),
                        "Throws an exception when using an invalid value"
                )
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
        Assertions.assertDoesNotThrow(
                () -> exam.update(validDescription(), validStartingMoment(), validDuration()),
                "Exams are not being able to be modified in UPCOMING state"
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
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> exam.update(validDescription(), validStartingMoment(), validDuration()),
                "Exams are being allowed to be modified in IN_PROGRESS state"
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
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> exam.update(validDescription(), validStartingMoment(), validDuration()),
                "Exams are being allowed to be modified in FINISHED state"
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

    /**
     * Tests several variants of the {@link Exam#removeOwner(String)}, testing with the creator and additional owner,
     * using values that are contained and not contained as well.
     */
    @Test
    void testRemoveAndOwnersQuantity() {
        Assertions.assertAll(
                "Removing an owner does not work as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExam().removeOwner(validOwner()),
                        "Removing an owner that is not contained throws an unexpected exception"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> {
                            final var exam = createExam();
                            exam.addOwner(validOwner());
                            exam.removeOwner(validOwner());
                        },
                        "Removing an owner that is contained in the owners set throws an unexpected exception, " +
                                "having another owner in the set (the owner)"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> {
                            final var exam = createExam();
                            final var creator = exam.getOwners().stream()
                                    .findFirst()
                                    .orElseThrow(IllegalStateException::new);
                            final var anotherOwner = validOwner();
                            exam.addOwner(anotherOwner);
                            exam.removeOwner(creator);
                        },
                        "Removing the creator throws an unexpected exception, even when having another owner"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> {
                            final var exam = createExam();
                            final var creator = exam.getOwners().stream()
                                    .findFirst()
                                    .orElseThrow(IllegalStateException::new);
                            exam.removeOwner(creator);
                        },
                        "Removing the creator (only owner) is being allowed"
                ),
                () -> Assertions.assertThrows(
                        IllegalEntityStateException.class,
                        () -> {
                            final var exam = createExam();
                            final var creator = exam.getOwners().stream()
                                    .findFirst()
                                    .orElseThrow(IllegalStateException::new);
                            final var anotherOwner = validOwner();
                            exam.removeOwner(creator);
                            exam.removeOwner(anotherOwner);
                        },
                        "Removing an owner when there is any other (even the creator was removed) " +
                                "is being allowed"
                )

        );
    }

    /**
     * Tests that an owner can be added when the exam is upcoming.
     */
    @Test
    void testAddOwnerIfUpcoming() {
        Assertions.assertDoesNotThrow(
                () -> createExam().addOwner(validOwner()),
                "Adding an owner when the exam is upcoming is not being allowed"
        );
    }

    /**
     * Tests that an owner can be added when the exam is in progress.
     */
    @Test
    void testAddOwnerIfInProgress() {
        Assertions.assertDoesNotThrow(
                () -> {
                    final var exam = createExam();
                    exam.startExam();
                    exam.addOwner(validOwner());
                },
                "Adding an owner when the exam is in progress is not being allowed"
        );
    }

    /**
     * Tests that an owner can be added when the exam is finished.
     */
    @Test
    void testAddOwnerIfFinished() {
        Assertions.assertDoesNotThrow(
                () -> {
                    final var exam = createExam();
                    exam.startExam();
                    exam.finishExam();
                    exam.addOwner(validOwner());
                },
                "Adding an owner when the exam is finished is not being allowed"
        );
    }

    /**
     * Tests that an owner can be removed when the exam is upcoming.
     */
    @Test
    void testRemoveOwnerIfUpcoming() {
        Assertions.assertDoesNotThrow(
                () -> {
                    final var exam = createExam();
                    final var owner = validOwner();
                    exam.addOwner(owner);
                    exam.removeOwner(owner);
                },
                "Removing an owner when the exam is upcoming is not being allowed"
        );
    }

    /**
     * Tests that an owner can be removed when the exam is in progress.
     */
    @Test
    void testRemoveOwnerIfInProgress() {
        Assertions.assertDoesNotThrow(
                () -> {
                    final var exam = createExam();
                    final var owner = validOwner();
                    exam.addOwner(owner);
                    exam.startExam();
                    exam.removeOwner(owner);
                },
                "Removing an owner when the exam is in progress is not being allowed"
        );
    }

    /**
     * Tests that an owner can be removed when the exam is finished.
     */
    @Test
    void testRemoveOwnerIfFinished() {
        Assertions.assertDoesNotThrow(
                () -> {
                    final var exam = createExam();
                    final var owner = validOwner();
                    exam.addOwner(owner);
                    exam.startExam();
                    exam.finishExam();
                    exam.removeOwner(owner);
                },
                "Removing an owner when the exam is finished is not being allowed"
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
                () -> new Exam(null, validStartingMoment(), validDuration(), validOwner()),
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
                        () -> new Exam(shortDescription, validStartingMoment(), validDuration(), validOwner()),
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
                () -> new Exam(longDescription(), validStartingMoment(), validDuration(), validOwner()),
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
                () -> new Exam(validDescription(), null, validDuration(), validOwner()),
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
                () -> new Exam(validDescription(), pastStartingMoment(), validDuration(), validOwner()),
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
                () -> new Exam(validDescription(), validStartingMoment(), null, validOwner()),
                "Creating an exam with a null duration is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exam} with a zero {@link Duration} duration.
     */
    @Test
    void testZeroDurationOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exam(validDescription(), validStartingMoment(), Duration.ZERO, validOwner()),
                "Creating an exam with a zero duration is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exam} with a zero {@link Duration} duration.
     */
    @Test
    void testNegativeDurationOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exam(validDescription(), validStartingMoment(), negativeDuration(), validOwner()),
                "Creating an exam with a negative duration is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown when creating an {@link Exam} with a null creator.
     */
    @Test
    void testNullCreator() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exam(validDescription(), validStartingMoment(), validDuration(), null),
                "Creating an exam with a null creator is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown when creating an {@link Exam}
     * with an empty string creator.
     */
    @Test
    void testEmptyStringCreator() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exam(validDescription(), validStartingMoment(), validDuration(), ""),
                "Creating an exam with an empty string creator is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown when creating an {@link Exam}
     * with a blank string creator.
     */
    @Test
    void testBlankStringCreator() {
        Assertions.assertAll(
                "Creating an exam with a blank string creator is being allowed.",
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new Exam(validDescription(), validStartingMoment(), validDuration(), " "),
                        "It is being allowed with a space"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new Exam(validDescription(), validStartingMoment(), validDuration(), "\t"),
                        "It is being allowed with a tab"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new Exam(validDescription(), validStartingMoment(), validDuration(), "\n"),
                        "It is being allowed with a newline character"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new Exam(validDescription(), validStartingMoment(), validDuration(), "\n \t \n"),
                        "It is being allowed with a combination of several blank characters"
                )
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

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exam} with a zero {@link Duration} duration.
     */
    @Test
    void testZeroDurationOnUpdate() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.update(validDescription(), validStartingMoment(), Duration.ZERO),
                "Updating an exam with a zero duration is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exam} with a zero {@link Duration} duration.
     */
    @Test
    void testNegativeDurationOnUpdate() {
        final var exam = createExam();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exam.update(validDescription(), validStartingMoment(), negativeDuration()),
                "Updating an exam with a negative duration is being allowed."
        );
    }


    // ================================
    // Add owner
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown when adding a null creator to an {@link Exam}.
     */
    @Test
    void testNullOwner() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createExam().addOwner(null),
                "Adding a null owner is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown when adding an empty string creator to an {@link Exam}.
     */
    @Test
    void testEmptyStringOwner() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createExam().addOwner(""),
                "Adding an empty string owner is being allowed."
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown when adding a blank string creator to an {@link Exam}.
     */
    @Test
    void testBlankStringOwner() {
        Assertions.assertAll(
                "Adding a blank string owner is being allowed.",
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> createExam().addOwner(" "),
                        "It is being allowed with a space"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> createExam().addOwner("\t"),
                        "It is being allowed with a tab"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> createExam().addOwner("\n"),
                        "It is being allowed with a newline character"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> createExam().addOwner("\n \t \n"),
                        "It is being allowed with a combination of several blank characters"
                )
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
                validDuration(),
                validOwner()
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

    /**
     * @return A random username.
     */
    private static String validOwner() {
        return Faker.instance().name().username();
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

    /**
     * @return A {@link Duration} that is invalid to be used in an {@link Exam} as a duration because it is negative.
     */
    private static Duration negativeDuration() {
        final var number = Faker.instance().number().numberBetween(Short.MIN_VALUE, 0L);
        return Duration.ofMinutes(number);
    }

    /**
     * @return An invalid owner.
     */
    private static String invalidOwner() {
        final List<String> invalidValues = new LinkedList<>();
        invalidValues.add(null);
        invalidValues.add("");
        invalidValues.add(" \t\n");
        final var index = (int) Faker.instance().number().numberBetween(0L, invalidValues.size());
        return invalidValues.get(index);
    }
}
