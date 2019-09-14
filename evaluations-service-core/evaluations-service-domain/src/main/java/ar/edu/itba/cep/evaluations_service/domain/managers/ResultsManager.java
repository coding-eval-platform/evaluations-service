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
import ar.edu.itba.cep.evaluations_service.services.ResultsService;
import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult.Result.*;


/**
 * A component in charge of managing {@link ExerciseSolutionResult}s,
 * sending to run {@link ExerciseSolution}s and setting a result based on an execution result.
 */
@Component
@AllArgsConstructor
@Transactional(readOnly = true)
public class ResultsManager implements ResultsService {

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


    // ================================================================================================================
    // ResultsService
    // ================================================================================================================

    @Override
    public List<ExerciseSolutionResult> getResultsForSolution(final long solutionId)
            throws NoSuchEntityException, IllegalEntityStateException {
        final var solution = DataLoadingHelper.loadSolution(exerciseSolutionRepository, solutionId);
        checkSubmitted(solution);
        return exerciseSolutionResultRepository.find(solution);
    }

    @Override
    public ExerciseSolutionResult getResultFor(final long solutionId, final long testCaseId)
            throws NoSuchEntityException, IllegalEntityStateException {
        final var solution = DataLoadingHelper.loadSolution(exerciseSolutionRepository, solutionId);
        final var testCase = DataLoadingHelper.loadTestCase(testCaseRepository, testCaseId);
        checkSubmitted(solution);
        // If the Optional is empty, something unexpected has happened (must exist if solution is submitted).
        // Throw an IllegalStateException which is not part of the API, in order to be reported.
        return exerciseSolutionResultRepository.find(solution, testCase).orElseThrow(IllegalStateException::new);
    }


    @Override
    public void retryForSolution(final long solutionId) throws NoSuchEntityException, IllegalEntityStateException {
        final var solution = DataLoadingHelper.loadSolution(exerciseSolutionRepository, solutionId);
        checkSubmitted(solution);
        // Check if the solution is answered
        if (!isAnswered(solution)) {
            return; // Do nothing if not answered.
        }
        // Get the results and stay only with those that are marked (no execution is taking place)
        // For those results, unmark them and store the new state in the repository
        // Finally, send to run by publishing the event.
        exerciseSolutionResultRepository.find(solution)
                .stream()
                .filter(ExerciseSolutionResult::isMarked)
                .peek(ExerciseSolutionResult::unmark)
                .peek(exerciseSolutionResultRepository::save)
                .map(ExecutionRequestedEvent::fromResult)
                .forEach(publisher::publishEvent)
        ;

    }

    @Override
    @Transactional
    public void retryForSolutionAndTestCase(final long solutionId, final long testCaseId)
            throws NoSuchEntityException, IllegalEntityStateException {
        if (!exerciseSolutionRepository.existsById(solutionId) || !testCaseRepository.existsById(testCaseId)) {
            throw new NoSuchEntityException();
        }
        // If the result does not exist, it means that the solution has not been submitted yet.
        final var result = exerciseSolutionResultRepository.find(solutionId, testCaseId)
                .orElseThrow(() -> new IllegalEntityStateException(SOLUTION_NOT_SUBMITTED));

        // If the result is marked, then it means that it is not being executed right now.
        if (!result.isMarked()) {
            return; // Skip if execution is taking place right now.
        }

        // Then check if it is answered.
        if (!isAnswered(result.getSolution())) {
            return; // Skip if not answered.
        }

        // Remove mark (this indicates that the solution is being sent to run again).
        result.unmark();
        exerciseSolutionResultRepository.save(result);
        publisher.publishEvent(ExecutionRequestedEvent.fromResult(result));
    }


    // ================================================================================================================
    // Event Listeners
    // ================================================================================================================

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
     * Checks that the {@link ExamSolutionSubmission} to which the given {@code solution} belongs to
     * is already submitted.
     *
     * @param solution The {@link ExerciseSolution} to be checked.
     * @throws IllegalEntityStateException If the {@link ExamSolutionSubmission} is not submitted.
     */
    private static void checkSubmitted(final ExerciseSolution solution) throws IllegalEntityStateException {
        // Then check the submission's state
        if (solution.getSubmission().getState() != ExamSolutionSubmission.State.SUBMITTED) {
            throw new IllegalEntityStateException(SOLUTION_NOT_SUBMITTED);
        }
    }

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
        exerciseSolutionRepository.getExerciseSolutions(submission)
                .stream()
                .map(this::createResultsFor) // This will set the "not answered" mark accordingly
                .flatMap(Collection::stream)
                .peek(exerciseSolutionResultRepository::save)
                .filter(result -> !result.isMarked())
                .map(ExecutionRequestedEvent::fromResult)
                .forEach(publisher::publishEvent)
        ;
    }

    /**
     * Builds a {@link List} of {@link ExerciseSolutionResult} corresponding to the given {@code solution}.
     * {@link TestCase}s are retrieved from the {@link TestCaseRepository} using the given {@code solution}'s owner
     * {@link ar.edu.itba.cep.evaluations_service.models.Exercise}.
     * If the {@code solution} is not answered, the returned {@link ExerciseSolutionResult}s will be marked as
     * {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult.Result#NOT_ANSWERED}.
     *
     * @param solution The {@link ExerciseSolution}.
     * @return The created {@link ExerciseSolutionResult}s {@link List}.
     */
    private List<ExerciseSolutionResult> createResultsFor(final ExerciseSolution solution) {
        final var answered = isAnswered(solution);
        return testCaseRepository.getAllTestCases(solution.getExercise())
                .stream()
                .map(testCase -> new ExerciseSolutionResult(solution, testCase))
                .peek(solutionResult -> {
                    if (!answered) {
                        solutionResult.mark(NOT_ANSWERED);
                    }
                })
                .collect(Collectors.toList());
    }


    /**
     * Processes the execution of the {@link ExerciseSolution} with the given {@code solutionId}
     * when being evaluated with the {@link TestCase} with the given {@code testCaseId}.
     * Processes is performed by checking the encapsulated data in the given {@code executionResult}.
     *
     * @param solutionId      The id of the referenced {@link ExerciseSolution}.
     * @param testCaseId      The id of the referenced {@link TestCase}.
     * @param executionResult An {@link ExecutionResult} with data to be processed.
     * @throws NoSuchEntityException    If there is no {@link ExerciseSolution} with the given {@code solutionId},
     *                                  or if there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalArgumentException If the given {@code executionResult} is {@code null}.
     */
    private void processResult(final long solutionId, final long testCaseId, final ExecutionResult executionResult)
            throws NoSuchEntityException, IllegalArgumentException {
        Assert.notNull(executionResult, "Event without executionResult");
        exerciseSolutionResultRepository.find(solutionId, testCaseId)
                .ifPresentOrElse(
                        solutionResult -> {
                            final var result = getResultFor(
                                    executionResult,
                                    () -> solutionResult.getTestCase().getExpectedOutputs()
                            );
                            solutionResult.mark(result);
                            exerciseSolutionResultRepository.save(solutionResult);
                        },
                        () -> {
                            // TODO: This should not happen as the ExerciseSolutionResult
                            //  is created when the exam is submitted,
                            //  which also means that both the test case and solution exist.
                            //  If this case is given, it can be that by an unknown reason
                            //  the test case, the solution, or the result have been deleted.
                            //  This should be reported accordingly.
                            throw new NoSuchEntityException(
                                    "A TestCase an ExerciseSolution or an ExerciseSolutionResult is missing"
                            );
                        }
                );
    }

    /**
     * Gets the {@link ExerciseSolutionResult.Result} according to the given {@code executionResult},
     * using the given {@code expectedOutputsSupplier} to retrieve the expected results
     * if the given {@index executionResult} is a {@link FinishedExecutionResult}.
     *
     * @param executionResult         The {@link ExecutionResult} to be analyzed.
     * @param expectedOutputsSupplier The expected outputs to check if the execution is approved or failed
     *                                in case it is a {@link FinishedExecutionResult}.
     * @return The corresponding {@link ExerciseSolutionResult.Result}.
     * @throws IllegalArgumentException If the given {@link ExecutionResult} is not a known subtype.
     *                                  This can happen if a new subtype is added and it is not handled here.
     */
    private ExerciseSolutionResult.Result getResultFor(
            final ExecutionResult executionResult,
            final Supplier<List<String>> expectedOutputsSupplier) throws IllegalArgumentException {
        if (executionResult instanceof FinishedExecutionResult) {
            return isApproved((FinishedExecutionResult) executionResult, expectedOutputsSupplier) ? APPROVED : FAILED;
        }
        if (executionResult instanceof TimedOutExecutionResult) {
            return TIMED_OUT;
        }
        if (executionResult instanceof CompileErrorExecutionResult) {
            return NOT_COMPILED;
        }
        if (executionResult instanceof InitializationErrorExecutionResult) {
            return INITIALIZATION_ERROR;
        }
        if (executionResult instanceof UnknownErrorExecutionResult) {
            return UNKNOWN_ERROR;
        }
        throw new IllegalArgumentException("Unknown subtype. Have you added a new subtype of ExecutionResult?");
    }

    /**
     * Indicates whether the given {@code solution} is answered.
     *
     * @param solution The {@link ExerciseSolution} to be checked.
     * @return {@code true} if it is answered, or {@code false} otherwise.
     */
    private static boolean isAnswered(final ExerciseSolution solution) {
        return StringUtils.hasText(solution.getAnswer());
    }

    /**
     * Checks whether the given {@code result} is approved.
     *
     * @param result                  The {@link FinishedExecutionResult} from where execution stuff is taken.
     * @param expectedOutputsSupplier A {@link Supplier} of the expected outputs for the execution
     *                                in order to be considered approved.
     * @return {@code true} if the execution is approved, or {@code false} otherwise.
     * @implNote The method checks whether the exit code of the {@link FinishedExecutionResult} is 0, if there is
     * no data in the standard error output, and if the outputs match the given {@code expectedOutputs}.
     */
    private static boolean isApproved(
            final FinishedExecutionResult result,
            final Supplier<List<String>> expectedOutputsSupplier) {
        return result.getExitCode() == 0
                && result.getStderr().isEmpty()
                && expectedOutputsSupplier.get().equals(result.getStdout());
    }

    /**
     * An {@link IllegalStateException} that indicates that an {@link ExamSolutionSubmission} is not submitted yet.
     */
    private final static IllegalEntityStateError SOLUTION_NOT_SUBMITTED =
            new IllegalEntityStateError("Solutions not submitted yet", "state");

    /**
     * An {@link IllegalStateException} that indicates that an {@link ExamSolutionSubmission} is not submitted yet.
     */
    private final static IllegalEntityStateError NOT_MARKED =
            new IllegalEntityStateError("Exercise Solution Result is not marked yet", "result");
}
