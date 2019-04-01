package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.models.TestCase.Visibility;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.IllegalValue;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Data transfer object for receiving {@link TestCase}s data from an API consumer.
 */
public class TestCaseUploadDto {

    /**
     * Indicates whether the test case is public or private.
     */
    @NotNull(message = "The visibility is missing.", payload = MissingValue.class)
    private final TestCase.Visibility visibility;

    /**
     * The inputs of the test case.
     */
    @NotNull(message = "The inputs list is missing.", payload = MissingValue.class)
    @NotEmpty(message = "The inputs list is empty", payload = IllegalValue.class)
    private final List<
            @NotNull(message = "The inputs list contains nulls", payload = IllegalValue.class) String> inputs;

    /**
     * The expected output.
     */
    @NotNull(message = "The expected outputs list is missing.", payload = MissingValue.class)
    @NotEmpty(message = "The expected outputs list is empty", payload = IllegalValue.class)
    private final List<
            @NotNull(message = "The expected outputs list contains nulls", payload = IllegalValue.class) String> outputs;


    /**
     * Constructor.
     *
     * @param visibility Indicates whether the test case is public or private.
     * @param inputs     The inputs of the test case.
     * @param outputs    The expected output.
     */
    @JsonCreator
    public TestCaseUploadDto(
            @JsonProperty(
                    value = "visibility",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final Visibility visibility,
            @JsonProperty(
                    value = "inputs",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final List<String> inputs,
            @JsonProperty(
                    value = "expectedOutputs",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final List<String> outputs) {
        this.visibility = visibility;
        this.inputs = inputs;
        this.outputs = outputs;
    }


    /**
     * @return Indicates whether the test case is public or private.
     */
    public Visibility getVisibility() {
        return visibility;
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
    public List<String> getOutputs() {
        return outputs;
    }
}
