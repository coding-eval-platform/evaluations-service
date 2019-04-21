package ar.edu.itba.cep.evaluations_service.messages_sender.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Juan Marcos Bellini on 2019-04-16.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.evaluations_service.messages_sender"
})
public class OutboxTableMessagingConfig {
}
