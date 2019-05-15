package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.dto.ExecutionResultDto;
import com.bellotapps.the_messenger.commons.Message;
import com.bellotapps.the_messenger.commons.payload.PayloadDeserializer;
import com.bellotapps.the_messenger.consumer.DeserializerMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adapts an {@link ExecutionResultProcessor} into the Kafka Command senders infrastructure,
 * waiting for command results.
 * Is implemented as a {@link DeserializerMessageHandler} of {@link ExecutionResultDto} that takes data from the
 * request dto and calls the {@link ExecutionResultProcessor#processExecution(long, long, ExecutionResult)} method,
 * getting ids from the received {@link Message}'s headers.
 */
@Component
public class ExecutionResultHandler extends DeserializerMessageHandler<ExecutionResultDto> {

    /**
     * The {@link ExecutionResultProcessor} that is in charge of processing an execution result.
     */
    private final ExecutionResultProcessor executionResultProcessor;


    /**
     * Constructor.
     *
     * @param executionRequestDtoDeserializer A {@link PayloadDeserializer} of {@link ExecutionResultDto}.
     * @param executionResultProcessor        The {@link ExecutionResultProcessor}
     *                                        that is in charge of processing an execution result.
     */
    @Autowired
    public ExecutionResultHandler(
            final PayloadDeserializer<ExecutionResultDto> executionRequestDtoDeserializer,
            final ExecutionResultProcessor executionResultProcessor) {
        super(executionRequestDtoDeserializer);
        this.executionResultProcessor = executionResultProcessor;
    }


    @Override
    protected void andThen(final ExecutionResultDto executionResultDto, final Message message) {
        final var testCaseId = message.headerValue(Constants.TEST_CASE_ID_HEADER)
                .map(Long::parseLong)
                .orElseThrow(() -> new IllegalArgumentException("Missing test case id")); // TODO: throw?
        final var solutionId = message.headerValue(Constants.SOLUTION_ID_HEADER)
                .map(Long::parseLong)
                .orElseThrow(() -> new IllegalArgumentException("Missing solution id")); // TODO: throw?
        executionResultProcessor.processExecution(
                solutionId,
                testCaseId,
                executionResultDto.getAdapted()
        );
    }
}
