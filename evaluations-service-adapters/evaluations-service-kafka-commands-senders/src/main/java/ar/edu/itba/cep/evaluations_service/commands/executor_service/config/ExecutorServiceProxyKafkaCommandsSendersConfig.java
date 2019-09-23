package ar.edu.itba.cep.evaluations_service.commands.executor_service.config;

import ar.edu.itba.cep.executor.client.EnableExecutorServiceCommandMessages;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the executor service proxy for command messages.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.evaluations_service.commands.executor_service"
})
@EnableExecutorServiceCommandMessages
public class ExecutorServiceProxyKafkaCommandsSendersConfig {
}
