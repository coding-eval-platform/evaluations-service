package ar.edu.itba.cep.evaluations_service.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;


/**
 * Test class for {@link ExerciseSolution}s
 */
@ExtendWith(MockitoExtension.class)
class ExerciseSolutionTest {

    /**
     * Indicates how many elements must have the created {@link String} {@link List}s used for testing
     * (i.e to be used as inputs and expected outputs).
     */
    private static final int STRING_LISTS_SIZE = 10;


    /**
     * A mocked {@link Exercise} that will own the created {@link ExerciseSolution}s to be tested.
     */
    private final Exercise mockedExercise;

    /**
     * Constructor.
     *
     * @param mockedExercise A mocked {@link Exercise} that will own the created {@link ExerciseSolution}s to be tested.
     */
    ExerciseSolutionTest(@Mock final Exercise mockedExercise) {
        this.mockedExercise = mockedExercise;
    }


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests that creating an {@link ExerciseSolution}
     * with valid values can be performed without any exception being thrown.
     */
    @Test
    void testAcceptableArguments() {
        Assertions.assertDoesNotThrow(
                this::createExerciseSolution,
                "Exercise solutions with acceptable arguments are not being created"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }


    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    // ================================
    // Creation
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExerciseSolution} with a null {@link Exercise}.
     */
    @Test
    void testNullExerciseOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExerciseSolution(null, validAnswer()),
                "Creating an exercise solution with a null exercise is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }


    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExerciseSolution} with a null answer.
     */
    @Test
    void testNullAnswerOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExerciseSolution(mockedExercise, null),
                "Creating an exercise solution with a null answer is being allowed"
        );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExerciseSolution} with a too short answer.
     */
    @Test
    void testShortAnswerOnCreation() {
        shortAnswer().ifPresent(
                answer -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new ExerciseSolution(mockedExercise, answer),
                        "Creating an exercise solution with a too short answer is being allowed"
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
     * Creates a valid {@link ExerciseSolution}.
     *
     * @return An {@link ExerciseSolution}.
     */
    private ExerciseSolution createExerciseSolution() {
        return new ExerciseSolution(
                mockedExercise,
                validAnswer()
        );
    }


    // ========================================
    // Valid values
    // ========================================

    /**
     * @return A random answer whose length is between the valid limits.
     */
    private static String validAnswer() {
        // There is no such max value for answers, but it is needed for creating a value with Faker
        final var maxLength = Short.MAX_VALUE;
        return Faker.instance()
                .lorem()
                .characters(ValidationConstants.ANSWER_MIN_LENGTH, maxLength);
    }


    // ========================================
    // Invalid values
    // ========================================

    /**
     * @return An {@link Optional} containing an answer whose length is below the valid limit
     * if there is such limit (i.e th min length is positive). Otherwise, an empty {@link Optional} is returned.
     */
    private static Optional<String> shortAnswer() {
        if (ValidationConstants.ANSWER_MIN_LENGTH > 0) {
            final var answer = Faker.instance()
                    .lorem()
                    .fixedString(ValidationConstants.ANSWER_MIN_LENGTH - 1);
            return Optional.of(answer);
        }
        return Optional.empty();
    }
}
