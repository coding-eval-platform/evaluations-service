package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.events.ExamSolutionSubmittedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResultArrivedEvent;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ResultsManager}, containing tests for the illegal arguments situations
 * (i.e how the manager behaves when operating with invalid values).
 */
@ExtendWith(MockitoExtension.class)
class ResultsManagerIllegalArgumentsTest extends AbstractResultsManagerTest {

    /**
     * Constructor.
     *
     * @param testCaseRepository               A {@link TestCaseRepository}
     *                                         that is injected to the {@link ResultsManager}.
     * @param exerciseSolutionRepository       An {@link ExerciseSolutionRepository}
     *                                         that is injected to the {@link ExamManager}.
     * @param exerciseSolutionResultRepository A {@link ExerciseSolutionResultRepository}
     *                                         that is injected to the {@link ResultsManager}.
     * @param publisher                        An {@link ApplicationEventPublisher}
     *                                         that is injected to the {@link ExamManager}.
     */
    ResultsManagerIllegalArgumentsTest(
            @Mock(name = "testCaseRepository") final TestCaseRepository testCaseRepository,
            @Mock(name = "exerciseSolutionRepository") final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock(name = "resultRepository") final ExerciseSolutionResultRepository exerciseSolutionResultRepository,
            @Mock(name = "eventPublisher") final ApplicationEventPublisher publisher) {
        super(testCaseRepository, exerciseSolutionRepository, exerciseSolutionResultRepository, publisher);
    }


    // ================================================================================================================
    // ExamSolutionSubmittedEvent reception
    // ================================================================================================================

    /**
     * Tests the reception of a {@code null} {@link ExamSolutionSubmittedEvent}.
     */
    @Test
    void testNullExamSolutionSubmissionEvent() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> resultsManager.examSolutionSubmitted(null),
                "The reception of a null result event does not throw an IllegalArgumentException"
        );
        verifyNoInteractionsWithMocks();
    }

    /**
     * Tests the reception of an {@link ExamSolutionSubmittedEvent}
     * that returns a {@code null} {@link ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission}.
     */
    @Test
    void testExamSolutionSubmissionEventWithNullSubmission(
            @Mock(name = "event") final ExamSolutionSubmittedEvent event) {
        when(event.getSubmission()).thenReturn(null);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> resultsManager.examSolutionSubmitted(event),
                "The reception of an event with a null submission does not throw an IllegalArgumentException"
        );
        verifyNoInteractionsWithMocks();
    }

    /**
     * Tests the reception of a {@code null} {@link ExecutionResultArrivedEvent}.
     */
    @Test
    void testNullExecutionResultArrivedEvent() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> resultsManager.receiveExecutionResult(null),
                "The reception of a null result event does not throw an IllegalArgumentException"
        );
        verifyNoInteractionsWithMocks();
    }

    /**
     * Tests the reception of an {@link ExecutionResultArrivedEvent}
     * that returns a {@code null} {@link ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult}.
     */
    @Test
    void testExecutionResultArrivedEventWithNullResult(@Mock(name = "event") final ExecutionResultArrivedEvent event) {
        when(event.getResult()).thenReturn(null);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> resultsManager.receiveExecutionResult(event),
                "The reception of an event with a null result does not throw an IllegalArgumentException"
        );
        verifyNoInteractionsWithMocks();
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Performs verifications over all the mocks, checking that there are no interactions with them.
     */
    private void verifyNoInteractionsWithMocks() {
        verifyZeroInteractions(testCaseRepository);
        verifyZeroInteractions(exerciseSolutionRepository);
        verifyZeroInteractions(exerciseSolutionResultRepository);
        verifyZeroInteractions(publisher);
    }
}
