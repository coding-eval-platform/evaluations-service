package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.executor.models.ExecutionRequest;

/**
 * A port out of the application that allows sending async command messages to the executor service.
 */
public interface ExecutorServiceCommandMessageProxy {

    /**
     * Requests an execution to the executor service.
     *
     * @param executionRequest The {@link ExecutionRequest} to be sent to the executor service.
     * @param replyData        Data indicating how the reply must be handled.
     */
    void requestExecution(final ExecutionRequest executionRequest, final ExecutionResponseReplyData replyData);
}
