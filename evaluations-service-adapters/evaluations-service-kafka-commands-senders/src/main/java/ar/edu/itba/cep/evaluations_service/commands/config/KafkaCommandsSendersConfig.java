package ar.edu.itba.cep.evaluations_service.commands.config;

import com.bellotapps.the_messenger.commons.Message;
import com.bellotapps.the_messenger.producer.BiConsumerMessageProducer;
import com.bellotapps.the_messenger.producer.MessageProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Configuration class for the Kafka Command senders module.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.evaluations_service.commands.*.config"
})
public class KafkaCommandsSendersConfig {

    /**
     * Creates a bean of {@link BiConsumerMessageProducer} that allows sending messages.
     *
     * @param kafkaTemplate The underlying {@link KafkaTemplate} used by the {@link MessageProducer}.
     * @return A bean of {@link MessageProducer}.
     */
    @Bean
    public MessageProducer messageProducer(final KafkaTemplate kafkaTemplate) {
        @SuppressWarnings("unchecked") final var template = (KafkaTemplate<String, Message>) kafkaTemplate;
        return new BiConsumerMessageProducer((message, channel) -> template.send(channel, message));
    }
}
