package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.dto.ExecutionRequestDto;
import ar.edu.itba.cep.evaluations_service.commands.executor_service.dto.ExecutionResultDto;
import com.bellotapps.the_messenger.commons.payload.PayloadSerializer;
import com.bellotapps.the_messenger.producer.MessageBuilder;
import com.bellotapps.the_messenger.producer.MessageBuilderFactory;
import com.bellotapps.the_messenger.producer.basic_factories.GenericMessageBuilderFactory;

/**
 * A {@link MessageBuilderFactory} of {@link ExecutionResultDto},
 * which overrides all the created {@link MessageBuilder}s adding a reply channel header.
 */
public final class ExecutionRequestDtoMessageBuilderFactory extends GenericMessageBuilderFactory<ExecutionRequestDto> {

    /**
     * Constructor.
     *
     * @param sender         The sender to be configured to the created {@link MessageBuilder}s.
     * @param serializer     The {@link PayloadSerializer} to be configured to the created {@link MessageBuilder}s.
     * @param messageCreator The {@link MessageBuilder.MessageCreator}
     *                       to be configured to the created {@link MessageBuilder}s.
     */
    public ExecutionRequestDtoMessageBuilderFactory(
            final String sender,
            final PayloadSerializer<ExecutionRequestDto> serializer,
            final MessageBuilder.MessageCreator messageCreator) {
        super(sender, serializer, messageCreator);
    }

    @Override
    public MessageBuilder<ExecutionRequestDto> create() {
        return super.create()
                .withHeader(Constants.REPLY_CHANNEL_HEADER, Constants.REPLY_CHANNEL);
    }
}
