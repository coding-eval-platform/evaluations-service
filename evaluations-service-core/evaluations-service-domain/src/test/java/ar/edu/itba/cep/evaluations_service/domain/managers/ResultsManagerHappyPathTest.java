package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.*;
import ar.edu.itba.cep.evaluations_service.domain.events.ExamSolutionSubmittedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionRequestedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResultArrivedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.*;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link ResultsManager}, containing tests for the happy paths
 * (i.e how the manager behaves when operating with valid values, entity states, etc.).
 */
@ExtendWith(MockitoExtension.class)
class ResultsManagerHappyPathTest extends AbstractResultsManagerTest {

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
    ResultsManagerHappyPathTest(
            @Mock(name = "testCaseRepository") final TestCaseRepository testCaseRepository,
            @Mock(name = "exerciseSolutionRepository") final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock(name = "resultRepository") final ExerciseSolutionResultRepository exerciseSolutionResultRepository,
            @Mock(name = "eventPublisher") final ApplicationEventPublisher publisher) {
        super(testCaseRepository, exerciseSolutionRepository, exerciseSolutionResultRepository, publisher);
    }


    // ================================================================================================================
    // ResultsService methods
    // ================================================================================================================

    // ================================================
    // Results retrieval
    // ================================================

    /**
     * Tests the retrieval of all the {@link ExerciseSolutionResult} of a given {@link ExerciseSolution}.
     *
     * @param solution A {@link ExerciseSolution} mock (with deep stubs enabled)
     *                 that owns the returned {@link ExerciseSolutionResult}s.
     * @param results  A {@link List} of {@link ExerciseSolutionResult} (the one being retrieved).
     */
    @Test
    void testGetAllResultsForSolution(
            @Mock(name = "solution", answer = RETURNS_DEEP_STUBS) final ExerciseSolution solution,
            @Mock(name = "results") final List<ExerciseSolutionResult> results) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        when(solution.getSubmission().getState()).thenReturn(ExamSolutionSubmission.State.SUBMITTED);
        when(exerciseSolutionResultRepository.find(solution)).thenReturn(results);

        Assertions.assertEquals(
                results,
                resultsManager.getResultsForSolution(solutionId),
                "Getting all results for a solution is not returned the list returned by the repository"
        );

        verify(exerciseSolutionRepository, only()).findById(solutionId);
        verifyZeroInteractions(testCaseRepository);
        verify(exerciseSolutionResultRepository, only()).find(solution);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests retrieving an {@link ExerciseSolutionResult} for an {@link ExerciseSolution} and {@link TestCase}.
     *
     * @param result An {@link ExerciseSolutionResult} mock (the one being returned).
     */
    @Test
    void testGetResultsForSolutionAndTestCase(@Mock(name = "result") final ExerciseSolutionResult result) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var testCaseId = TestHelper.validTestCaseId();
        when(exerciseSolutionRepository.existsById(solutionId)).thenReturn(true);
        when(testCaseRepository.existsById(testCaseId)).thenReturn(true);
        when(exerciseSolutionResultRepository.find(solutionId, testCaseId)).thenReturn(Optional.of(result));

        Assertions.assertEquals(
                result,
                resultsManager.getResultFor(solutionId, testCaseId),
                "Getting a result for a solution and test case is not returned the one returned by the repository"
        );

        verify(exerciseSolutionRepository, only()).existsById(solutionId);
        verify(testCaseRepository, only()).existsById(testCaseId);
        verify(exerciseSolutionResultRepository, only()).find(solutionId, testCaseId);
        verifyZeroInteractions(publisher);
    }


    // ================================================
    // Retrying execution
    // ================================================

    /**
     * Tests retrying executions for an {@link ExerciseSolution} that is not answered.
     *
     * @param solution An {@link ExerciseSolution} mock (the one without answer).
     */
    @Test
    void testRetryExecutionForNotAnsweredSolution(
            @Mock(name = "solution", answer = RETURNS_DEEP_STUBS) final ExerciseSolution solution) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        when(solution.getSubmission().getState()).thenReturn(ExamSolutionSubmission.State.SUBMITTED);
        when(solution.getAnswer()).thenReturn(null);

        resultsManager.retryForSolution(solutionId);

        verify(exerciseSolutionRepository, only()).findById(solutionId);
        verifyZeroInteractions(testCaseRepository);
        verifyZeroInteractions(exerciseSolutionResultRepository);
        verifyZeroInteractions(publisher);
    }


    /**
     * Tests retrying executions for an {@link ExerciseSolution} that is answered, which contains
     * marked and unmarked results.
     *
     * @param solution        An {@link ExerciseSolution} mock (the one whose execution must be retried).
     * @param testCase        The {@link TestCase} belonging to the result that is marked.
     * @param markedResult    The {@link ExerciseSolutionResult} that is marked.
     * @param nonMarkedResult The {@link ExerciseSolutionResult} that is not marked.
     */

    @Test
    void testRetryExecutionForAnsweredSolution(
            @Mock(name = "solution", answer = RETURNS_DEEP_STUBS) final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "markedResult") final ExerciseSolutionResult markedResult,
            @Mock(name = "nonMarkedResult") final ExerciseSolutionResult nonMarkedResult) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(solution.getSubmission().getState()).thenReturn(ExamSolutionSubmission.State.SUBMITTED);
        when(solution.getAnswer()).thenReturn(createAnswer());
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        when(markedResult.isMarked()).thenReturn(true);
        when(markedResult.getSolution()).thenReturn(solution);
        when(markedResult.getTestCase()).thenReturn(testCase);
        doNothing().when(markedResult).unmark();
        when(nonMarkedResult.isMarked()).thenReturn(false);
        when(exerciseSolutionResultRepository.save(nonMarkedResult)).thenReturn(nonMarkedResult);
        when(exerciseSolutionResultRepository.find(solution)).thenReturn(List.of(markedResult, nonMarkedResult));

        resultsManager.retryForSolution(solutionId);

        verify(markedResult, times(1)).unmark();
        verify(exerciseSolutionRepository, only()).findById(solutionId);
        verifyZeroInteractions(testCaseRepository);
        verify(exerciseSolutionResultRepository, times(1)).find(solution);
        verify(exerciseSolutionResultRepository, times(1)).save(markedResult);
        verifyNoMoreInteractions(exerciseSolutionResultRepository);
        verify(publisher, only()).publishEvent(argThat(eventIsWellFormed(solution, testCase)));
    }


    /**
     * Tests retrying execution for an {@link ExerciseSolution} and a {@link TestCase},
     * when the corresponding {@link ExerciseSolutionResult} is already marked.
     *
     * @param result An {@link ExerciseSolutionResult} mock (the one being checked if answered).
     */
    @Test
    void testRetryExecutionForSolutionAndTestCaseWithUnmarkedResult(
            @Mock(name = "result") final ExerciseSolutionResult result) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var testCaseId = TestHelper.validTestCaseId();
        when(exerciseSolutionRepository.existsById(solutionId)).thenReturn(true);
        when(testCaseRepository.existsById(testCaseId)).thenReturn(true);
        when(exerciseSolutionResultRepository.find(solutionId, testCaseId)).thenReturn(Optional.of(result));
        when(result.isMarked()).thenReturn(false);

        resultsManager.retryForSolutionAndTestCase(solutionId, testCaseId);

        verify(exerciseSolutionRepository, only()).existsById(solutionId);
        verify(testCaseRepository, only()).existsById(testCaseId);
        verify(exerciseSolutionResultRepository, only()).find(solutionId, testCaseId);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests retrying execution for an {@link ExerciseSolution} and a {@link TestCase},
     * when the said {@link ExerciseSolution} is not answered.
     *
     * @param result An {@link ExerciseSolutionResult} mock
     *               (the one owning the {@link ExerciseSolution} without answer).
     */
    @Test
    void testRetryExecutionForSolutionAndTestCaseWithNotAnsweredSolution(
            @Mock(name = "result", answer = RETURNS_DEEP_STUBS) final ExerciseSolutionResult result) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var testCaseId = TestHelper.validTestCaseId();
        when(exerciseSolutionRepository.existsById(solutionId)).thenReturn(true);
        when(testCaseRepository.existsById(testCaseId)).thenReturn(true);
        when(exerciseSolutionResultRepository.find(solutionId, testCaseId)).thenReturn(Optional.of(result));
        when(result.isMarked()).thenReturn(true);
        when(result.getSolution().getAnswer()).thenReturn(null);

        resultsManager.retryForSolutionAndTestCase(solutionId, testCaseId);

        verify(exerciseSolutionRepository, only()).existsById(solutionId);
        verify(testCaseRepository, only()).existsById(testCaseId);
        verify(exerciseSolutionResultRepository, only()).find(solutionId, testCaseId);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests retrying execution for an {@link ExerciseSolution} and a {@link TestCase},
     * when the said {@link ExerciseSolution} is not answered.
     *
     * @param result An {@link ExerciseSolutionResult} mock
     *               (the one owning the {@link ExerciseSolution} without answer).
     */
    @Test
    void testRetryExecutionForSolutionAndTestCaseInIdealConditions(
            @Mock(name = "result") final ExerciseSolutionResult result,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        final var testCaseId = TestHelper.validTestCaseId();
        when(exerciseSolutionRepository.existsById(solutionId)).thenReturn(true);
        when(testCaseRepository.existsById(testCaseId)).thenReturn(true);
        when(exerciseSolutionResultRepository.find(solutionId, testCaseId)).thenReturn(Optional.of(result));
        when(exerciseSolutionResultRepository.save(result)).thenReturn(result);
        when(result.isMarked()).thenReturn(true);
        when(result.getSolution()).thenReturn(solution);
        when(result.getTestCase()).thenReturn(testCase);
        when(solution.getAnswer()).thenReturn(createAnswer());
        doNothing().when(result).unmark();

        resultsManager.retryForSolutionAndTestCase(solutionId, testCaseId);

        verify(result, times(1)).unmark();
        verify(exerciseSolutionRepository, only()).existsById(solutionId);
        verify(testCaseRepository, only()).existsById(testCaseId);
        verify(exerciseSolutionResultRepository, times(1)).find(solutionId, testCaseId);
        verify(exerciseSolutionResultRepository, times(1)).save(result);
        verifyNoMoreInteractions(exerciseSolutionResultRepository);
        verify(publisher, only()).publishEvent(argThat(eventIsWellFormed(solution, testCase)));
    }


    // ================================================================================================================
    // ExamSolutionSubmittedEvent reception
    // ================================================================================================================

    /**
     * Performs an {@link ExamSolutionSubmittedEvent} test for the case in which there is an {@link ExerciseSolution}
     * with answer, and another one without.
     *
     * @param event                 A {@link ExamSolutionSubmittedEvent} mock that is received by the manager.
     * @param submission            An {@link ExamSolutionSubmission} mock.
     * @param exercise1             An {@link Exercise} mock.
     * @param testCase1a            A {@link TestCase} mock.
     * @param testCase1b            Another {@link TestCase} mock.
     * @param exercise2             Another {@link Exercise} mock.
     * @param testCase2a            Another {@link TestCase} mock.
     * @param testCase2b            Another {@link TestCase} mock.
     * @param solutionWithAnswer    An {@link ExerciseSolution} mock to be configured in order to return an answer
     *                              when {@link ExerciseSolution#getAnswer()} is called.
     * @param solutionWithoutAnswer An {@link ExerciseSolution} mock to be configured in order to return a null value
     *                              when {@link ExerciseSolution#getAnswer()} is called.
     */
    @Test
    void testExamSolutionSubmissionEventWithSolutionsWithAndWithoutAnswer(
            @Mock(name = "event") final ExamSolutionSubmittedEvent event,
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "exercise1") final Exercise exercise1,
            @Mock(name = "testCase1a") final TestCase testCase1a,
            @Mock(name = "testCase1b") final TestCase testCase1b,
            @Mock(name = "exercise2") final Exercise exercise2,
            @Mock(name = "testCase2a") final TestCase testCase2a,
            @Mock(name = "testCase2b") final TestCase testCase2b,
            @Mock(name = "solutionWithAnswer") final ExerciseSolution solutionWithAnswer,
            @Mock(name = "solutionWithoutAnswer") final ExerciseSolution solutionWithoutAnswer) {
        testExamSolutionSubmissionEvent(
                event,
                submission,
                SolutionData.create(
                        exercise1,
                        testCase1a,
                        testCase1b,
                        solutionWithAnswer,
                        createAnswer()
                ),
                SolutionData.create(
                        exercise2,
                        testCase2a,
                        testCase2b,
                        solutionWithoutAnswer,
                        null
                )
        );
    }

    /**
     * Performs an {@link ExamSolutionSubmittedEvent} test for the case in which all {@link ExerciseSolution} has
     * answer.
     *
     * @param event      A {@link ExamSolutionSubmittedEvent} mock that is received by the manager.
     * @param submission An {@link ExamSolutionSubmission} mock.
     * @param exercise1  An {@link Exercise} mock.
     * @param testCase1a A {@link TestCase} mock.
     * @param testCase1b Another {@link TestCase} mock.
     * @param exercise2  Another {@link Exercise} mock.
     * @param testCase2a Another {@link TestCase} mock.
     * @param testCase2b Another {@link TestCase} mock.
     * @param solution1  An {@link ExerciseSolution} mock.
     * @param solution2  Another {@link ExerciseSolution} mock.
     */
    @Test
    void testExamSolutionSubmissionEventWithSolutionsWithAnswer(
            @Mock(name = "event") final ExamSolutionSubmittedEvent event,
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "exercise1") final Exercise exercise1,
            @Mock(name = "testCase1a") final TestCase testCase1a,
            @Mock(name = "testCase1b") final TestCase testCase1b,
            @Mock(name = "exercise2") final Exercise exercise2,
            @Mock(name = "testCase2a") final TestCase testCase2a,
            @Mock(name = "testCase2b") final TestCase testCase2b,
            @Mock(name = "solution1") final ExerciseSolution solution1,
            @Mock(name = "solution2") final ExerciseSolution solution2) {
        testExamSolutionSubmissionEvent(
                event,
                submission,
                SolutionData.create(
                        exercise1,
                        testCase1a,
                        testCase1b,
                        solution1,
                        createAnswer()
                ),
                SolutionData.create(
                        exercise2,
                        testCase2a,
                        testCase2b,
                        solution2,
                        createAnswer()
                )
        );
    }

    /**
     * Performs an {@link ExamSolutionSubmittedEvent} test for the case in which any {@link ExerciseSolution} has
     * answer.
     *
     * @param event      A {@link ExamSolutionSubmittedEvent} mock that is received by the manager.
     * @param submission An {@link ExamSolutionSubmission} mock.
     * @param exercise1  An {@link Exercise} mock.
     * @param testCase1a A {@link TestCase} mock.
     * @param testCase1b Another {@link TestCase} mock.
     * @param exercise2  Another {@link Exercise} mock.
     * @param testCase2a Another {@link TestCase} mock.
     * @param testCase2b Another {@link TestCase} mock.
     * @param solution1  An {@link ExerciseSolution} mock.
     * @param solution2  Another {@link ExerciseSolution} mock.
     */
    @Test
    void testExamSolutionSubmissionEventWithSolutionsWithoutAnswer(
            @Mock(name = "event") final ExamSolutionSubmittedEvent event,
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "exercise1") final Exercise exercise1,
            @Mock(name = "testCase1a") final TestCase testCase1a,
            @Mock(name = "testCase1b") final TestCase testCase1b,
            @Mock(name = "exercise2") final Exercise exercise2,
            @Mock(name = "testCase2a") final TestCase testCase2a,
            @Mock(name = "testCase2b") final TestCase testCase2b,
            @Mock(name = "solution1") final ExerciseSolution solution1,
            @Mock(name = "solution2") final ExerciseSolution solution2) {
        testExamSolutionSubmissionEvent(
                event,
                submission,
                SolutionData.create(
                        exercise1,
                        testCase1a,
                        testCase1b,
                        solution1,
                        null
                ),
                SolutionData.create(
                        exercise2,
                        testCase2a,
                        testCase2b,
                        solution2,
                        null
                )
        );
    }


    // ================================================================================================================
    // ExecutionResultArrivedEvent
    // ================================================================================================================

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * in which the {@link ExecutionResult} is a {@link FinishedExecutionResult}, with a non zero exit code.
     *
     * @param event           A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param executionResult A {@link FinishedExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithFinishedExecutionResultWithNonZeroExitCode(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solutionResult", answer = RETURNS_DEEP_STUBS) final ExerciseSolutionResult solutionResult,
            @Mock(name = "executionResult") final FinishedExecutionResult executionResult) {
        testProcessExecution(
                event,
                solutionResult,
                r -> {
                },
                executionResult,
                r -> when(r.getExitCode()).thenReturn(TestHelper.validNonZeroExerciseSolutionExitCode()),
                ExerciseSolutionResult.Result.FAILED
        );
    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * in which the {@link ExecutionResult} is a {@link FinishedExecutionResult}, with a zero exit code,
     * but a non empty standard error output.
     *
     * @param event           A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param executionResult A {@link TimedOutExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithFinishedExecutionResultWithZeroExitCodeAndNonEmptyStderr(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solutionResult", answer = RETURNS_DEEP_STUBS) final ExerciseSolutionResult solutionResult,
            @Mock(name = "executionResult") final FinishedExecutionResult executionResult) {
        testProcessExecution(
                event,
                solutionResult,
                r -> {
                },
                executionResult,
                r -> {
                    when(r.getExitCode()).thenReturn(0);
                    when(r.getStderr()).thenReturn(TestHelper.validExerciseSolutionResultList());

                },
                ExerciseSolutionResult.Result.FAILED
        );
    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * in which the {@link ExecutionResult} is a {@link FinishedExecutionResult}, with a zero exit code,
     * no standard error output and standard output not equal to the expected output.
     *
     * @param event           A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param executionResult A {@link TimedOutExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithFinishedExecutionResultWithZeroExitCodeEmptyStderrAndDifferentOutput(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solutionResult", answer = RETURNS_DEEP_STUBS) final ExerciseSolutionResult solutionResult,
            @Mock(name = "executionResult") final FinishedExecutionResult executionResult) {
        final var expectedOutputs = TestHelper.validExerciseSolutionResultList();
        final var anotherOutputs = new LinkedList<>(expectedOutputs);
        Collections.shuffle(anotherOutputs);
        testProcessExecution(
                event,
                solutionResult,
                r -> when(r.getTestCase().getExpectedOutputs()).thenReturn(expectedOutputs),
                executionResult,
                r -> {
                    when(r.getExitCode()).thenReturn(0);
                    when(r.getStderr()).thenReturn(Collections.emptyList());
                    when(r.getStdout()).thenReturn(anotherOutputs);

                },
                ExerciseSolutionResult.Result.FAILED
        );
    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * in which the {@link ExecutionResult} is a {@link FinishedExecutionResult}, with a zero exit code,
     * no standard error output and standard output equal to the expected output.
     *
     * @param event           A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param executionResult A {@link FinishedExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithFinishedExecutionResultWithZeroExitCodeAndEmptyStderrAndExpectedOutput(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solutionResult", answer = RETURNS_DEEP_STUBS) final ExerciseSolutionResult solutionResult,
            @Mock(name = "executionResult") final FinishedExecutionResult executionResult) {
        final var expectedOutputs = TestHelper.validExerciseSolutionResultList();
        testProcessExecution(
                event,
                solutionResult,
                r -> when(r.getTestCase().getExpectedOutputs()).thenReturn(expectedOutputs),
                executionResult,
                r -> {
                    when(r.getExitCode()).thenReturn(0);
                    when(r.getStderr()).thenReturn(Collections.emptyList());
                    when(r.getStdout()).thenReturn(expectedOutputs);

                },
                ExerciseSolutionResult.Result.APPROVED
        );
    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * in which the {@link ExecutionResult} is a {@link TimedOutExecutionResult}.
     *
     * @param event           A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param executionResult A {@link TimedOutExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithTimedOutExecutionResult(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solutionResult", answer = RETURNS_DEEP_STUBS) final ExerciseSolutionResult solutionResult,
            @Mock(name = "executionResult") final TimedOutExecutionResult executionResult) {
        testProcessExecution(
                event,
                solutionResult,
                r -> {
                },
                executionResult,
                r -> {
                },
                ExerciseSolutionResult.Result.TIMED_OUT
        );
    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * in which the {@link ExecutionResult} is a {@link CompileErrorExecutionResult}.
     *
     * @param event           A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param executionResult A {@link CompileErrorExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithNotCompiledExecutionResult(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solutionResult", answer = RETURNS_DEEP_STUBS) final ExerciseSolutionResult solutionResult,
            @Mock(name = "executionResult") final CompileErrorExecutionResult executionResult) {
        testProcessExecution(
                event,
                solutionResult,
                r -> {
                },
                executionResult,
                r -> {
                },
                ExerciseSolutionResult.Result.NOT_COMPILED
        );
    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * in which the {@link ExecutionResult} is an {@link InitializationErrorExecutionResult}.
     *
     * @param event           A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param executionResult An {@link InitializationErrorExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithInitializationErrorExecutionResult(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solutionResult", answer = RETURNS_DEEP_STUBS) final ExerciseSolutionResult solutionResult,
            @Mock(name = "executionResult") final InitializationErrorExecutionResult executionResult) {
        testProcessExecution(
                event,
                solutionResult,
                r -> {
                },
                executionResult,
                r -> {
                },
                ExerciseSolutionResult.Result.INITIALIZATION_ERROR
        );
    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * in which the {@link ExecutionResult} is an {@link UnknownErrorExecutionResult}.
     *
     * @param event           A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param executionResult An {@link UnknownErrorExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithUnknownErrorExecutionResult(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solutionResult", answer = RETURNS_DEEP_STUBS) final ExerciseSolutionResult solutionResult,
            @Mock(name = "executionResult") final UnknownErrorExecutionResult executionResult) {
        testProcessExecution(
                event,
                solutionResult,
                r -> {
                },
                executionResult,
                r -> {
                },
                ExerciseSolutionResult.Result.UNKNOWN_ERROR
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Creates an {@link ExerciseSolution} answer with text.
     *
     * @return An {@link ExerciseSolution} answer
     */
    private static String createAnswer() {
        return Faker.instance().lorem().characters();
    }

    /**
     * Performs an {@link ExamSolutionSubmittedEvent} received test.
     *
     * @param event         A {@link ExamSolutionSubmittedEvent} mock that is received by the manager.
     * @param submission    An {@link ExamSolutionSubmission} mock.
     * @param solutionData1 A {@link SolutionData} with mocks.
     * @param solutionData2 Another {@link SolutionData} with mocks.
     */
    void testExamSolutionSubmissionEvent(
            final ExamSolutionSubmittedEvent event,
            final ExamSolutionSubmission submission,
            final SolutionData solutionData1,
            final SolutionData solutionData2) {
        solutionData1.setupSolutionMock();
        solutionData2.setupSolutionMock();
        solutionData1.setupRepositories(testCaseRepository);
        solutionData2.setupRepositories(testCaseRepository);
        when(event.getSubmission()).thenReturn(submission);
        when(exerciseSolutionRepository.getExerciseSolutions(submission))
                .thenReturn(List.of(solutionData1.getSolution(), solutionData2.getSolution()));

        resultsManager.examSolutionSubmitted(event);

        verify(exerciseSolutionRepository, only()).getExerciseSolutions(submission);
        solutionData1.verifyTestCaseRepositoryAccesses(testCaseRepository);
        solutionData2.verifyTestCaseRepositoryAccesses(testCaseRepository);
        verifyNoMoreInteractions(testCaseRepository);
        solutionData1.verifyExerciseSolutionResultRepositoryAccesses(exerciseSolutionResultRepository);
        solutionData2.verifyExerciseSolutionResultRepositoryAccesses(exerciseSolutionResultRepository);
        verifyNoMoreInteractions(exerciseSolutionResultRepository);
        solutionData1.verifyExecutionRequestEventPublishing(publisher);
        solutionData2.verifyExecutionRequestEventPublishing(publisher);
        verifyNoMoreInteractions(publisher);


    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test.
     *
     * @param event                     An {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param solutionResult            An {@link ExerciseSolutionResult} mock which is the one being affected.
     * @param solutionResultConfigurer  A {@link Consumer} of {@link ExerciseSolutionResult} intended to configure
     *                                  the {@link ExerciseSolutionResult} mock (e.g to set the expected outputs).
     * @param executionResult           An {@link ExecutionResult} mock which is returned by the event.
     * @param executionResultConfigurer A {@link Consumer} of a subclass of {@link ExecutionResult} of type {@code R}
     *                                  intended to configure the {@link ExecutionResult} mock
     *                                  (e.g to set the exit code).
     * @param expectedResult            The expected {@link ExerciseSolutionResult.Result}
     *                                  (i.e which is set in the {@link ExecutionResult}).
     * @param <R>                       The concrete type of {@link ExecutionResult}.
     */
    private <R extends ExecutionResult> void testProcessExecution(
            final ExecutionResultArrivedEvent event,
            final ExerciseSolutionResult solutionResult,
            final Consumer<ExerciseSolutionResult> solutionResultConfigurer,
            final R executionResult,
            final Consumer<R> executionResultConfigurer,
            final ExerciseSolutionResult.Result expectedResult) {

        final var testCaseId = TestHelper.validTestCaseId();
        final var solutionId = TestHelper.validExerciseSolutionId();

        // Configure the execution result mock
        executionResultConfigurer.accept(executionResult);

        // Configure the event
        when(event.getTestCaseId()).thenReturn(testCaseId);
        when(event.getSolutionId()).thenReturn(solutionId);
        when(event.getResult()).thenReturn(executionResult);

        // Configure the solution result mock
        doNothing().when(solutionResult).mark(expectedResult);
        solutionResultConfigurer.accept(solutionResult);

        // Setup repository
        when(exerciseSolutionResultRepository.find(solutionId, testCaseId)).thenReturn(Optional.of(solutionResult));
        when(exerciseSolutionResultRepository.save(solutionResult)).thenReturn(solutionResult);

        // Call the method to be tested
        resultsManager.receiveExecutionResult(event);

        // Verifications
        verifyZeroInteractions(exerciseSolutionRepository);
        verifyZeroInteractions(testCaseRepository);
        verify(solutionResult, times(1)).mark(expectedResult);
        verify(exerciseSolutionResultRepository, times(1)).find(solutionId, testCaseId);
        verify(exerciseSolutionResultRepository, times(1)).save(solutionResult);
        verifyNoMoreInteractions(exerciseSolutionRepository);
        verifyZeroInteractions(publisher);
    }


    /**
     * A container class for a solution structure mocks
     * (i.e an {@link Exercise}, some {@link TestCase}s and an {@link ExerciseSolution}).
     */
    @Getter
    @ToString(doNotUseGetters = true)
    @EqualsAndHashCode(doNotUseGetters = true)
    @AllArgsConstructor(staticName = "create")
    private static final class SolutionData {

        /**
         * The {@link Exercise} mock.
         */
        private final Exercise exercise;
        /**
         * A {@link TestCase} mock.
         */
        private final TestCase testCase1;
        /**
         * Another {@link TestCase} mock.
         */
        private final TestCase testCase2;
        /**
         * An {@link ExerciseSolution} mock.
         */
        private final ExerciseSolution solution;
        /**
         * The answer in the {@link ExerciseSolution}.
         */
        private final String answer;

        /**
         * Setups the {@link ExerciseSolution} mock.
         */
        private void setupSolutionMock() {
            when(solution.getExercise()).thenReturn(exercise);
            when(solution.getAnswer()).thenReturn(answer);
        }

        /**
         * Setups the given {@code testCaseRepository}, in order to retrieve the {@link TestCase}s.
         *
         * @param testCaseRepository The {@link TestCaseRepository} mock to be set up.
         */
        private void setupRepositories(final TestCaseRepository testCaseRepository) {
            when(testCaseRepository.getAllTestCases(exercise)).thenReturn(List.of(testCase1, testCase2));
        }

        /**
         * Performs verification over the given {@code testCaseRepository}
         * (i.e checks that the {@link TestCaseRepository#getAllTestCases(Exercise)} method is called using the
         * {@link Exercise} in this container class).
         *
         * @param testCaseRepository The {@link TestCaseRepository} to be verified.
         */
        private void verifyTestCaseRepositoryAccesses(final TestCaseRepository testCaseRepository) {
            verify(testCaseRepository, times(1)).getAllTestCases(exercise);
        }

        /**
         * Performs verification over the given {@code repository}
         * (i.e checks that the {@link ExerciseSolutionResultRepository#save(ExerciseSolutionResult)} method
         * is called using one {@link ExerciseSolutionResult}s with the {@code solution} and {@code testCase1},
         * and with another for the {@code testCase2}.
         *
         * @param repository The {@link ExerciseSolutionResultRepository} to be verified.
         */
        private void verifyExerciseSolutionResultRepositoryAccesses(final ExerciseSolutionResultRepository repository) {
            final var stateMatcher = isAnswered() ? isNotMarkedMatcher() : isNotAnsweredMatcher();
            verify(repository, times(1)).save(argThat(and(resultIsWellFormed(solution, testCase1), stateMatcher)));
            verify(repository, times(1)).save(argThat(and(resultIsWellFormed(solution, testCase2), stateMatcher)));
        }

        /**
         * Performs verification over the given {@code publisher}
         * (i.e checks that the {@link ApplicationEventPublisher#publishEvent(Object)} method
         * is called only if the solution has an answer with the corresponding events.
         *
         * @param publisher The {@link ApplicationEventPublisher} to be checked.
         */
        private void verifyExecutionRequestEventPublishing(final ApplicationEventPublisher publisher) {
            if (isAnswered()) {
                verify(publisher, times(1)).publishEvent(argThat(eventIsWellFormed(solution, testCase1)));
                verify(publisher, times(1)).publishEvent(argThat(eventIsWellFormed(solution, testCase2)));
            } else {
                verifyZeroInteractions(publisher);
            }
        }

        /**
         * Convenient method to check "whether the solution has an answer".
         *
         * @return {@code true} "if it has", or {@code false} otherwise.
         */
        private boolean isAnswered() {
            return StringUtils.hasText(answer);
        }


        /**
         * Combines the two given {@link ArgumentMatcher}s with and "and" operation.
         *
         * @param wellFormedMatcher An {@link ArgumentMatcher} to check if the result is well formed.
         * @param stateMatcher      An {@link ArgumentMatcher} to check the state of the result.
         * @return The combined {@link ArgumentMatcher}.
         */
        private static ArgumentMatcher<ExerciseSolutionResult> and(
                final ArgumentMatcher<ExerciseSolutionResult> wellFormedMatcher,
                final ArgumentMatcher<ExerciseSolutionResult> stateMatcher) {
            return arg -> wellFormedMatcher.matches(arg) && stateMatcher.matches(arg);
        }

        /**
         * An {@link ArgumentMatcher} for {@link ExerciseSolutionResult} to check whether it contains
         * the given {@code solution} and {@code testCase}.
         *
         * @param solution The {@link ExerciseSolution} to be checked.
         * @param testCase The {@link TestCase} to be checked.
         * @return The {@link ArgumentMatcher}.
         */
        private static ArgumentMatcher<ExerciseSolutionResult> resultIsWellFormed(
                final ExerciseSolution solution,
                final TestCase testCase) {
            return result -> result.getSolution().equals(solution) && result.getTestCase().equals(testCase);
        }

        /**
         * An {@link ArgumentMatcher} for {@link ExerciseSolutionResult} to check whether it contains
         * a {@link ExerciseSolutionResult.Result#NOT_ANSWERED} result.
         *
         * @return The {@link ArgumentMatcher}.
         */
        private static ArgumentMatcher<ExerciseSolutionResult> isNotMarkedMatcher() {
            return result -> !result.isMarked();
        }

        /**
         * An {@link ArgumentMatcher} for {@link ExerciseSolutionResult} to check whether it contains
         * a {@link ExerciseSolutionResult.Result#NOT_ANSWERED} result.
         *
         * @return The {@link ArgumentMatcher}.
         */
        private static ArgumentMatcher<ExerciseSolutionResult> isNotAnsweredMatcher() {
            return result -> result.getResult() == ExerciseSolutionResult.Result.NOT_ANSWERED;
        }
    }

    /**
     * An {@link ArgumentMatcher} for {@link ExecutionRequestedEvent} to check whether the said event contains
     * the given {@code solution} and {@code testCase}.
     *
     * @param solution The {@link ExerciseSolution} to be checked.
     * @param testCase The {@link TestCase} to be checked.
     * @return The {@link ArgumentMatcher}.
     */
    private static ArgumentMatcher<ExecutionRequestedEvent> eventIsWellFormed(
            final ExerciseSolution solution,
            final TestCase testCase) {
        return event -> event.getSolution().equals(solution) && event.getTestCase().equals(testCase);
    }
}
