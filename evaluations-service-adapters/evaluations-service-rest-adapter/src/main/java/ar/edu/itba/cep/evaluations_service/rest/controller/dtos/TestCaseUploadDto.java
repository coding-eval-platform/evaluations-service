package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.models.TestCase.Visibility;
import ar.edu.itba.cep.evaluations_service.rest.controller.validation.NotNullsInIterable;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.IllegalValue;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Data transfer object for receiving {@link TestCase}s data from an API consumer.
 */
public class TestCaseUploadDto {

    /**
     * Indicates whether the test case is public or private.
     */
    @NotNull(message = "The visibility is missing.", payload = MissingValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final TestCase.Visibility visibility;

    /**
     * The timeout of the test case.
     */
    @Positive(message = "The timeout must be positive", payload = IllegalValue.class,
            groups = {
                    Create.class,
                    Update.class
            }
    )
    private final Long timeout;

    /**
     * The inputs of the test case.
     */
    @NotNull(message = "The inputs list must not be null", payload = MissingValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    @NotNullsInIterable(message = "The inputs list contains nulls", payload = IllegalValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final List<String> inputs;

    /**
     * The expected output.
     */
    @NotEmpty(message = "The expected outputs list must not be null nor empty", payload = IllegalValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    @NotNullsInIterable(message = "The expected outputs  list contains nulls", payload = IllegalValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final List<String> expectedOutputs;


    /**
     * Constructor.
     *
     * @param visibility      Indicates whether the test case is public or private.
     * @param timeout         The timeout of the test case.
     * @param inputs          The inputs of the test case.
     * @param expectedOutputs The expected output.
     */
    @JsonCreator
    public TestCaseUploadDto(
            @JsonProperty(
                    value = "visibility",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final Visibility visibility,
            @JsonProperty(
                    value = "timeout",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final Long timeout,
            @JsonProperty(
                    value = "inputs",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final List<String> inputs,
            @JsonProperty(
                    value = "expectedOutputs",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final List<String> expectedOutputs) {
        this.visibility = visibility;
        this.timeout = timeout;
        this.inputs = inputs;
        this.expectedOutputs = expectedOutputs;
    }


    /**
     * @return Indicates whether the test case is public or private.
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * @return The timeout of the test case.
     */
    public Long getTimeout() {
        return timeout;
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


    // ================================================================================================================
    // Validation groups
    // ================================================================================================================

    /**
     * Validation group for the create operation.
     */
    public interface Create {
    }

    /**
     * Validation group for the update operation.
     */
    public interface Update {

    }
}
