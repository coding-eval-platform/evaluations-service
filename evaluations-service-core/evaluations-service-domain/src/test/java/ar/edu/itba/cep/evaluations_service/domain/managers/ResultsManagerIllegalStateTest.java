package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.function.BiConsumer;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link ResultsManager}, containing tests for the illegal state situations
 * (i.e how the manager behaves when operating with exams that are not in a valid state for operating with).
 */
@ExtendWith(MockitoExtension.class)
class ResultsManagerIllegalStateTest extends AbstractResultsManagerTest {

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
    ResultsManagerIllegalStateTest(
            @Mock(name = "testCaseRepository") final TestCaseRepository testCaseRepository,
            @Mock(name = "exerciseSolutionRepository") final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock(name = "resultRepository") final ExerciseSolutionResultRepository exerciseSolutionResultRepository,
            @Mock(name = "eventPublisher") final ApplicationEventPublisher publisher) {
        super(testCaseRepository, exerciseSolutionRepository, exerciseSolutionResultRepository, publisher);
    }


    /**
     * Tests that trying to get all {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}s
     * of an {@link ExerciseSolution} that belongs to an non submitted {@link ExamSolutionSubmission}
     * throws an {@link IllegalEntityStateException}.
     *
     * @param solution An {@link ExerciseSolution} mock (with deep stubs enabled)
     *                 (the one whose {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}s
     *                 are being tried to be retrieved).
     */
    @Test
    void testGetAllResultsForNonSubmittedSubmission(
            @Mock(name = "solution", answer = RETURNS_DEEP_STUBS) final ExerciseSolution solution) {
        testAccessAllEntitiesOfSolutionBelongingToNonSubmittedSubmission(
                solution,
                ResultsManager::getResultsForSolution,
                "Trying to get results for a solution that belongs to a non submitted submission is being allowed"
        );
    }

    /**
     * Tests that trying to get an {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
     * of an {@link ExerciseSolution} and a {@link TestCase}
     * when the {@link ExerciseSolution} belongs to an non submitted {@link ExamSolutionSubmission}
     * throws an {@link IllegalEntityStateException}.
     *
     * @param solution The {@link ExerciseSolution}.
     * @param exercise The {@link Exercise} that belongs to both the {@link ExerciseSolution} and the {@link TestCase}.
     * @param testCase The {@link TestCase}.
     */
    @Test
    void testGetAResultsForNonSubmittedSubmission(
            @Mock(name = "solution", answer = RETURNS_DEEP_STUBS) final ExerciseSolution solution,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testAccessAnEntityOfASolutionBelongingToNonSubmittedSubmission(
                solution,
                exercise,
                testCase,
                ResultsManager::getResultFor,
                "Trying to get a result for a solution and test case" +
                        " when the solution belongs to a non submitted submission is being allowed"
        );
    }


    /**
     * Tests that trying retry all executions of an {@link ExerciseSolution}
     * that belongs to an non submitted {@link ExamSolutionSubmission}
     * throws an {@link IllegalEntityStateException}.
     *
     * @param solution An {@link ExerciseSolution} mock (with deep stubs enabled)
     *                 (the one whose executions are being tried to be retried).
     */
    @Test
    void testRetryAllExecutionsForNonSubmittedSubmission(
            @Mock(name = "solution", answer = RETURNS_DEEP_STUBS) final ExerciseSolution solution) {
        testAccessAllEntitiesOfSolutionBelongingToNonSubmittedSubmission(
                solution,
                ResultsManager::retryForSolution,
                "Trying to retry all executions for a solution" +
                        " that belongs to a non submitted submission is being allowed"
        );
    }

    /**
     * Tests that trying to retry execution for an {@link ExerciseSolution}
     * and a {@link TestCase}
     * when the {@link ExerciseSolution} belongs to an non submitted {@link ExamSolutionSubmission}
     * throws an {@link IllegalEntityStateException}.
     *
     * @param solution The {@link ExerciseSolution}.
     * @param exercise The {@link Exercise} that belongs to both the {@link ExerciseSolution} and the {@link TestCase}.
     * @param testCase The {@link TestCase}.
     */
    @Test
    void testRetryAnExecutionForNonSubmittedSubmission(
            @Mock(name = "solution", answer = RETURNS_DEEP_STUBS) final ExerciseSolution solution,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        testAccessAnEntityOfASolutionBelongingToNonSubmittedSubmission(
                solution,
                exercise,
                testCase,
                ResultsManager::retryForSolutionAndTestCase,
                "Trying to retry execution for a solution and test case" +
                        " when the solution belongs to a non submitted submission is being allowed"
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Performs the illegal entity state test over the given {@code resultsManagerAction},
     * which access all {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
     * for a given {@link ExerciseSolution}.
     *
     * @param solution             An {@link ExerciseSolution} mock (with deep stubs enabled).
     * @param resultsManagerAction The {@link ResultsManager} action being tested.
     *                             Must accept an {@link ExerciseSolution} id.
     * @param message              A message to display in case of failure.
     */
    private void testAccessAllEntitiesOfSolutionBelongingToNonSubmittedSubmission(
            final ExerciseSolution solution,
            final BiConsumer<ResultsManager, Long> resultsManagerAction,
            final String message) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(solution.getSubmission().getState()).thenReturn(ExamSolutionSubmission.State.UNPLACED);
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> resultsManagerAction.accept(resultsManager, solutionId),
                message
        );
        verify(exerciseSolutionRepository, only()).findById(solutionId);
        verifyZeroInteractions(testCaseRepository);
        verifyZeroInteractions(exerciseSolutionResultRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Performs the illegal entity state test over the given {@code resultsManagerAction},
     * which access an {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
     * for a given {@link ExerciseSolution} and {@link TestCase}.
     *
     * @param solution             The {@link ExerciseSolution}.
     * @param exercise             The {@link Exercise}
     *                             that belongs to both the {@link ExerciseSolution} and the {@link TestCase}.
     * @param testCase             The {@link TestCase}.
     * @param resultsManagerAction The {@link ResultsManager} action being tested.
     *                             Must accept an {@link ExerciseSolution}
     *                             and a {@link TestCase} ids.
     * @param message              A message to display in case of failure.
     */
    private void testAccessAnEntityOfASolutionBelongingToNonSubmittedSubmission(
            final ExerciseSolution solution,
            final Exercise exercise,
            final TestCase testCase,
            final ResultsManagerSolutionTestCaseAction resultsManagerAction,
            final String message) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var testCaseId = TestHelper.validTestCaseId();
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(solution.getSubmission().getState()).thenReturn(ExamSolutionSubmission.State.UNPLACED);
        when(solution.getExercise()).thenReturn(exercise);
        when(testCase.getExercise()).thenReturn(exercise);

        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> resultsManagerAction.accept(resultsManager, solutionId, testCaseId),
                message
        );

        verify(exerciseSolutionRepository, only()).findById(solutionId);
        verify(testCaseRepository, only()).findById(testCaseId);
        verifyZeroInteractions(exerciseSolutionResultRepository);
        verifyZeroInteractions(publisher);
    }
}
