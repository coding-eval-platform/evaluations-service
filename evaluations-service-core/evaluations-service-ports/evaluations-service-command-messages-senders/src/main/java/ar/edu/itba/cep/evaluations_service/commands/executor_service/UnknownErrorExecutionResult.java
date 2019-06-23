package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import lombok.ToString;

/**
 * Represents an {@link ExecutionResult} for an execution that failed unexpectedly.
 */
@ToString(doNotUseGetters = true, callSuper = true)
public class UnknownErrorExecutionResult implements ExecutionResult {
}
