package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResultArrivedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
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
import java.util.function.BiConsumer;

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
     * Tests that trying to get all results for an {@link ExerciseSolution} that does not exist
     * throws a {@link NoSuchEntityException}
     */
    @Test
    void testGetAllResultsForNonExistenceSolution() {
        testAllForNonExistenceSolution(
                ResultsManager::getResultsForSolution,
                "Trying to get results for a solution that does not exists does not throw a NoSuchEntityException");
    }

    /**
     * Tests that trying to get a result for an {@link ExerciseSolution} and a {@link TestCase}
     * when the {@link ExerciseSolution} does not exist, throws a {@link NoSuchEntityException}
     */
    @Test
    void testGetAResultForNonExistenceSolution() {
        testSomethingForNonExistenceSolution(
                ResultsManager::getResultFor,
                "Trying to get results for a solution that does not exists" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to get a result for an {@link ExerciseSolution} and a {@link TestCase}
     * when the {@link TestCase} does not exist, throws a {@link NoSuchEntityException}
     */
    @Test
    void testGetAResultForNonExistenceTestCase() {
        testSomethingForNonExistenceTestCase(
                ResultsManager::getResultFor,
                "Trying to get results for a solution that does not exists" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to get a result for an {@link ExerciseSolution} and a {@link TestCase} that are not related,
     * throws a {@link NoSuchEntityException}
     *
     * @param solution The {@link ExerciseSolution}.
     * @param testCase The {@link TestCase}.
     */
    @Test
    void testGetAResultForForNonRelatedSolutionAndTestCase(
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "solutionExercise") final Exercise exercise1,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "testCaseExercise") final Exercise exercise2) {
        testSomethingForNonRelatedSolutionAndTestCase(
                solution,
                exercise1,
                testCase,
                exercise2,
                ResultsManager::getResultFor,
                "Trying to get results for a solution and test case that are not related" +
                        " does not throw a NoSuchEntityException"
        );
    }


    /**
     * Tests that trying to retry all executions for an {@link ExerciseSolution} that does not exist
     * throws a {@link NoSuchEntityException}
     */
    @Test
    void testRetryAllExecutionForNonExistenceSolution() {
        testAllForNonExistenceSolution(
                ResultsManager::retryForSolution,
                "Trying to retry all executions for a solution that does not exists" +
                        " does not throw a NoSuchEntityException");
    }

    /**
     * Tests that trying to retry an execution for an {@link ExerciseSolution} and a {@link TestCase}
     * when the {@link ExerciseSolution} does not exist, throws a {@link NoSuchEntityException}
     */
    @Test
    void testRetryAnExecutionForNonExistenceSolution() {
        testSomethingForNonExistenceSolution(
                ResultsManager::retryForSolutionAndTestCase,
                "Trying to retry execution for a solution that does not exists" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to get a result for an {@link ExerciseSolution} and a {@link TestCase}
     * when the {@link TestCase} does not exist, throws a {@link NoSuchEntityException}
     */
    @Test
    void testRetryAnExecutionForNonExistenceTestCase() {
        testSomethingForNonExistenceTestCase(
                ResultsManager::retryForSolutionAndTestCase,
                "Trying to retry execution for a test case that does not exists" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to get a result for an {@link ExerciseSolution} and a {@link TestCase} that are not related,
     * throws a {@link NoSuchEntityException}
     *
     * @param solution The {@link ExerciseSolution}.
     * @param testCase The {@link TestCase}.
     */
    @Test
    void testRetryAnExecutionForNonRelatedSolutionAndTestCase(
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "solutionExercise") final Exercise exercise1,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "testCaseExercise") final Exercise exercise2) {
        testSomethingForNonRelatedSolutionAndTestCase(
                solution,
                exercise1,
                testCase,
                exercise2,
                ResultsManager::retryForSolutionAndTestCase,
                "Trying to retry execution for a solution and test case that are not related" +
                        " does not throw a NoSuchEntityException"
        );
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
    void testReceiveExecutionResult(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "executionResult") final ExecutionResult executionResult) {
        final var solutionId = TestHelper.validExerciseSolutionId();
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


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Performs a test for a {@link ResultsManager}'s action that accesses entities belonging to
     * a non existence {@link ExerciseSolution}, checking that is throws a {@link NoSuchEntityException}.
     *
     * @param resultsManagerAction The {@link ResultsManager}'s action.
     * @param message              An assertion message to display in case of failure.
     */
    private void testAllForNonExistenceSolution(
            final BiConsumer<ResultsManager, Long> resultsManagerAction,
            final String message) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> resultsManagerAction.accept(resultsManager, solutionId),
                message
        );

        verify(exerciseSolutionRepository, only()).findById(solutionId);
        verifyZeroInteractions(testCaseRepository);
        verifyZeroInteractions(exerciseSolutionResultRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Performs a test for a {@link ResultsManager}'s action that accesses an entity belonging to an
     * {@link ExerciseSolution} and a {@link TestCase}, when the {@link ExerciseSolution} does not exist.
     *
     * @param resultsManagerAction The {@link ResultsManager}'s action.
     * @param message              An assertion message to display in case of failure.
     */
    private void testSomethingForNonExistenceSolution(
            final ResultsManagerSolutionTestCaseAction resultsManagerAction,
            final String message) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var testCaseId = TestHelper.validExerciseId();
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> resultsManagerAction.accept(resultsManager, solutionId, testCaseId),
                message
        );

        verify(exerciseSolutionRepository, atMost(1)).existsById(solutionId);
        verify(testCaseRepository, atMost(1)).existsById(testCaseId);
        verifyNoMoreInteractions(exerciseSolutionResultRepository, testCaseRepository);
        verifyZeroInteractions(exerciseSolutionResultRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Performs a test for a {@link ResultsManager}'s action that accesses an entity belonging to an
     * {@link ExerciseSolution} and a {@link TestCase}, when the {@link TestCase} does not exist.
     *
     * @param resultsManagerAction The {@link ResultsManager}'s action.
     * @param message              An assertion message to display in case of failure.
     */
    private void testSomethingForNonExistenceTestCase(
            final ResultsManagerSolutionTestCaseAction resultsManagerAction,
            final String message) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var testCaseId = TestHelper.validExerciseId();
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> resultsManagerAction.accept(resultsManager, solutionId, testCaseId),
                message
        );

        verify(exerciseSolutionRepository, atMost(1)).existsById(solutionId);
        verify(testCaseRepository, atMost(1)).existsById(testCaseId);
        verifyNoMoreInteractions(exerciseSolutionResultRepository, testCaseRepository);
        verifyZeroInteractions(exerciseSolutionResultRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Performs a test for a {@link ResultsManager}'s action that accesses an entity belonging to an
     * {@link ExerciseSolution} and a {@link TestCase}, when they are not related.
     *
     * @param solution             The {@link ExerciseSolution}.
     * @param exercise1            The {@link Exercise} to which the solution belongs.
     * @param exercise2            The {@link Exercise} to which the test case belongs.
     * @param testCase             The {@link TestCase}.
     * @param resultsManagerAction The {@link ResultsManager}'s action.
     * @param message              An assertion message to display in case of failure.
     */
    private void testSomethingForNonRelatedSolutionAndTestCase(
            final ExerciseSolution solution,
            final Exercise exercise1,
            final TestCase testCase,
            final Exercise exercise2,
            final ResultsManagerSolutionTestCaseAction resultsManagerAction,
            final String message) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var testCaseId = TestHelper.validExerciseId();
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(solution.getExercise()).thenReturn(exercise1);
        when(testCase.getExercise()).thenReturn(exercise2);

        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> resultsManagerAction.accept(resultsManager, solutionId, testCaseId),
                message
        );

        verify(exerciseSolutionRepository, atMost(1)).findById(solutionId);
        verify(testCaseRepository, atMost(1)).findById(testCaseId);
        verifyNoMoreInteractions(exerciseSolutionResultRepository, testCaseRepository);
        verifyZeroInteractions(exerciseSolutionResultRepository);
        verifyZeroInteractions(publisher);
    }
}
