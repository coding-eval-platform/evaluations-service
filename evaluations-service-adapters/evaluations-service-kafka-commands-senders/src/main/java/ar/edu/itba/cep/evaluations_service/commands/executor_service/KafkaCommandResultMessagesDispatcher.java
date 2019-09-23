package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.executor.client.ExecutionResponseDispatcher;
import com.bellotapps.the_messenger.commons.Message;
import com.bellotapps.the_messenger.consumer.MessageHandler;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka command reply messages dispatcher.
 */
@Component
@AllArgsConstructor
public class KafkaCommandResultMessagesDispatcher {

    /**
     * The {@link MessageHandler} in charge of dispatching actions based on received messages.
     */
    private final ExecutionResponseDispatcher<SolutionAndTestCaseIds> messageDispatcher;


    /**
     * Receives a {@link Message}s and delegates it handling to the {@code messageHandler}.
     *
     * @param message The received {@link Message}.
     */
    @KafkaListener(
            topics = {
                    "${executor-service.command-messages.request-execution.reply-channel}",
            },
            autoStartup = "true"
    )
    public void dispatch(final Message message) {
        this.messageDispatcher.dispatch(message);
    }
}
