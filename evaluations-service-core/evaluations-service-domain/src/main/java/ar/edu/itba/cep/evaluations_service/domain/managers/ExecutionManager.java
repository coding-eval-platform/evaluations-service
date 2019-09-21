package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResponseProcessor;
import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResponseReplyData;
import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutorServiceCommandMessageProxy;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionRequestedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResponseArrivedEvent;
import ar.edu.itba.cep.executor.models.ExecutionRequest;
import ar.edu.itba.cep.executor.models.ExecutionResponse;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * A component in charge of sending {@link ExecutionRequest}s and receiving {@link ExecutionResponse}s.
 */
@Component
@AllArgsConstructor
public class ExecutionManager implements ExecutionResponseProcessor {

    /**
     * A proxy for the executor service.
     */
    private final ExecutorServiceCommandMessageProxy executorService;
    /**
     * An {@link ApplicationEventPublisher} to publish relevant events to the rest of the application's components.
     */
    private final ApplicationEventPublisher publisher;


    /**
     * Processes the given {@code event} by sending an {@link ExecutionRequest} to the executor service.
     *
     * @param event The {@link ExecutionRequestedEvent} to be processed.
     */
    @EventListener(ExecutionRequestedEvent.class)
    public void executionRequested(final ExecutionRequestedEvent event) {
        final var solution = event.getSolution();
        final var testCase = event.getTestCase();
        final var request = new ExecutionRequest(
                solution.getAnswer(),
                testCase.getProgramArguments(),
                testCase.getStdin(),
                null,
                testCase.getTimeout(),
                solution.getExercise().getLanguage()
        );
        final var replyData = new ExecutionResponseReplyData(solution.getId(), testCase.getId());
        executorService.requestExecution(request, replyData);
    }

    @Override
    public void processResponse(final long solutionId, final long testCaseId, final ExecutionResponse executionResponse)
            throws NoSuchEntityException, IllegalArgumentException {
        publisher.publishEvent(ExecutionResponseArrivedEvent.create(solutionId, testCaseId, executionResponse));
    }
}
