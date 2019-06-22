package ar.edu.itba.cep.evaluations_service.commands.executor_service.dto;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.TimedOutExecutionResult;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An {@link ExecutionResultDto} for a {@link TimedOutExecutionResult}.
 */
public class TimedOutExecutionResultDto implements ExecutionResultDto<TimedOutExecutionResult> {

    /**
     * Constructor.
     */
    @JsonCreator
    public TimedOutExecutionResultDto() throws IllegalArgumentException {
    }

    @Override
    public TimedOutExecutionResult getAdapted() {
        return new TimedOutExecutionResult();
    }
}
