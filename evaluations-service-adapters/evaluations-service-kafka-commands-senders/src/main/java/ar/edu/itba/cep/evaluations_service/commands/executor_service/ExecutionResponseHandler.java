package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.executor.dtos.ExecutionResponseDto;
import ar.edu.itba.cep.executor.models.ExecutionResponse;
import com.bellotapps.the_messenger.commons.Message;
import com.bellotapps.the_messenger.commons.payload.PayloadDeserializer;
import com.bellotapps.the_messenger.consumer.DeserializerMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adapts an {@link ExecutionResponseProcessor} into the Kafka Command senders infrastructure,
 * waiting for command results.
 * Is implemented as a {@link DeserializerMessageHandler} of {@link ExecutionResponseDto} that takes data from the
 * request dto and calls the {@link ExecutionResponseProcessor#processResponse(long, long, ExecutionResponse)} method,
 * getting ids from the received {@link Message}'s headers.
 */
@Component
public class ExecutionResponseHandler extends DeserializerMessageHandler<ExecutionResponseDto> {

    /**
     * The {@link ExecutionResponseProcessor} that is in charge of processing an {@link ExecutionResponse}.
     */
    private final ExecutionResponseProcessor executionResponseProcessor;


    /**
     * Constructor.
     *
     * @param executionRequestDtoDeserializer A {@link PayloadDeserializer} of {@link ExecutionResponseDto}.
     * @param executionResponseProcessor      The {@link ExecutionResponseProcessor}
     *                                        that is in charge of processing an {@link ExecutionResponse}.
     */
    @Autowired
    public ExecutionResponseHandler(
            final PayloadDeserializer<ExecutionResponseDto> executionRequestDtoDeserializer,
            final ExecutionResponseProcessor executionResponseProcessor) {
        super(executionRequestDtoDeserializer);
        this.executionResponseProcessor = executionResponseProcessor;
    }


    @Override
    protected void andThen(final ExecutionResponseDto executionResponseDto, final Message message) {
        final var testCaseId = message.headerValue(Constants.TEST_CASE_ID_HEADER)
                .map(Long::parseLong)
                .orElseThrow(() -> new IllegalArgumentException("Missing test case id")); // TODO: throw?
        final var solutionId = message.headerValue(Constants.SOLUTION_ID_HEADER)
                .map(Long::parseLong)
                .orElseThrow(() -> new IllegalArgumentException("Missing solution id")); // TODO: throw?
        executionResponseProcessor.processResponse(solutionId, testCaseId, executionResponseDto.getExecutionResponse());
    }
}
