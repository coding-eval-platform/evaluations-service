package ar.edu.itba.cep.evaluations_service.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a test case for an {@link Exercise}.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(doNotUseGetters = true, callSuper = true)
public class TestCase {

    /**
     * The test case's id.
     */
    private final long id;

    /**
     * The inputs of the test case.
     */
    private final List<String> inputs;

    /**
     * The expected outputs.
     */
    private final List<String> expectedOutputs;

    /**
     * Indicates whether the test case is public or private.
     */
    // TODO: maybe, when implementing the scoring system, subclasses would be better (only privates will grant score).
    private Visibility visibility;

    /**
     * The time given to the exercise to execute, in milliseconds.
     */
    private Long timeout;

    /**
     * The {@link Exercise} to which this test case belongs to.
     */
    private final Exercise exercise;


    /**
     * Default constructor.
     */
    /* package */ TestCase() {
        // Initialize final fields with default values.
        this.id = 0;
        this.inputs = null;
        this.expectedOutputs = null;
        this.exercise = null;
    }

    /**
     * Constructor.
     *
     * @param visibility      Indicates whether the test case is public or private.
     * @param timeout         The time given to the exercise to execute, in milliseconds.
     * @param inputs          The inputs of the test case.
     * @param expectedOutputs The expected outputs.
     * @param exercise        The {@link Exercise} to which this test case belongs to.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public TestCase(
            final Visibility visibility,
            final Long timeout,
            final List<String> inputs,
            final List<String> expectedOutputs,
            final Exercise exercise)
            throws IllegalArgumentException {
        assertVisibility(visibility);
        assertTimeout(timeout);
        assertInputList(inputs);
        assertExpectedOutputsList(expectedOutputs);
        assertExercise(exercise);
        this.id = 0;
        this.visibility = visibility;
        this.timeout = timeout;
        this.inputs = new LinkedList<>(inputs);
        this.expectedOutputs = new LinkedList<>(expectedOutputs);
        this.exercise = exercise;
    }


    /**
     * Updates all fields of this test case.
     *
     * @param visibility      Indicates whether the test case is public or private.
     * @param timeout         The time given to the exercise to execute, in milliseconds.
     * @param inputs          inputs The new {@link List} of inputs for this test case.
     * @param expectedOutputs The new {@link List} of outputs for this test case.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public void update(
            final TestCase.Visibility visibility,
            final Long timeout,
            final List<String> inputs,
            final List<String> expectedOutputs) throws IllegalArgumentException {
        assertVisibility(visibility);
        assertTimeout(timeout);
        assertInputList(inputs);
        assertExpectedOutputsList(expectedOutputs);
        this.visibility = visibility;
        this.timeout = timeout;
        this.inputs.clear();
        this.inputs.addAll(inputs);
        this.expectedOutputs.clear();
        this.expectedOutputs.addAll(expectedOutputs);
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code visibility} is valid.
     *
     * @param visibility The {@link Visibility} to be checked.
     * @throws IllegalArgumentException If the visibility is not valid.
     */
    private static void assertVisibility(final Visibility visibility) throws IllegalArgumentException {
        Assert.notNull(visibility, "The visibility is missing");
    }

    /**
     * Asserts that the given {@code timeout} is valid.
     *
     * @param timeout The timeout to be checked.
     * @throws IllegalArgumentException If the {@code timeout} is not valid.
     */
    private static void assertTimeout(final Long timeout) throws IllegalArgumentException {
        Assert.isTrue(timeout == null || timeout > 0, "The timeout must be null or positive");
    }

    /**
     * Asserts that the given {@code inputs} {@link List} is valid.
     *
     * @param inputs The inputs {@link List} to be checked.
     * @throws IllegalArgumentException If the inputs {@link List} is not valid.
     */
    private static void assertInputList(final List<String> inputs) throws IllegalArgumentException {
        Assert.notNull(inputs, "The inputs list is missing");
        Assert.notEmpty(inputs,
                "The list must not be empty." +
                        " To clear the inputs list use TestCase#removeAllInputs");
        Assert.isTrue(inputs.stream().noneMatch(Objects::isNull), "The list must not contain null elements");
    }

    /**
     * Asserts that the given {@code outputs} {@link List} is valid.
     *
     * @param outputs The outputs {@link List} to be checked.
     * @throws IllegalArgumentException If the outputs {@link List} is not valid.
     */
    private static void assertExpectedOutputsList(final List<String> outputs) throws IllegalArgumentException {
        Assert.notNull(outputs, "The expected outputs list is missing");
        Assert.notEmpty(outputs,
                "The list must not be empty." +
                        " To clear the expected outputs list use TestCase#removeAllExpectedOutputs");
        Assert.isTrue(outputs.stream().noneMatch(Objects::isNull), "The list must not contain null elements");
    }

    /**
     * Asserts that the given {@code exercise} is valid.
     *
     * @param exercise The {@link Exercise} to be checked.
     * @throws IllegalArgumentException If the exercise is not valid.
     */
    private static void assertExercise(final Exercise exercise) throws IllegalArgumentException {
        Assert.notNull(exercise, "The exercise is missing");
    }

    // ================================
    // Helpers
    // ================================


    /**
     * An enum holding visibility values (i.e indicate whether the test case is public or private).
     */
    public enum Visibility {
        /**
         * Indicates that the test case is public
         * (i.e can be seen by the student sitting for the exam).
         */
        PUBLIC,
        /**
         * Indicates that the test case is private
         * (i.e cannot be seen by the student sitting for the exam, and is used to actually evaluate this student).
         */
        PRIVATE,
        ;
    }
}
