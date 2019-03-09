package ar.edu.itba.cep.evaluations_service.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


/**
 * Test class for {@link Exercise}s
 */
@ExtendWith(MockitoExtension.class)
class ExerciseTest {

    /**
     * A mocked {@link Exam} that will own the created {@link Exercise}s to be tested.
     */
    private final Exam mockedExam;

    /**
     * Constructor.
     *
     * @param mockedExam A mocked {@link Exam} that will own the created {@link Exercise}s to be tested.
     */
    ExerciseTest(@Mock final Exam mockedExam) {
        this.mockedExam = mockedExam;
    }


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests that creating an {@link Exercise} with valid values can be performed without any exception being thrown.
     */
    @Test
    void testAcceptableArguments() {
        Assertions.assertDoesNotThrow(this::createExercise,
                "An exercise is not being created with acceptable arguments.");
        Mockito.verifyZeroInteractions(mockedExam);
    }

    @Test
    void testSetValidQuestion() {
        Assertions.assertAll("Setting a valid question is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExercise().setQuestion(validQuestion()),
                        "It throws an exception"
                ),
                () -> {
                    final var exercise = createExercise();
                    final var question = validQuestion();
                    exercise.setQuestion(question);
                    Assertions.assertEquals(
                            question,
                            exercise.getQuestion(),
                            "Is not being set (does not change the Exercise value)"
                    );
                }
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
     * when creating an {@link Exercise} with a null question.
     */
    @Test
    void testNullQuestionOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exercise(null, mockedExam),
                "Creating an exercise with a null question is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exercise} with a too short question.
     */
    @Test
    void testShortQuestionOnCreation() {
        shortQuestion().ifPresent(
                shortQuestion -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new Exercise(shortQuestion, mockedExam),
                        "Creating an exercise with a too short question is being allowed."
                )
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exercise} with a null {@link Exam}.
     */
    @Test
    void testNullExamOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exercise(validQuestion(), null),
                "Creating an exercise with a null exam is being allowed."
        );
    }


    // ================================
    // Setters
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a null question to an {@link Exercise}.
     */
    @Test
    void testSetNullQuestion() {
        final var exercise = createExercise();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exercise.setQuestion(null),
                "Updating an exercise with a null question is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a too short question to an {@link Exercise}.
     */
    @Test
    void testSetShortQuestion() {
        final var exercise = createExercise();
        shortQuestion().ifPresent(
                shortQuestion -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> exercise.setQuestion(shortQuestion),
                        "Updating an exercise with a too short question is being allowed."
                )
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Creation of objects
    // ========================================

    /**
     * Creates a valid {@link Exercise}.
     *
     * @return An {@link Exercise}.
     */
    private Exercise createExercise() {
        return new Exercise(
                validQuestion(),
                mockedExam
        );
    }


    // ========================================
    // Valid values
    // ========================================

    /**
     * @return A random question whose length is between the valid limits.
     */
    private static String validQuestion() {
        // There is no such max value for questions, but it is needed for creating a value with Faker
        final var maxLength = Short.MAX_VALUE;
        return Faker.instance()
                .lorem()
                .characters(ValidationConstants.QUESTION_MIN_LENGTH, maxLength);
    }


    // ========================================
    // Invalid values
    // ========================================

    /**
     * @return An {@link Optional} containing a question whose length is below the valid limit
     * if there is such limit (i.e th min length is positive). Otherwise, an empty {@link Optional} is returned.
     */
    private static Optional<String> shortQuestion() {
        if (ValidationConstants.QUESTION_MIN_LENGTH > 0) {
            final var question = Faker.instance()
                    .lorem()
                    .fixedString(ValidationConstants.QUESTION_MIN_LENGTH - 1);
            return Optional.of(question);
        }
        return Optional.empty();
    }
}
