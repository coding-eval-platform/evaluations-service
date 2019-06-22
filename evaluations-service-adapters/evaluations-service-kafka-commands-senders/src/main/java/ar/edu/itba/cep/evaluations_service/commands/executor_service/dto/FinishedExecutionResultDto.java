package ar.edu.itba.cep.evaluations_service.commands.executor_service.dto;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.FinishedExecutionResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * An {@link ExecutionResultDto} for a {@link FinishedExecutionResult}.
 */
public class FinishedExecutionResultDto implements ExecutionResultDto<FinishedExecutionResult> {


    /**
     * The {@link FinishedExecutionResult} corresponding to this DTO.
     */
    private final FinishedExecutionResult adapted;


    /**
     * Constructor.
     *
     * @param exitCode The execution's exit code.
     * @param stdout   A {@link List} of {@link String}s
     *                 that were sent to standard output by the program being executed.
     *                 Each {@link String} in the {@link List} is a line that was printed in standard output.
     * @param stderr   A {@link List} of {@link String}s
     *                 that were sent to standard error output by the program being executed.
     *                 Each {@link String} in the {@link List}
     *                 is a line that was printed in standard error output.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    @JsonCreator
    public FinishedExecutionResultDto(
            @JsonProperty(value = "exitCode", access = JsonProperty.Access.WRITE_ONLY) final int exitCode,
            @JsonProperty(value = "stdout", access = JsonProperty.Access.WRITE_ONLY) final List<String> stdout,
            @JsonProperty(value = "stderr", access = JsonProperty.Access.WRITE_ONLY) final List<String> stderr)
            throws IllegalArgumentException {
        this.adapted = new FinishedExecutionResult(exitCode, stdout, stderr);
    }

    @Override
    public FinishedExecutionResult getAdapted() {
        return adapted;
    }
}
