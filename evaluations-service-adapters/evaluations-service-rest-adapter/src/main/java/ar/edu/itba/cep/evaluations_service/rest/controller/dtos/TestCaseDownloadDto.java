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
     * The inputs of the test case.
     */
    private final List<String> inputs;

    /**
     * The expected output.
     */
    private final List<String> outputs;


    /**
     * Constructor.
     *
     * @param testCase The {@link TestCase} whose data will be transferred.
     */
    public TestCaseDownloadDto(final TestCase testCase) {
        this.id = testCase.getId();
        this.visibility = testCase.getVisibility();
        this.inputs = testCase.getInputs();
        this.outputs = testCase.getExpectedOutputs();
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
     * @return The inputs of the test case.
     */
    @JsonProperty(value = "inputs", access = JsonProperty.Access.READ_ONLY)
    public List<String> getInputs() {
        return inputs;
    }

    /**
     * @return The expected output.
     */
    @JsonProperty(value = "expectedOutputs", access = JsonProperty.Access.READ_ONLY)
    public List<String> getOutputs() {
        return outputs;
    }
}
