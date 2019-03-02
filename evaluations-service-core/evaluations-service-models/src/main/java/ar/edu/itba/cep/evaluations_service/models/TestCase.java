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
     * The inputs of the test case.
     */
    private final List<String> inputs;
    /**
     * The expected output.
     */
    private final List<String> expectedOutputs;
    /**
     * Indicates whether the test case is public or private.
     */
    // TODO: maybe, when implementing the scoring system, subclasses would be better (only privates will grant score).
    private Visibility visibility;

    /**
     * The {@link Exercise} to which this test case belongs to.
     */
    private final Exercise belongsTo;


    /**
     * Constructor.
     *
     * @param visibility Indicates whether the test case is public or private.
     * @param belongsTo  The {@link Exercise} to which this test case belongs to.
     */
    public TestCase(final Visibility visibility, final Exercise belongsTo) {
        this.id = 0;
        this.inputs = new LinkedList<>();
        this.expectedOutputs = new LinkedList<>();
        this.visibility = visibility;
        this.belongsTo = belongsTo;
    }


    /**
     * @return The test case's id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The inputs of the test case.
     */
    public List<String> getInputs() {
        return inputs;
    }

    /**
     * @return The expected output.
     */
    public List<String> getExpectedOutputs() {
        return expectedOutputs;
    }

    /**
     * @return Indicates whether the test case is public or private.
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * @return The {@link Exercise} to which this test case belongs to.
     */
    public Exercise belongsToExercise() {
        return belongsTo;
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
     * Replaces the inputs {@link List} for this test case.
     *
     * @param inputs The new {@link List} of inputs for this test case.
     */
    public void setInputs(final List<String> inputs) {
        this.inputs.clear();
        this.inputs.addAll(inputs);
    }

    /**
     * Replaces the outputs {@link List} for this test case.
     *
     * @param outputs The new {@link List} of outputs for this test case.
     */
    public void setExpectedInputs(final List<String> outputs) {
        this.expectedOutputs.clear();
        this.expectedOutputs.addAll(outputs);
    }

    /**
     * Removes all inputs.
     */
    public void removeAllInputs() {
        this.inputs.clear();
    }

    /**
     * Removes all outputs.
     */
    public void removeAllExpectedOutputs() {
        this.expectedOutputs.clear();
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
                "Input: " + inputs + ", " +
                "ExpectedOutput: " + expectedOutputs + ", " +
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
