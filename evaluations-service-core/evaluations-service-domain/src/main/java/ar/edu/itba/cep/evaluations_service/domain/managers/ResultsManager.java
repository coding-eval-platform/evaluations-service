package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.*;
import ar.edu.itba.cep.evaluations_service.domain.events.ExamSolutionSubmittedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionRequestedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResultArrivedEvent;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
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
        exerciseSolutionRepository.getExerciseSolutions(submission)
                .stream()
                .map(this::createResultsFor) // This will set the "not answered" mark accordingly
                .flatMap(Collection::stream)
                .peek(exerciseSolutionResultRepository::save)
                .filter(result -> !result.isMarked())
                .map(result -> ExecutionRequestedEvent.create(result.getSolution(), result.getTestCase()))
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
        final var answered = StringUtils.hasText(solution.getAnswer());
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
}
