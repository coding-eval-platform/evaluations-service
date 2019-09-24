package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.SolutionAndTestCaseIds;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionRequestedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResponseArrivedEvent;
import ar.edu.itba.cep.executor.api.ExecutionRequestSender;
import ar.edu.itba.cep.executor.api.ExecutionResponseHandler;
import ar.edu.itba.cep.executor.models.ExecutionRequest;
import ar.edu.itba.cep.executor.models.ExecutionResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * A component in charge of sending {@link ExecutionRequest}s and receiving {@link ExecutionResponse}s.
 */
@Component
@AllArgsConstructor
public class ExecutionManager implements ExecutionResponseHandler<SolutionAndTestCaseIds> {

    /**
     * A proxy for the executor service.
     */
    private final ExecutionRequestSender<SolutionAndTestCaseIds> executionRequester;
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
                solution.getCompilerFlags(),
                testCase.getTimeout(),
                solution.getExercise().getLanguage()
        );
        executionRequester.requestExecution(request, SolutionAndTestCaseIds.create(solution.getId(), testCase.getId()));
    }

    @Override
    public void processExecutionResponse(final ExecutionResponse response, final SolutionAndTestCaseIds idData) {
        publisher.publishEvent(
                ExecutionResponseArrivedEvent.create(idData.getSolutionId(), idData.getTestCaseId(), response)
        );
    }
}
