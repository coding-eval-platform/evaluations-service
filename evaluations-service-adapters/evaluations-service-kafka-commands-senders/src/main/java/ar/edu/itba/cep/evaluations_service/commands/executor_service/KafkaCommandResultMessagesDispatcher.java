package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import com.bellotapps.the_messenger.commons.Message;
import com.bellotapps.the_messenger.consumer.BuiltInMessageHandler;
import com.bellotapps.the_messenger.consumer.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka command reply messages dispatcher.
 */
@Component
public class KafkaCommandResultMessagesDispatcher {

    /**
     * The {@link MessageHandler} in charge of dispatching actions based on received messages.
     */
    private final MessageHandler messageHandler;

    @Autowired
    public KafkaCommandResultMessagesDispatcher(final MessageHandler executionResponseHandler) {
        this.messageHandler = BuiltInMessageHandler.Builder.create()
                .configureTypedMessageHandlers()
                .handleReplyMessageWith(executionResponseHandler)
                .continueWithParentBuilder()
                .build();
    }


    /**
     * Receives a {@link Message}s and delegates it handling to the {@code messageHandler}.
     *
     * @param message The received {@link Message}.
     */
    @KafkaListener(
            topics = {
                    Constants.REPLY_CHANNEL,
            },
            autoStartup = "true"
    )
    public void dispatch(final Message message) {
        this.messageHandler.handle(message);
    }
}
