package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import lombok.ToString;

/**
 * An {@link ExecutionResult} that corresponds to a timed-out execution.
 */
@ToString(doNotUseGetters = true, callSuper = true)
public final class TimedOutExecutionResult implements ExecutionResult {
}
