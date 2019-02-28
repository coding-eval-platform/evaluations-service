package ar.edu.itba.cep.evaluations_service.models;

import java.util.Objects;

/**
 * Represents an exercise's solution result (i.e approved or failed).
 * This class relates an {@link ExerciseSolution} and a {@link TestCase},
 * indicating whether the solution is approved or failed, when testing it with the test case.
 */
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
     * Constructor.
     *
     * @param solution The {@link ExerciseSolution} to which this result makes reference.
     * @param testCase The test case being used to reach the result.
     * @param result   Indicates whether the result is approved or failed.
     */
    public ExerciseSolutionResult(final ExerciseSolution solution, final TestCase testCase, final Result result) {
        this.result = result;
        this.id = 0;
        this.solution = solution;
        this.testCase = testCase;
    }


    /**
     * @return The exercise's solution result id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The {@link ExerciseSolution} to which this result makes reference.
     */
    public ExerciseSolution getSolution() {
        return solution;
    }

    /**
     * @return The test case being used to reach the result.
     */
    public TestCase getTestCase() {
        return testCase;
    }

    /**
     * @return Indicates whether the result is approved or failed.
     */
    public Result getResult() {
        return result;
    }

    // ================================
    // equals, hashcode and toString
    // ================================

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExerciseSolutionResult)) {
            return false;
        }
        final var that = (ExerciseSolutionResult) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ExerciseSolutionResult [" +
                "ID: " + id + ", " +
                "Solution: " + solution + ", " +
                "TestCase: " + testCase + ", " +
                "Result: " + result +
                "]";
    }


    // ================================
    // Helpers
    // ================================

    /**
     * An enum indicating whether the result is "approved" or "failed".
     */
    public enum Result {
        APPROVED,
        FAILED,
        ;
    }
}
