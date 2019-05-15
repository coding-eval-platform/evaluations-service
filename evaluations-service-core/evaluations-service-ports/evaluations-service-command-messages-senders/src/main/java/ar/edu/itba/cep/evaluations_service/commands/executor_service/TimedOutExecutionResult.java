package ar.edu.itba.cep.evaluations_service.commands.executor_service;

/**
 * An {@link ExecutionResult} that corresponds to a timed-out execution.
 */
public class TimedOutExecutionResult implements ExecutionResult {
    @Override
    public String toString() {
        return "TimedOutExecutionResult{}";
    }
}
