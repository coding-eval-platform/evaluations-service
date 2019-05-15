package ar.edu.itba.cep.evaluations_service.commands.executor_service.dto;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionRequest;
import ar.edu.itba.cep.evaluations_service.models.Language;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data transfer object for sending execution requests.
 */
public class ExecutionRequestDto {

    /**
     * The code to be run.
     */
    private final String code;

    /**
     * The input arguments to be passed to the execution.
     */
    private final List<String> inputs;

    /**
     * The time given to execute, in milliseconds..
     */
    private final Long timeout;

    /**
     * The programming language in which the {@link #code} is written.
     */
    private final Language language;


    /**
     * Constructor.
     *
     * @param executionRequest The {@link ExecutionRequest} from where data is taken.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public ExecutionRequestDto(final ExecutionRequest executionRequest) {
        this.code = executionRequest.getCode();
        this.inputs = executionRequest.getInputs();
        this.timeout = executionRequest.getTimeout();
        this.language = executionRequest.getLanguage();
    }


    /**
     * @return The code to be run.
     */
    @JsonProperty(value = "code", access = JsonProperty.Access.READ_ONLY)
    public String getCode() {
        return code;
    }

    /**
     * @return The input arguments to be passed to the execution.
     */
    @JsonProperty(value = "inputs", access = JsonProperty.Access.READ_ONLY)
    public List<String> getInputs() {
        return inputs;
    }

    /**
     * @return The time given to execute, in milliseconds.
     */
    @JsonProperty(value = "timeout", access = JsonProperty.Access.READ_ONLY)
    public Long getTimeout() {
        return timeout;
    }

    /**
     * @return The programming language in which the {@link #getCode()} is written.
     */
    @JsonProperty(value = "language", access = JsonProperty.Access.READ_ONLY)
    public Language getLanguage() {
        return language;
    }
}
