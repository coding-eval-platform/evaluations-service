package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutorServiceCommandMessageProxy;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionRequestedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResponseArrivedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.executor.models.ExecutionResponse;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;

import static org.mockito.Mockito.*;

/**
 * Test class for the {@link ExecutionManager}.
 */
@ExtendWith(MockitoExtension.class)
class ExecutionManagerTest {

    // ================================================================================================================
    // Mocks
    // ================================================================================================================

    /**
     * An {@link ExecutorServiceCommandMessageProxy} mock that is injected to the {@link ExecutionManager}.
     */
    private final ExecutorServiceCommandMessageProxy executorService;

    /**
     * An {@link ApplicationEventPublisher} mock that is injected to the {@link ExecutionManager}.
     */
    private final ApplicationEventPublisher publisher;


    // ================================================================================================================
    // Solutions Manager
    // ================================================================================================================

    /**
     * The {@link ExecutionManager} being tested.
     */
    private final ExecutionManager executionManager;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param executorService An {@link ExecutorServiceCommandMessageProxy} mock
     *                        that is injected to the {@link ExecutionManager}.
     * @param publisher       An {@link ApplicationEventPublisher} that is injected to the {@link ExecutionManager}.
     */
    ExecutionManagerTest(
            @Mock(name = "executorService") final ExecutorServiceCommandMessageProxy executorService,
            @Mock(name = "publisher") final ApplicationEventPublisher publisher) {
        this.executorService = executorService;
        this.publisher = publisher;
        this.executionManager = new ExecutionManager(executorService, publisher);
    }

    /**
     * Tests the {@link ExecutionManager#executionRequested(ExecutionRequestedEvent)} method.
     *
     * @param event    The {@link ExecutionRequestedEvent} being handled.
     * @param exercise The {@link Exercise} to which the {@code solution} belongs to.
     * @param solution The {@link ExerciseSolution} to be send to execute.
     * @param testCase The {@link TestCase} used to run the solution.
     */
    @Test
    void testExecutionRequested(
            @Mock(name = "event") final ExecutionRequestedEvent event,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase) {

        final var testCaseId = TestHelper.validTestCaseId();
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var code = Faker.instance().lorem().characters();
        final var programArguments = TestHelper.validTestCaseList();
        final var stdin = TestHelper.validTestCaseList();
        final var language = TestHelper.validLanguage();
        final var timeout = TestHelper.validTestCaseTimeout();

        when(testCase.getId()).thenReturn(testCaseId);
        when(testCase.getProgramArguments()).thenReturn(programArguments);
        when(testCase.getStdin()).thenReturn(stdin);
        when(testCase.getTimeout()).thenReturn(timeout);
        when(solution.getId()).thenReturn(solutionId);
        when(solution.getAnswer()).thenReturn(code);
        when(solution.getExercise()).thenReturn(exercise);
        when(exercise.getLanguage()).thenReturn(language);
        when(event.getSolution()).thenReturn(solution);
        when(event.getTestCase()).thenReturn(testCase);

        executionManager.executionRequested(event);

        verifyZeroInteractions(publisher);
        verify(executorService, only())
                .requestExecution(
                        argThat(req ->
                                Objects.equals(code, req.getCode())
                                        && Objects.equals(programArguments, req.getProgramArguments())
                                        && Objects.equals(stdin, req.getStdin())
                                        && Objects.equals(language, req.getLanguage())
                                        && Objects.equals(timeout, req.getTimeout())
                        ),
                        argThat(i -> solutionId == i.getSolutionId() && testCaseId == i.getTestCaseId())
                );
    }

    /**
     * Tests the {@link ExecutionManager#processResponse(long, long, ExecutionResponse)} method.
     *
     * @param executionResponse The {@link ExecutionResponse} to be processed (together with the needed ids).
     */
    @Test
    void testProcessExecution(@Mock(name = "executionResponse") final ExecutionResponse executionResponse) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var testCaseId = TestHelper.validTestCaseId();

        executionManager.processResponse(solutionId, testCaseId, executionResponse);

        verifyZeroInteractions(executorService);
        verify(publisher, only())
                .publishEvent(
                        argThat(
                                (final ExecutionResponseArrivedEvent event) ->
                                        event.getSolutionId() == solutionId
                                                && event.getTestCaseId() == testCaseId
                                                && Objects.equals(event.getResponse(), executionResponse)
                        )
                );

    }
}
