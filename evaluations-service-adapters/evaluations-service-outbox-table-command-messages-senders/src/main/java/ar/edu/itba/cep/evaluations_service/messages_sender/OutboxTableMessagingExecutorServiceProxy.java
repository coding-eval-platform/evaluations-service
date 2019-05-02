package ar.edu.itba.cep.evaluations_service.messages_sender;

import com.bellotapps.outbox_debezium.commons.Message;
import com.bellotapps.outbox_debezium.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Concrete implementation of {@link ExecutorServiceCommandProxy} using Eventuate Tram Commands.
 */
@Component
public class OutboxTableMessagingExecutorServiceProxy implements ExecutorServiceCommandProxy {

    /**
     * An {@link ExecutorServiceCommandResultHandler} in charge of processing execution results.
     */
    private final ExecutorServiceCommandResultHandler executionResultHandler;

    private final MessageProducer messageProducer;

    private final DataSource dataSource;

    /**
     * Constructor.
     *
     * @param executionResultHandler An {@link ExecutorServiceCommandResultHandler}
     *                               in charge of processing execution results.
     * @param messageProducer
     * @param dataSource
     */
    @Autowired
    public OutboxTableMessagingExecutorServiceProxy(
            final ExecutorServiceCommandResultHandler executionResultHandler,
            final MessageProducer messageProducer, final DataSource dataSource) {
        this.executionResultHandler = executionResultHandler;
        this.messageProducer = messageProducer;
        this.dataSource = dataSource;
    }

    @Override
    public void requestExecution(
            final String code,
            final List<String> inputs,
            final ExecutionResultHandlerData handlerData) {

        final var message = Message.Builder.create()
                .from("EvaluationService")
                .withPayload("Code: " + code + ", Inputs: " + inputs.toString())
                .build();
        messageProducer.send(message, "ExecutorService");

        // TODO: Send the execution request and reply saying that the result references the solution and test case
    }

    public void handle() {
        // TODO: get stuff from response message
        executionResultHandler.processExecution(1, 2, 3, Collections.emptyList(), Collections.emptyList());
    }

    public interface ConnectionSupplier extends Supplier<Connection> {

    }
}
