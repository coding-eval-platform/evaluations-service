package ar.edu.itba.cep.evaluations_service.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


/**
 * Test class for {@link ExerciseSolutionResult}s
 */
@ExtendWith(MockitoExtension.class)
class ExerciseSolutionResultTest {

    /**
     * A mocked {@link ExerciseSolution} which is used as the solution to which the {@link ExerciseSolutionResult}s
     * being tested corresponds to.
     */
    private final ExerciseSolution mockedExerciseSolution;

    /**
     * A mocked {@link TestCase} which is used for the execution that produces the {@link ExerciseSolutionResult}s
     * being tested.
     */
    private final TestCase mockedTestCase;

    /**
     * Constructor.
     *
     * @param mockedExerciseSolution A mocked {@link ExerciseSolution} which is used as the solution
     *                               to which the {@link ExerciseSolutionResult}s being tested corresponds to.
     * @param mockedTestCase         A mocked {@link TestCase} which is used for the execution
     *                               that produces the {@link ExerciseSolutionResult}s being tested.
     */
    ExerciseSolutionResultTest(
            @Mock final ExerciseSolution mockedExerciseSolution,
            @Mock final TestCase mockedTestCase) {
        this.mockedExerciseSolution = mockedExerciseSolution;
        this.mockedTestCase = mockedTestCase;
    }


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests that creating an {@link ExerciseSolutionResult}
     * with valid values can be performed without any exception being thrown.
     */
    @Test
    void testAcceptableArguments() {
        Assertions.assertDoesNotThrow(
                this::createExerciseSolutionResult,
                "Exercise solutions results with acceptable arguments are not being created"
        );
        Mockito.verifyZeroInteractions(mockedExerciseSolution);
        Mockito.verifyZeroInteractions(mockedTestCase);
    }

    /**
     * Tests that an {@link ExerciseSolutionResult} has the result with a {@code null} value when created.
     */
    @Test
    void testResultIsNullWhenCreated() {
        Assertions.assertNull(
                createExerciseSolutionResult().getResult(),
                "The result must be null when creating an exercise solution result"
        );
        Mockito.verifyZeroInteractions(mockedExerciseSolution);
        Mockito.verifyZeroInteractions(mockedTestCase);
    }

    /**
     * Tests that an {@link ExerciseSolutionResult} reports that it is not marked when created.
     */
    @Test
    void testNotMarkedWhenCreated() {
        Assertions.assertFalse(
                createExerciseSolutionResult().isMarked(),
                "The exercise solution result must report unmarked when created"
        );
        Mockito.verifyZeroInteractions(mockedExerciseSolution);
        Mockito.verifyZeroInteractions(mockedTestCase);
    }

    /**
     * Tests the marking process.
     */
    @Test
    void testMarking() {
        final var exerciseSolutionResult = createExerciseSolutionResult();
        final var result = validResult();
        exerciseSolutionResult.mark(result);

        Assertions.assertAll(
                "Marking a result does not work as expected",
                () -> Assertions.assertTrue(
                        exerciseSolutionResult.isMarked(),
                        "The result is not marked"
                ),
                () -> Assertions.assertEquals(
                        result,
                        exerciseSolutionResult.getResult(),
                        "The set result is not the expected"
                )
        );

        Mockito.verifyZeroInteractions(mockedExerciseSolution);
        Mockito.verifyZeroInteractions(mockedTestCase);
    }

    /**
     * Tests the unmarking process
     */
    @Test
    void testUnmark() {
        final var result = createExerciseSolutionResult();
        result.unmark();

        Assertions.assertAll(
                "Unmarking is not working as expected.",
                () -> Assertions.assertFalse(result.isMarked(), "It indicates that is not marked"),
                () -> Assertions.assertNull(result.getResult(), "The result is not null")
        );
    }

    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExerciseSolutionResult} with a null {@link ExerciseSolution}.
     */
    @Test
    void testNullExerciseSolutionOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExerciseSolutionResult(null, mockedTestCase),
                "Creating an exercise solution result with a null exercise solution is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedTestCase);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExerciseSolutionResult} with a null {@link TestCase}.
     */
    @Test
    void testNullTestCaseOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExerciseSolutionResult(mockedExerciseSolution, null),
                "Creating an exercise solution result with a null test case is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExerciseSolution);
    }

    /**
     * Tests that setting a {@code null} value as a mark throws an {@link IllegalArgumentException}.
     */
    @Test
    void testNullResult() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createExerciseSolutionResult().mark(null),
                "Marking with a null value is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExerciseSolution);
        Mockito.verifyZeroInteractions(mockedTestCase);
    }

    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Creation of objects
    // ========================================

    /**
     * Creates a valid {@link ExerciseSolutionResult}.
     *
     * @return An {@link ExerciseSolutionResult}.
     */
    private ExerciseSolutionResult createExerciseSolutionResult() {
        return new ExerciseSolutionResult(
                mockedExerciseSolution,
                mockedTestCase
        );
    }


    // ========================================
    // Valid values
    // ========================================

    /**
     * @return A random {@link ExerciseSolutionResult.Result}.
     */
    private static ExerciseSolutionResult.Result validResult() {
        final var results = ExerciseSolutionResult.Result.values();
        final var randomIndex = (int) Faker.instance().number().numberBetween(0L, results.length);
        return results[randomIndex];
    }
}
