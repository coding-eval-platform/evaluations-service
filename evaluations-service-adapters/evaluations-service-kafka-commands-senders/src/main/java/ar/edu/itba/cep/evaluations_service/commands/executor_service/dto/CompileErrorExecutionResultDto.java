package ar.edu.itba.cep.evaluations_service.commands.executor_service.dto;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.CompileErrorExecutionResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * An {@link ExecutionRequestDto} for {@link CompileErrorExecutionResult}s.
 */
public class CompileErrorExecutionResultDto implements ExecutionResultDto<CompileErrorExecutionResult> {

    /**
     * The {@link CompileErrorExecutionResult} corresponding to this DTO.
     */
    private final CompileErrorExecutionResult adapted;

    /**
     * Constructor.
     *
     * @param compilerErrors A {@link List} of {@link String}s that were reported by the compiler on failure.
     * @throws IllegalArgumentException If the given {@code compileErrorExecutionResult} is {@code null}.
     */
    @JsonCreator
    public CompileErrorExecutionResultDto(
            @JsonProperty(value = "compilerErrors", access = JsonProperty.Access.WRITE_ONLY) final List<String> compilerErrors)
            throws IllegalArgumentException {
        this.adapted = new CompileErrorExecutionResult(compilerErrors);
    }

    @Override
    public CompileErrorExecutionResult getAdapted() {
        return adapted;
    }
}
