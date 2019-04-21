package ar.edu.itba.cep.evaluations_service.messages_sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Concrete implementation of {@link ExecutorServiceCommandProxy} using Eventuate Tram Commands.
 */
@Component
public class OutboxTableMessagingExecutorServiceProxy implements ExecutorServiceCommandProxy {

    /**
     * An {@link ExecutorServiceCommandResultHandler} in charge of processing execution results.
     */
    private final ExecutorServiceCommandResultHandler executionResultHandler;

    /**
     * Constructor.
     *
     * @param executionResultHandler An {@link ExecutorServiceCommandResultHandler}
     *                               in charge of processing execution results.
     */
    @Autowired
    public OutboxTableMessagingExecutorServiceProxy(final ExecutorServiceCommandResultHandler executionResultHandler) {
        this.executionResultHandler = executionResultHandler;
    }

    @Override
    public void requestExecution(
            final String code,
            final List<String> inputs,
            final ExecutionResultHandlerData handlerData) {
        // TODO: Send the execution request and reply saying that the result references the solution and test case
    }

    public void handle() {
        // TODO: get stuff from response message
        executionResultHandler.processExecution(1, 2, 3, Collections.emptyList(), Collections.emptyList());
    }
}
