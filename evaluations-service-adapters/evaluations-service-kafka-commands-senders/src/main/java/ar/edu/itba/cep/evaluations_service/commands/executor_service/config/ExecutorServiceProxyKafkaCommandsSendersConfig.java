package ar.edu.itba.cep.evaluations_service.commands.executor_service.config;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionRequestDtoMessageBuilderFactory;
import ar.edu.itba.cep.executor.dtos.ExecutionRequestDto;
import ar.edu.itba.cep.executor.dtos.ExecutionResponseDto;
import com.bellotapps.the_messenger.commons.payload.PayloadDeserializer;
import com.bellotapps.the_messenger.commons.payload.PayloadSerializer;
import com.bellotapps.the_messenger.json.JacksonJsonPayloadDeserializer;
import com.bellotapps.the_messenger.json.JacksonJsonPayloadSerializer;
import com.bellotapps.the_messenger.producer.MessageBuilderFactory;
import com.bellotapps.the_messenger.transport.json.jackson.JacksonMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the executor service proxy for command messages.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.evaluations_service.commands.executor_service"
})
public class ExecutorServiceProxyKafkaCommandsSendersConfig {

    /**
     * Creates a bean of {@link JacksonJsonPayloadSerializer} of {@link ExecutionRequestDto}.
     *
     * @return A bean of {@link PayloadSerializer} of {@link ExecutionResponseDto}.
     */
    @Bean
    public PayloadSerializer<ExecutionRequestDto> executionRequestDtoPayloadSerializer() {
        return new JacksonJsonPayloadSerializer<>(new ObjectMapper(), ExecutionRequestDto.class);
    }

    /**
     * Creates a bean of {@link JacksonJsonPayloadDeserializer} of {@link ExecutionResponseDto}.
     *
     * @return A bean of {@link PayloadDeserializer} of {@link ExecutionResponseDto}.
     */
    @Bean
    public PayloadDeserializer<ExecutionResponseDto> executionResponseDtoPayloadDeserializer() {
        return new JacksonJsonPayloadDeserializer<>(new ObjectMapper(), ExecutionResponseDto.class);
    }

    /**
     * Creates a bean of {@link ExecutionRequestDtoMessageBuilderFactory}.
     *
     * @param executionRequestDtoPayloadSerializer A {@link PayloadSerializer} of {@link ExecutionRequestDto}.
     * @return A bean of {@link MessageBuilderFactory} of {@link ExecutionRequestDto}.
     */
    @Bean
    public MessageBuilderFactory<ExecutionRequestDto> executionRequestMessageBuilderFactory(
            final PayloadSerializer<ExecutionRequestDto> executionRequestDtoPayloadSerializer) {
        return new ExecutionRequestDtoMessageBuilderFactory(
                "EvaluationsService",
                executionRequestDtoPayloadSerializer,
                JacksonMessage::new);
    }
}
