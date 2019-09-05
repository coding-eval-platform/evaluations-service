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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
                        createAnswer(),
                        this::verifyExecutionRequestEventsArePublishedForSolutionsWithAnswer
                ),
                SolutionData.create(
                        exercise2,
                        testCase2a,
                        testCase2b,
                        solutionWithoutAnswer,
                        null,
                        this::verifyFailedResultIsSavedForSolutionsWithoutAnswer
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
                        createAnswer(),
                        this::verifyExecutionRequestEventsArePublishedForSolutionsWithAnswer
                ),
                SolutionData.create(
                        exercise2,
                        testCase2a,
                        testCase2b,
                        solution2,
                        createAnswer(),
                        this::verifyExecutionRequestEventsArePublishedForSolutionsWithAnswer
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
                        null,
                        this::verifyFailedResultIsSavedForSolutionsWithoutAnswer
                ),
                SolutionData.create(
                        exercise2,
                        testCase2a,
                        testCase2b,
                        solution2,
                        null,
                        this::verifyFailedResultIsSavedForSolutionsWithoutAnswer
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
     * @param solution        A {@link ExerciseSolution} mock which is referenced in the event.
     * @param testCase        A {@link TestCase} mock which is referenced in the event.
     * @param executionResult A {@link TimedOutExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithFinishedExecutionResultWithNonZeroExitCode(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "executionResult") final FinishedExecutionResult executionResult) {
        testProcessExecution(
                event,
                solution,
                testCase,
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
     * @param solution        A {@link ExerciseSolution} mock which is referenced in the event.
     * @param testCase        A {@link TestCase} mock which is referenced in the event.
     * @param executionResult A {@link TimedOutExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithFinishedExecutionResultWithZeroExitCodeAndNonEmptyStderr(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "executionResult") final FinishedExecutionResult executionResult) {
        testProcessExecution(
                event,
                solution,
                testCase,
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
     * @param solution        A {@link ExerciseSolution} mock which is referenced in the event.
     * @param testCase        A {@link TestCase} mock which is referenced in the event.
     * @param executionResult A {@link TimedOutExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithFinishedExecutionResultWithZeroExitCodeEmptyStderrAndDifferentOutput(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "executionResult") final FinishedExecutionResult executionResult) {
        final var expectedOutputs = TestHelper.validExerciseSolutionResultList();
        final var anotherOutputs = new LinkedList<>(expectedOutputs);
        Collections.shuffle(anotherOutputs);
        when(testCase.getExpectedOutputs()).thenReturn(expectedOutputs);
        testProcessExecution(
                event,
                solution,
                testCase,
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
     * @param solution        A {@link ExerciseSolution} mock which is referenced in the event.
     * @param testCase        A {@link TestCase} mock which is referenced in the event.
     * @param executionResult A {@link FinishedExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithFinishedExecutionResultWithZeroExitCodeAndEmptyStderrAndExpectedOutput(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "executionResult") final FinishedExecutionResult executionResult) {
        final var expectedOutputs = TestHelper.validExerciseSolutionResultList();
        when(testCase.getExpectedOutputs()).thenReturn(expectedOutputs);
        testProcessExecution(
                event,
                solution,
                testCase,
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
     * @param solution        A {@link ExerciseSolution} mock which is referenced in the event.
     * @param testCase        A {@link TestCase} mock which is referenced in the event.
     * @param executionResult A {@link TimedOutExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithTimedOutExecutionResult(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "executionResult") final TimedOutExecutionResult executionResult) {
        testProcessExecution(
                event,
                solution,
                testCase,
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
     * @param solution        A {@link ExerciseSolution} mock which is referenced in the event.
     * @param testCase        A {@link TestCase} mock which is referenced in the event.
     * @param executionResult A {@link CompileErrorExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithNotCompiledExecutionResult(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "executionResult") final CompileErrorExecutionResult executionResult) {
        testProcessExecution(
                event,
                solution,
                testCase,
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
     * @param solution        An {@link ExerciseSolution} mock which is referenced in the event.
     * @param testCase        A {@link TestCase} mock which is referenced in the event.
     * @param executionResult An {@link InitializationErrorExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithInitializationErrorExecutionResult(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "executionResult") final InitializationErrorExecutionResult executionResult) {
        testProcessExecution(
                event,
                solution,
                testCase,
                executionResult,
                r -> {
                },
                null
        );
    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test,
     * in which the {@link ExecutionResult} is an {@link UnknownErrorExecutionResult}.
     *
     * @param event           A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param solution        An {@link ExerciseSolution} mock which is referenced in the event.
     * @param testCase        A {@link TestCase} mock which is referenced in the event.
     * @param executionResult An {@link UnknownErrorExecutionResult} mock which is returned by the event.
     */
    @Test
    void testProcessExecutionWithUnknownErrorExecutionResult(
            @Mock(name = "event") final ExecutionResultArrivedEvent event,
            @Mock(name = "solution") final ExerciseSolution solution,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "executionResult") final UnknownErrorExecutionResult executionResult) {
        testProcessExecution(
                event,
                solution,
                testCase,
                executionResult,
                r -> {
                },
                null
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
            final ExamSolutionSubmission submission, final SolutionData solutionData1,
            final SolutionData solutionData2) {
        solutionData1.setupSolutionMock();
        solutionData2.setupSolutionMock();
        solutionData1.setupRepositories(testCaseRepository);
        solutionData2.setupRepositories(testCaseRepository);
        when(event.getSubmission()).thenReturn(submission);
        when(exerciseSolutionRepository.getExerciseSolutions(submission))
                .thenReturn(List.of(solutionData1.getSolution(), solutionData2.getSolution()));

        resultsManager.examSolutionSubmitted(event);

        solutionData1.verifyTestCasesAccess(testCaseRepository);
        solutionData2.verifyTestCasesAccess(testCaseRepository);
        verifyNoMoreInteractions(testCaseRepository);
        verify(exerciseSolutionRepository, only()).getExerciseSolutions(submission);
        solutionData1.verifySolutionAndTestCases();
        solutionData2.verifySolutionAndTestCases();
        verifyNoMoreInteractions(publisher);
        verifyNoMoreInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Performs an {@link ExecutionResultArrivedEvent} received test.
     *
     * @param event                     A {@link ExecutionResultArrivedEvent} mock that is received by the manager.
     * @param solution                  An {@link ExerciseSolution} mock which is referenced in the event.
     * @param testCase                  A {@link TestCase} mock which is referenced in the event.
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
            final ExerciseSolution solution,
            final TestCase testCase,
            final R executionResult,
            final Consumer<R> executionResultConfigurer,
            final ExerciseSolutionResult.Result expectedResult) {

        final var testCaseId = TestHelper.validTestCaseId();
        final var solutionId = TestHelper.validExerciseSolutionId();

        // Configure the result mock
        executionResultConfigurer.accept(executionResult);

        // Configure the event
        when(event.getTestCaseId()).thenReturn(testCaseId);
        when(event.getSolutionId()).thenReturn(solutionId);
        when(event.getResult()).thenReturn(executionResult);

        // Setup repositories
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        when(exerciseSolutionResultRepository.save(any(ExerciseSolutionResult.class))).then(i -> i.getArgument(0));

        // Call the method to be tested
        resultsManager.receiveExecutionResult(event);

        // Verifications
        verify(testCaseRepository, only()).findById(testCaseId);
        verify(exerciseSolutionRepository, only()).findById(solutionId);

        Optional.ofNullable(expectedResult)
                .ifPresentOrElse(
                        result -> verify(exerciseSolutionResultRepository, only())
                                .save(
                                        argThat(solutionResult ->
                                                solutionResult.getResult().equals(result)
                                                        && solutionResult.getTestCase().equals(testCase)
                                                        && solutionResult.getSolution().equals(solution)
                                        )
                                ),
                        () -> verifyZeroInteractions(exerciseSolutionResultRepository)
                );
        verifyZeroInteractions(publisher);
    }

    /**
     * Verifies how the {@link ApplicationEventPublisher} is accessed when there are {@link ExerciseSolution}s
     * with answer (i.e {@link ExecutionRequestedEvent}s must be published in the publisher).
     *
     * @param solutionWithAnswer The {@link ExerciseSolution} with answer
     *                           (which is included in the {@link ExecutionRequestedEvent}).
     * @param testCases          The {@link TestCase} included in the {@link ExecutionRequestedEvent}.
     */
    private void verifyExecutionRequestEventsArePublishedForSolutionsWithAnswer(
            final ExerciseSolution solutionWithAnswer,
            final List<TestCase> testCases) {
        testCases.forEach(testCase ->
                verify(publisher, times(1))
                        .publishEvent(argThat(executionRequestedEventIsWellFormed(solutionWithAnswer, testCase)))
        );
    }

    /**
     * Verifies how the {@link ExerciseSolutionResultRepository} is accessed when there are {@link ExerciseSolution}s
     * without answer (i.e {@link ExerciseSolutionResult}s must be stored).
     *
     * @param solutionWithoutAnswer The {@link ExerciseSolution} without answer
     *                              (to which the {@link ExerciseSolutionResult} created belongs to).
     * @param testCases             The {@link TestCase} to which the {@link ExerciseSolutionResult} created belongs to.
     */
    private void verifyFailedResultIsSavedForSolutionsWithoutAnswer(
            final ExerciseSolution solutionWithoutAnswer,
            final List<TestCase> testCases) {
        testCases.forEach(testCase ->
                verify(exerciseSolutionResultRepository, times(1))
                        .save(argThat(solutionWithNoAnswerResult(solutionWithoutAnswer, testCase)))
        );
    }

    /**
     * An {@link ArgumentMatcher} for {@link ExecutionRequestedEvent} to check whether the said event contains
     * the given {@code solution} and {@code testCase}.
     *
     * @param solution The {@link ExerciseSolution} to be checked.
     * @param testCase The {@link TestCase} to be checked.
     * @return The {@link ArgumentMatcher}.
     */
    private static ArgumentMatcher<ExecutionRequestedEvent> executionRequestedEventIsWellFormed(
            final ExerciseSolution solution,
            final TestCase testCase) {
        return event -> event.getSolution().equals(solution) && event.getTestCase().equals(testCase);
    }

    /**
     * An {@link ArgumentMatcher} for {@link ExerciseSolutionResult} to check whether the said event contains
     * the given {@code solution} and {@code testCase}.
     *
     * @param solution The {@link ExerciseSolution} to be checked.
     * @param testCase The {@link TestCase} to be checked.
     * @return The {@link ArgumentMatcher}.
     */
    private static ArgumentMatcher<ExerciseSolutionResult> solutionWithNoAnswerResult(
            final ExerciseSolution solution,
            final TestCase testCase) {
        return result -> result.getSolution().equals(solution)
                && result.getTestCase().equals(testCase)
                && result.getResult().equals(ExerciseSolutionResult.Result.FAILED);
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
         * A {@link BiConsumer} of {@link ExerciseSolution} and {@link List} of {@link TestCase}
         * used to verify how those mocks are used.
         */
        private final BiConsumer<ExerciseSolution, List<TestCase>> solutionAndTestCasesVerifier;

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
            when(testCaseRepository.getAllTestCases(exercise)).thenReturn(testCases());
        }

        /**
         * Convenient method that returns the {@link TestCase}s as a {@link List}.
         *
         * @return The {@link TestCase}s.
         */
        private List<TestCase> testCases() {
            return List.of(testCase1, testCase2);
        }

        /**
         * Performs verification over the given {@code testCaseRepository}
         * (i.e checks that the {@link TestCaseRepository#getAllTestCases(Exercise)} method is called using the
         * {@link Exercise} in this container class).
         *
         * @param testCaseRepository The {@link TestCaseRepository} to be verified.
         */
        private void verifyTestCasesAccess(final TestCaseRepository testCaseRepository) {
            verify(testCaseRepository, times(1)).getAllTestCases(exercise);
        }

        /**
         * Calls the {@link #solutionAndTestCasesVerifier}, with the {@link ExerciseSolution} and the {@link TestCase}s.
         * This method is intended to be used to verify actions with those mocks.
         */
        private void verifySolutionAndTestCases() {
            solutionAndTestCasesVerifier.accept(solution, testCases());
        }
    }
}
