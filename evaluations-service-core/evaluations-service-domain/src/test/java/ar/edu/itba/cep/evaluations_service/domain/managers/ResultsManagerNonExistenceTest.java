package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResultArrivedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Test class for {@link ResultsManager},
 * containing tests for the non-existence condition
 * (i.e how the manager behaves when trying to operate over entities that do not exist).
 */
@ExtendWith(MockitoExtension.class)
class ResultsManagerNonExistenceTest extends AbstractResultsManagerTest {

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
    ResultsManagerNonExistenceTest(
            @Mock(name = "testCaseRepository") final TestCaseRepository testCaseRepository,
            @Mock(name = "exerciseSolutionRepository") final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock(name = "resultRepository") final ExerciseSolutionResultRepository exerciseSolutionResultRepository,
            @Mock(name = "eventPublisher") final ApplicationEventPublisher publisher) {
        super(testCaseRepository, exerciseSolutionRepository, exerciseSolutionResultRepository, publisher);
    }


    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * checking the condition in which the {@link ExerciseSolutionResultRepository} returns an empty {@link Optional}
     * when trying to retrieve the corresponding
     * {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
     * for the data that arrived with the event.
     *
     * @param event           The {@link ExecutionResultArrivedEvent} that contains ids of entities that do not exist.
     * @param executionResult An {@link ExecutionResult} to be retrieved from the event.
     */
    @Test
    void test(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "executionResult") final ExecutionResult executionResult) {
        final var solutionId = TestHelper.validExerciseId();
        final var testCaseId = TestHelper.validTestCaseId();

        // Configure the event
        when(event.getTestCaseId()).thenReturn(testCaseId);
        when(event.getSolutionId()).thenReturn(solutionId);
        when(event.getResult()).thenReturn(executionResult);

        // Setup repository
        when(exerciseSolutionResultRepository.find(solutionId, testCaseId)).thenReturn(Optional.empty());

        // Call the method to be tested
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> resultsManager.receiveExecutionResult(event),
                "Trying to handle an execution result arrived event that contains data of entities" +
                        " that do not exist does not throw a NoSuchEntityException"
        );

        // Verifications
        verify(exerciseSolutionResultRepository, only()).find(solutionId, testCaseId);
        verifyZeroInteractions(exerciseSolutionRepository);
        verifyZeroInteractions(testCaseRepository);
        verifyZeroInteractions(publisher);
    }
}
