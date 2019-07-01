package ar.edu.itba.cep.evaluations_service.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * Represents an exercise's solution result (i.e approved or failed).
 * This class relates an {@link ExerciseSolution} and a {@link TestCase},
 * indicating whether the solution is approved or failed, when testing it with the test case.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(doNotUseGetters = true, callSuper = true)
public class ExerciseSolutionResult {

    /**
     * The exercise's solution result id.
     */
    private final long id;
    /**
     * The {@link ExerciseSolution} to which this result makes reference.
     */
    private final ExerciseSolution solution;
    /**
     * The test case being used to reach the result.
     */
    private final TestCase testCase;
    /**
     * Indicates whether the result is approved or failed.
     */
    private final Result result;


    /**
     * Default constructor.
     */
    /* package */ ExerciseSolutionResult() {
        // Initialize final fields with default values.
        this.id = 0;
        this.solution = null;
        this.testCase = null;
        this.result = null;
    }

    /**
     * Constructor.
     *
     * @param solution The {@link ExerciseSolution} to which this result makes reference.
     * @param testCase The test case being used to reach the result.
     * @param result   Indicates whether the result is approved or failed.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public ExerciseSolutionResult(final ExerciseSolution solution, final TestCase testCase, final Result result)
            throws IllegalArgumentException {
        assertSolution(solution);
        assertTestCase(testCase);
        assertResult(result);
        this.id = 0;
        this.solution = solution;
        this.testCase = testCase;
        this.result = result;
    }


    // ================================
    // Helpers
    // ================================

    /**
     * An enum with values describing the result.
     */
    public enum Result {
        /**
         * Indicates that an exercise's solution is accepted when testing it with a given test case.
         */
        APPROVED,
        /**
         * Indicates that an exercise's solution is not accepted when testing it with a given test case because
         * the execution returned a non expected output.
         */
        FAILED,
        /**
         * Indicates that an exercise's solution is not accepted when testing it with a given test case because
         * the execution timed out.
         */
        TIMED_OUT,
        /**
         * Indicates that an exercise's solution is not accepted when testing it with a given test case because
         * the code could not be compiled.
         */
        NOT_COMPILED,
        ;
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code solution} is valid.
     *
     * @param solution The {@link ExerciseSolution} to be checked.
     * @throws IllegalArgumentException If the solution is not valid.
     */
    private static void assertSolution(final ExerciseSolution solution) throws IllegalArgumentException {
        Assert.notNull(solution, "The solution is missing");
    }

    /**
     * Asserts that the given {@code testCase} is valid.
     *
     * @param testCase The {@link TestCase} to be checked.
     * @throws IllegalArgumentException If the test case is not valid.
     */
    private static void assertTestCase(final TestCase testCase) throws IllegalArgumentException {
        Assert.notNull(testCase, "The test case is missing");
    }

    /**
     * Asserts that the given {@code result} is valid.
     *
     * @param result The {@link Result} to be checked.
     * @throws IllegalArgumentException If the result is not valid.
     */
    private static void assertResult(final Result result) throws IllegalArgumentException {
        Assert.notNull(result, "The result is missing");
    }
}
