package ar.edu.itba.cep.evaluations_service.commands.executor_service.dto;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.InitializationErrorExecutionResult;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An {@link ExecutionRequestDto} for {@link InitializationErrorExecutionResult}s.
 */
public class InitializationErrorExecutionResultDto implements ExecutionResultDto<InitializationErrorExecutionResult> {

    /**
     * Constructor.
     */
    @JsonCreator
    public InitializationErrorExecutionResultDto() {
    }

    @Override
    public InitializationErrorExecutionResult getAdapted() {
        return new InitializationErrorExecutionResult();
    }
}
