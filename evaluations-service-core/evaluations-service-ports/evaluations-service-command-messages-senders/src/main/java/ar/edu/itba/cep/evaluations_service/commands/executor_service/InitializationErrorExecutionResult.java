package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import lombok.ToString;

/**
 * Represents an {@link ExecutionResult} for an execution that failed to initialize.
 */
@ToString(doNotUseGetters = true, callSuper = true)
public class InitializationErrorExecutionResult implements ExecutionResult {
}
