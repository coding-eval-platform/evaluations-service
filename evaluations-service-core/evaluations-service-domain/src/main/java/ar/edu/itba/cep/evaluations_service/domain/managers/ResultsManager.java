package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.*;
import ar.edu.itba.cep.evaluations_service.domain.events.ExamSolutionSubmittedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionRequestedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResultArrivedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.DataLoadingHelper;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


/**
 * A component in charge of managing {@link ExerciseSolutionResult}s,
 * sending to run {@link ExerciseSolution}s and setting a result based on an execution result.
 */
@Component
@AllArgsConstructor
@Transactional(readOnly = true)
public class ResultsManager {

    /**
     * The {@link ExerciseSolutionRepository}.
     */
    private final ExerciseSolutionRepository exerciseSolutionRepository;
    /**
     * The {@link TestCaseRepository}.
     */
    private final TestCaseRepository testCaseRepository;
    /**
     * The {@link ExerciseSolutionResultRepository}.
     */
    private final ExerciseSolutionResultRepository exerciseSolutionResultRepository;

    /**
     * An {@link ApplicationEventPublisher} to publish relevant events to the rest of the application's components.
     */
    private final ApplicationEventPublisher publisher;


    /**
     * Handles the given {@code event}.
     *
     * @param event The {@link ExamSolutionSubmittedEvent} to be handled.
     * @throws IllegalArgumentException If the {@code event} is {@code null},
     *                                  or if it contains a {@code null} {@link ExamSolutionSubmission}
     */
    @Transactional
    @EventListener(ExamSolutionSubmittedEvent.class)
    public void examSolutionSubmitted(final ExamSolutionSubmittedEvent event) throws IllegalArgumentException {
        Assert.notNull(event, "The event must not be null");
        processExamSolutionSubmission(event.getSubmission());
    }

    /**
     * Handles the given {@code event}.
     *
     * @param event The {@link ExecutionResultArrivedEvent} to be handled.
     * @throws NoSuchEntityException    If the event contains an {@link ExerciseSolution} id or a {@link TestCase} id
     *                                  of a non existence entity.
     * @throws IllegalArgumentException If the {@code event} is {@code null},
     *                                  or if it contains a {@code null} {@link ExerciseSolution}
     */
    @Transactional
    @EventListener(ExecutionResultArrivedEvent.class)
    public void receiveExecutionResult(final ExecutionResultArrivedEvent event)
            throws NoSuchEntityException, IllegalArgumentException {
        Assert.notNull(event, "The event must not be null");
        processResult(event.getSolutionId(), event.getTestCaseId(), event.getResult());
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Sends to run the given {@code submission}
     * (i.e all {@link ExerciseSolution}s with answer
     *
     * @param submission The {@link ExamSolutionSubmission} to be sent to run.
     * @throws IllegalArgumentException If The given {@code submission} is {@code null}.
     */
    private void processExamSolutionSubmission(final ExamSolutionSubmission submission)
            throws IllegalArgumentException {
        Assert.notNull(submission, "The submission must not be null");

        final var solutions = exerciseSolutionRepository.getExerciseSolutions(submission)
                .stream()
                .map(this::createContainerForSolution)
                .collect(Collectors.groupingBy(AnswerCondition::fromContainer));

        Optional.ofNullable(solutions.get(AnswerCondition.ANSWERED))
                .stream()
                .flatMap(Collection::stream)
                .map(ResultsManager::createExecutionRequestedEvents)
                .flatMap(Collection::stream)
                .forEach(publisher::publishEvent);
        Optional.ofNullable(solutions.get(AnswerCondition.NOT_ANSWERED))
                .stream()
                .flatMap(Collection::stream)
                .map(ResultsManager::createFailed)
                .flatMap(Collection::stream)
                .forEach(exerciseSolutionResultRepository::save);
    }

    /**
     * Processes the execution of the {@link ExerciseSolution} with the given {@code solutionId}
     * when being evaluated with the {@link TestCase} with the given {@code testCaseId}.
     * Processes is performed by checking the encapsulated data in the given {@code executionResult}.
     *
     * @param solutionId The id of the referenced {@link ExerciseSolution}.
     * @param testCaseId The id of the referenced {@link TestCase}.
     * @param result     An {@link ExecutionResult} with data to be processed.
     * @throws NoSuchEntityException    If there is no {@link ExerciseSolution} with the given {@code solutionId},
     *                                  or if there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalArgumentException If the given {@code result} is {@code null}.
     */
    private void processResult(final long solutionId, final long testCaseId, final ExecutionResult result)
            throws NoSuchEntityException, IllegalArgumentException {
        Assert.notNull(result, "Event without result");

        final var solution = DataLoadingHelper.loadSolution(exerciseSolutionRepository, solutionId);
        final var testCase = DataLoadingHelper.loadTestCase(testCaseRepository, testCaseId);

        // State validation is not needed because the existence of a solution proves state validity

        // Get the ExerciseSolutionResultCreator corresponding to the given executionResult,
        // and then create the ExerciseSolutionResult to be saved.
        // If the ExecutionResult is an InitializationErrorExecutionResult or an UnknownErrorExecutionResult,
        // the returned Optional will be empty and nothing will happen.
        getResultCreator(result)
                .map(creator -> creator.apply(solution, testCase))
                .ifPresent(exerciseSolutionResultRepository::save)
        ;
    }

    /**
     * Creates a {@link SolutionAndTestCases} instance from the given {@code solution}.
     *
     * @param solution The {@link ExerciseSolution}.
     * @return The created {@link SolutionAndTestCases} container instance.
     */
    private SolutionAndTestCases createContainerForSolution(final ExerciseSolution solution) {
        return SolutionAndTestCases.create(solution, testCaseRepository.getAllTestCases(solution.getExercise()));
    }

    /**
     * Converts the given {@link SolutionAndTestCases} {@code container}
     * into a {@link List} of {@link ExecutionRequestedEvent}s
     *
     * @param container The {@link SolutionAndTestCases} to be converted.
     * @return A {@link List} with an {@link ExecutionRequestedEvent}
     * for each {@link TestCase} in the {@code container}.
     */
    private static List<ExecutionRequestedEvent> createExecutionRequestedEvents(final SolutionAndTestCases container) {
        return container.getTestCases()
                .stream()
                .map(testCase -> ExecutionRequestedEvent.create(container.getSolution(), testCase))
                .collect(Collectors.toList())
                ;
    }

    /**
     * Convenient method that maps the given {@link SolutionAndTestCases} container to a {@link List} of failed
     * {@link ExerciseSolutionResult}s.
     *
     * @param container The {@link SolutionAndTestCases} to be converted.
     * @return A {@link List} with failed {@link ExerciseSolutionResult}s corresponding to the given {@code container}.
     */
    private static List<ExerciseSolutionResult> createFailed(final SolutionAndTestCases container) {
        return container.getTestCases()
                .stream()
                .map(testCase -> failed(container.getSolution(), testCase))
                .collect(Collectors.toList())
                ;
    }

    /**
     * Gets the {@link ExerciseSolutionResultCreator} corresponding to the given {@link ExecutionResult}.
     *
     * @param result The {@link ExecutionResult} to be analyzed in order to know which
     *               {@link ExerciseSolutionResultCreator} must be returned.
     * @return The {@link ExerciseSolutionResultCreator} corresponding to the given {@link ExecutionResult}.
     */
    private static Optional<ExerciseSolutionResultCreator> getResultCreator(final ExecutionResult result) {
        if (result instanceof FinishedExecutionResult) {
            return Optional.of((s, t) -> createForFinished(s, t, (FinishedExecutionResult) result));
        }
        if (result instanceof TimedOutExecutionResult) {
            return Optional.of((s, t) -> new ExerciseSolutionResult(s, t, ExerciseSolutionResult.Result.TIMED_OUT));
        }
        if (result instanceof CompileErrorExecutionResult) {
            return Optional.of((s, t) -> new ExerciseSolutionResult(s, t, ExerciseSolutionResult.Result.NOT_COMPILED));
        }
        if (result instanceof InitializationErrorExecutionResult || result instanceof UnknownErrorExecutionResult) {
            return Optional.empty(); // TODO: maybe make a solution result for this special cases? retry?
        }
        throw new IllegalArgumentException("Unknown subtype");
    }

    /**
     * Creates an {@link ExerciseSolutionResult} for a {@link FinishedExecutionResult}.
     *
     * @param solution        The {@link ExerciseSolution} corresponding to the created {@link ExerciseSolutionResult}.
     * @param testCase        The {@link TestCase} corresponding to the created {@link ExerciseSolutionResult}.
     * @param executionResult The {@link FinishedExecutionResult} from where execution stuff is taken.
     * @return The created {@link ExerciseSolutionResult} for a {@link FinishedExecutionResult}.
     */
    private static ExerciseSolutionResult createForFinished(
            final ExerciseSolution solution,
            final TestCase testCase,
            final FinishedExecutionResult executionResult) {

        if (executionResult.getExitCode() == 0
                && executionResult.getStderr().isEmpty()
                && testCase.getExpectedOutputs().equals(executionResult.getStdout())) {
            return approved(solution, testCase);
        }
        return failed(solution, testCase);
    }

    /**
     * Creates an approved {@link ExerciseSolutionResult} with the given {@code solution} and {@code testCase}.
     *
     * @param solution The {@link ExerciseSolution}.
     * @param testCase The {@link TestCase}.
     * @return The created approved {@link ExerciseSolutionResult}.
     */
    private static ExerciseSolutionResult approved(final ExerciseSolution solution, final TestCase testCase) {
        return new ExerciseSolutionResult(solution, testCase, ExerciseSolutionResult.Result.APPROVED);
    }

    /**
     * Creates a failed {@link ExerciseSolutionResult} with the given {@code solution} and {@code testCase}.
     *
     * @param solution The {@link ExerciseSolution}.
     * @param testCase The {@link TestCase}.
     * @return The created failed {@link ExerciseSolutionResult}.
     */
    private static ExerciseSolutionResult failed(final ExerciseSolution solution, final TestCase testCase) {
        return new ExerciseSolutionResult(solution, testCase, ExerciseSolutionResult.Result.FAILED);
    }


    /**
     * A container class that holds an {@link ExerciseSolution} together with the {@link TestCase}s belonging to
     * the {@link ar.edu.itba.cep.evaluations_service.models.Exercise} to which the said {@link ExerciseSolution}
     * belongs to.
     */
    @Getter
    @ToString(doNotUseGetters = true)
    @EqualsAndHashCode(doNotUseGetters = true)
    @AllArgsConstructor(staticName = "create")
    private static final class SolutionAndTestCases {
        /**
         * The wrapped {@link ExerciseSolution}.
         */
        private final ExerciseSolution solution;
        /**
         * The wrapped {@link TestCase}s.
         */
        private final List<TestCase> testCases;
    }

    /**
     * Enum holding the possible conditions regarding the answer state of an {@link ExerciseSolution}.
     */
    private enum AnswerCondition {
        /**
         * Indicates that an {@link ExerciseSolution} has answer.
         */
        ANSWERED,
        /**
         * Indicates that an {@link ExerciseSolution} has no answer.
         */
        NOT_ANSWERED,
        ;

        /**
         * Retrieves the {@link AnswerCondition} corresponding for the given {@code container}.
         *
         * @param container The {@link SolutionAndTestCases} to be analyzed.
         * @return {@link #ANSWERED} if the {@link ExerciseSolution} in the container has an answer,
         * or {@link #NOT_ANSWERED} otherwise.
         */
        public static AnswerCondition fromContainer(final SolutionAndTestCases container) {
            return StringUtils.hasText(container.getSolution().getAnswer()) ? ANSWERED : NOT_ANSWERED;
        }
    }

    /**
     * Defines behaviour for an object that can create an {@link ExerciseSolutionResult}
     * from an {@link ExerciseSolution} and a {@link TestCase}.
     */
    private interface ExerciseSolutionResultCreator
            extends BiFunction<ExerciseSolution, TestCase, ExerciseSolutionResult> {
    }
}
