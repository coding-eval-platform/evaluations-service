package ar.edu.itba.cep.evaluations_service.commands.executor_service.dto;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult;
import ar.edu.itba.cep.evaluations_service.commands.executor_service.TimedOutExecutionResult;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An {@link ExecutionResultDto} for a {@link TimedOutExecutionResult}.
 */
public class TimedOutExecutionResultDto implements ExecutionResultDto {

    /**
     * Constructor.
     */
    @JsonCreator
    public TimedOutExecutionResultDto() throws IllegalArgumentException {
    }

    @Override
    public ExecutionResult getAdapted() {
        return new TimedOutExecutionResult();
    }
}
