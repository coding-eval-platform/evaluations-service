package ar.edu.itba.cep.evaluations_service.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a test case for an {@link Exercise}.
 */
public class TestCase {

    /**
     * The test case's id.
     */
    private final long id;
    /**
     * The input of the test case.
     */
    private final List<String> input;
    /**
     * The expected output.
     */
    private final List<String> expectedOutput;
    /**
     * Indicates whether the test case is public or private.
     */
    // TODO: maybe, when implementing the scoring system, subclasses would be better (only privates will grant score).
    private Visibility visibility;


    /**
     * Constructor.
     *
     * @param visibility Indicates whether the test case is public or private.
     */
    public TestCase(final Visibility visibility) {
        this.id = 0;
        this.input = new LinkedList<>();
        this.expectedOutput = new LinkedList<>();
        this.visibility = visibility;
    }


    /**
     * @return The test case's id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The input of the test case.
     */
    public List<String> getInput() {
        return input;
    }

    /**
     * @return The expected output.
     */
    public List<String> getExpectedOutput() {
        return expectedOutput;
    }

    /**
     * @return Indicates whether the test case is public or private.
     */
    public Visibility getVisibility() {
        return visibility;
    }


    /**
     * Changes the visibility for this test case.
     *
     * @param visibility The new {@link Visibility} for this test case.
     */
    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * Adds the given {@code input} to the {@link List} of inputs of this test case.
     *
     * @param input The input to be added.
     */
    public void addInput(final String input) {
        this.input.add(input);
    }

    /**
     * Adds the given {@code inputs} {@link List} to the {@link List} of inputs of this test case.
     *
     * @param inputs The {@link List} of inputs to be added.
     */
    public void addInputs(final List<String> inputs) {
        this.input.addAll(inputs);
    }

    /**
     * Removes the given {@code input} from the {@link List} of inputs of this test case.
     *
     * @param input The input to be removed.
     */
    public void removeInput(final String input) {
        this.input.remove(input);
    }

    /**
     * Removes all inputs.
     */
    public void removeAllInputs() {
        this.input.clear();
    }

    /**
     * Adds the given {@code output} to the {@link List} of expected outputs of this test case.
     *
     * @param output The output to be added.
     */
    public void addExpectedOutput(final String output) {
        this.expectedOutput.add(output);
    }

    /**
     * Adds the given {@code outputs} {@link List} to the {@link List} of expected outputs of this test case.
     *
     * @param outputs The {@link List} of outputs to be added.
     */
    public void addExpectedOutputs(final List<String> outputs) {
        this.expectedOutput.addAll(outputs);
    }

    /**
     * Removes the given {@code output} from the {@link List} of expected outputs of this test case.
     *
     * @param output The output to be removed.
     */
    public void removeOutput(final String output) {
        this.expectedOutput.remove(output);
    }

    /**
     * Removes all outputs.
     */
    public void removeAllOutputs() {
        this.expectedOutput.clear();
    }


    // ================================
    // equals, hashcode and toString
    // ================================

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestCase)) {
            return false;
        }
        final var testCase = (TestCase) o;
        return id == testCase.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TestCase [" +
                "ID: " + id + ", " +
                "Input: " + input + ", " +
                "ExpectedOutput: " + expectedOutput + ", " +
                "Visibility: " + visibility +
                ']';
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
