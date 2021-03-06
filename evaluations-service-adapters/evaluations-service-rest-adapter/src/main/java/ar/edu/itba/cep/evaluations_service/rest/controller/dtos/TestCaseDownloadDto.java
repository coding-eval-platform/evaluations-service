package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data transfer object for sending an {@link TestCase}s data to an API consumer.
 */
public class TestCaseDownloadDto {

    /**
     * The {@link TestCase}'s id.
     */
    private final long id;

    /**
     * Indicates whether the test case is public or private.
     */
    private final TestCase.Visibility visibility;

    /**
     * The timeout of the test case.
     */
    private final Long timeout;

    /**
     * The inputs of the test case.
     */
    private final List<String> programArguments;

    /**
     * The inputs of the test case.
     */
    private final List<String> stdin;

    /**
     * The expected output.
     */
    private final List<String> expectedOutputs;


    /**
     * Constructor.
     *
     * @param testCase The {@link TestCase} whose data will be transferred.
     */
    public TestCaseDownloadDto(final TestCase testCase) {
        this.id = testCase.getId();
        this.visibility = testCase.getVisibility();
        this.timeout = testCase.getTimeout();
        this.programArguments = testCase.getProgramArguments();
        this.stdin = testCase.getStdin();
        this.expectedOutputs = testCase.getExpectedOutputs();
    }


    /**
     * @return The {@link Exercise}'s id.
     */
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    public long getId() {
        return id;
    }

    /**
     * @return Indicates whether the test case is public or private.
     */
    @JsonProperty(value = "visibility", access = JsonProperty.Access.READ_ONLY)
    public TestCase.Visibility getVisibility() {
        return visibility;
    }

    /**
     * @return The timeout of the test case.
     */
    @JsonProperty(value = "timeout", access = JsonProperty.Access.READ_ONLY)
    public Long getTimeout() {
        return timeout;
    }

    /**
     * @return The program arguments list of the test case.
     */
    @JsonProperty(value = "programArguments", access = JsonProperty.Access.READ_ONLY)
    public List<String> getProgramArguments() {
        return programArguments;
    }

    /**
     * @return The stdin list of the test case.
     */
    @JsonProperty(value = "stdin", access = JsonProperty.Access.READ_ONLY)
    public List<String> getStdin() {
        return stdin;
    }

    /**
     * @return The expected output.
     */
    @JsonProperty(value = "expectedOutputs", access = JsonProperty.Access.READ_ONLY)
    public List<String> getExpectedOutputs() {
        return expectedOutputs;
    }
}
