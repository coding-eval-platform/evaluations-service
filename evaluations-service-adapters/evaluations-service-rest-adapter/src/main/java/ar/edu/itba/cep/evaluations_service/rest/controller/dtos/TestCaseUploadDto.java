package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.models.TestCase.Visibility;
import ar.edu.itba.cep.evaluations_service.rest.controller.validation.NotNullsInIterable;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.IllegalValue;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Data transfer object for receiving {@link TestCase}s data from an API consumer.
 */
@Getter
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
     * The program arguments list of the test case.
     */
    @NotNullsInIterable(message = "The program arguments list contains nulls", payload = IllegalValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final List<String> programArguments;

    /**
     * The stdin list of the test case.
     */
    @NotNullsInIterable(message = "The stdin list contains nulls", payload = IllegalValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final List<String> stdin;

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
     * @param visibility       Indicates whether the test case is public or private.
     * @param timeout          The timeout of the test case.
     * @param programArguments The program arguments list of the test case.
     * @param stdin            The stdin list of the test case.
     * @param expectedOutputs  The expected output.
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
                    value = "programArguments",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final List<String> programArguments,
            @JsonProperty(
                    value = "stdin",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final List<String> stdin,
            @JsonProperty(
                    value = "expectedOutputs",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final List<String> expectedOutputs) {
        this.visibility = visibility;
        this.timeout = timeout;
        this.programArguments = programArguments;
        this.stdin = stdin;
        this.expectedOutputs = expectedOutputs;
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
