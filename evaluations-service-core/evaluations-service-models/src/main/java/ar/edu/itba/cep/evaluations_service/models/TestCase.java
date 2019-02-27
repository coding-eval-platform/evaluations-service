package ar.edu.itba.cep.evaluations_service.models;

import java.util.Collections;
import java.util.List;

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
    private final Visibility visibility;


    /**
     * Constructor.
     *
     * @param input          The input of the test case.
     * @param expectedOutput The expected output.
     * @param visibility     Indicates whether the test case is public or private.
     */
    public TestCase(final List<String> input, final List<String> expectedOutput, final Visibility visibility) {
        this.id = 0;
        this.input = Collections.unmodifiableList(input);
        this.expectedOutput = Collections.unmodifiableList(expectedOutput);
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
     * An enum holding visibility values (i.e indicate whether the test case is public or private).
     */
    public enum Visibility {
        PUBLIC,
        PRIVATE;
    }
}
