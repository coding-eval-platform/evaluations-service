package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.*;
import com.github.javafaker.Faker;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for testing.
 */
class TestHelper {

    /**
     * Amount of days (in a non leap year).
     */
    private static final int DAYS_IN_A_YEAR = 365;

    /**
     * Indicates how many elements must have the created {@link String} {@link List}s used for testing
     * (i.e to be used as inputs and expected outputs).
     */
    private static final int STRING_LISTS_SIZE = 10;


    // ================================================================================================================
    // Valid values
    // ================================================================================================================

    // ================================
    // Entities
    // ================================

    /**
     * @return A valid {@link Exam}.
     */
    /* package */
    static Exam validExam() {
        return new Exam(
                validExamDescription(),
                validExamStartingMoment(),
                validExamDuration()
        );
    }

    /**
     * @return A valid {@link Exercise}.
     */
    /* package */
    static Exercise validExercise() {
        return new Exercise(
                validExerciseQuestion(),
                validExam()
        );
    }

    /**
     * @return A valid {@link TestCase}.
     */
    /* package */
    static TestCase validTestCase() {
        return new TestCase(
                validTestCaseVisibility(),
                validExercise()
        );
    }

    /**
     * @return A valid {@link ExerciseSolution}.
     */
    /* package */
    static ExerciseSolution validExerciseSolution() {
        return new ExerciseSolution(
                validExercise(),
                validExerciseSolutionAnswer()
        );
    }

    // ================================
    // Values
    // ================================

    /**
     * @return A valid {@link Exam} id.
     */
    /* package */
    static long validExamId() {
        return Faker.instance().number().numberBetween(1L, Long.MAX_VALUE);
    }

    /**
     * @return A valid {@link Exam} description.
     */
    /* package */
    static String validExamDescription() {
        return Faker.instance()
                .lorem()
                .characters(ValidationConstants.DESCRIPTION_MIN_LENGTH, ValidationConstants.DESCRIPTION_MAX_LENGTH);
    }

    /**
     * @return A valid {@link Exam} starting moment {@link LocalDateTime}.
     */
    /* package */
    static LocalDateTime validExamStartingMoment() {
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
     * @return A valid {@link Exam} {@link Duration}.
     */
    /* package */
    static Duration validExamDuration() {
        return Duration.ofMinutes(Faker.instance().number().numberBetween(15L, 240L));
    }

    /**
     * @return A valid {@link Exercise} id.
     */
    /* package */
    static long validExerciseId() {
        return Faker.instance().number().numberBetween(1L, Long.MAX_VALUE);
    }

    /**
     * @return A valid {@link Exercise} question.
     */
    /* package */
    static String validExerciseQuestion() {
        // There is no such max value for questions, but it is needed for creating a value with Faker
        final var maxLength = Short.MAX_VALUE;
        return Faker.instance()
                .lorem()
                .characters(ValidationConstants.QUESTION_MIN_LENGTH, maxLength);
    }

    /**
     * @return A valid {@link TestCase} id.
     */
    /* package */
    static long validTestCaseId() {
        return Faker.instance().number().numberBetween(1L, Long.MAX_VALUE);
    }

    /**
     * @return A random {@link TestCase.Visibility}.
     */
    /* package */
    static TestCase.Visibility validTestCaseVisibility() {
        final var visibilities = TestCase.Visibility.values();
        final var randomIndex = Faker.instance().number().numberBetween(0, visibilities.length);
        return visibilities[randomIndex];
    }

    /**
     * Creates a valid {@link List} of {@link String} to be used as inputs or expected outputs.
     *
     * @return A valid {@link List}.
     */
    /* package */
    static List<String> validTestCaseList() {
        return Faker.instance()
                .lorem()
                .words(STRING_LISTS_SIZE);
    }

    /**
     * @return A random answer whose length is between the valid limits.
     */
    /* package */
    static String validExerciseSolutionAnswer() {
        // There is no such max value for answers, but it is needed for creating a value with Faker
        final var maxLength = Short.MAX_VALUE;
        return Faker.instance()
                .lorem()
                .characters(ValidationConstants.ANSWER_MIN_LENGTH, maxLength);
    }

    /**
     * @return A valid {@link ExerciseSolution} id.
     */
    /* package */
    static long validExerciseSolutionId() {
        return Faker.instance().number().numberBetween(1L, Long.MAX_VALUE);
    }

    /**
     * @return A valid {@link ExerciseSolution} id.
     */
    /* package */
    static int validExerciseSolutionExitCode() {
        return Faker.instance().number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Creates a valid {@link List} of {@link String} to be used as stdout or stderr when processing an execution.
     *
     * @return A valid {@link List}.
     */
    /* package */
    static List<String> validExerciseSolutionResultList() {
        return Faker.instance()
                .lorem()
                .words(STRING_LISTS_SIZE);
    }


    // ================================================================================================================
    // Invalid values
    // ================================================================================================================

    /**
     * @return An invalid {@link Exam} description.
     */
    /* package */
    static String invalidExamDescription() {
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
     * @return An invalid {@link Exam} {@link LocalDateTime} starting moment.
     */
    /* package */
    static LocalDateTime invalidExamStartingAt() {
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
     * @return An invalid {@link Exam} duration.
     */
    /* package */
    static Duration invalidExamDuration() {
        return null;
    }

    /**
     * @return An invalid {@link Exercise} question.
     */
    /* package */
    static String invalidExerciseQuestion() {
        final var possibleValues = new LinkedList<String>();
        // Add a null value
        possibleValues.add(null);
        // Add a short description
        if (ValidationConstants.QUESTION_MIN_LENGTH > 0) {
            possibleValues.add(
                    Faker.instance()
                            .lorem()
                            .fixedString(ValidationConstants.QUESTION_MIN_LENGTH - 1)
            );
        }
        final var index = Faker.instance()
                .number()
                .numberBetween(0, possibleValues.size());
        return possibleValues.get(index);
    }

    /**
     * @return An invalid {@link TestCase.Visibility}.
     */
    /* package */
    static TestCase.Visibility invalidTestCaseVisibility() {
        return null;
    }

    /**
     * @return An invalid {@link List} of {@link String} for {@link TestCase}.
     */
    /* package */
    static List<String> invalidTestCaseList() {
        final var possibleValues = new LinkedList<List<String>>();
        possibleValues.add(null);
        final var listWithNulls = Stream.concat(
                Faker.instance().lorem().words(STRING_LISTS_SIZE - 1).stream(),
                Stream.of((String) null)
        ).collect(Collectors.toList());
        Collections.shuffle(listWithNulls); // Perform shuffling to be sure that check is performed in all the list
        possibleValues.add(listWithNulls);

        final var index = Faker.instance()
                .number()
                .numberBetween(0, possibleValues.size());
        return possibleValues.get(index);
    }

    /**
     * @return An invalid {@link ExerciseSolution} answer.
     */
    /* package */
    static String invalidExerciseSolutionAnswer() {
        final var possibleValues = new LinkedList<String>();
        // Add a null value
        possibleValues.add(null);
        // Add a short description
        if (ValidationConstants.ANSWER_MIN_LENGTH > 0) {
            possibleValues.add(
                    Faker.instance()
                            .lorem()
                            .fixedString(ValidationConstants.ANSWER_MIN_LENGTH - 1)
            );
        }
        final var index = Faker.instance()
                .number()
                .numberBetween(0, possibleValues.size());
        return possibleValues.get(index);
    }

    /**
     * @return An invalid {@link List} of {@link String} to be used as stdout or stderr when processing an execution.
     */
    /* package */
    static List<String> invalidExerciseSolutionResultList() {
        return null;
    }
}
