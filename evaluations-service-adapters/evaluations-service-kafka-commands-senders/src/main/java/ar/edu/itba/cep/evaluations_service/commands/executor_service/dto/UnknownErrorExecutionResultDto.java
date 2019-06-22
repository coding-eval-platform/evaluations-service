package ar.edu.itba.cep.evaluations_service.commands.executor_service.dto;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.UnknownErrorExecutionResult;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An {@link ExecutionRequestDto} for {@link UnknownErrorExecutionResult}s.
 */
public class UnknownErrorExecutionResultDto implements ExecutionResultDto<UnknownErrorExecutionResult> {

    /**
     * Constructor.
     */
    @JsonCreator
    public UnknownErrorExecutionResultDto() {
    }

    @Override
    public UnknownErrorExecutionResult getAdapted() {
        return new UnknownErrorExecutionResult();
    }
}
