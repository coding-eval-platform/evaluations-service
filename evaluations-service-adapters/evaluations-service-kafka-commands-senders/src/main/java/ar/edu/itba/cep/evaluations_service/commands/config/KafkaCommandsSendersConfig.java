package ar.edu.itba.cep.evaluations_service.commands.config;

import ar.edu.itba.cep.messenger.EnableKafkaMessenger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the Kafka Command senders module.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.evaluations_service.commands.*.config"
})
@EnableKafkaMessenger
public class KafkaCommandsSendersConfig {
}
