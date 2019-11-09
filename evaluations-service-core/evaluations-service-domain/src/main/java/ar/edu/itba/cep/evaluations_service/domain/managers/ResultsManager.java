package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.events.ExamSolutionSubmittedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionRequestedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExecutionResponseArrivedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.DataLoadingHelper;
import ar.edu.itba.cep.evaluations_service.domain.helpers.StateVerificationHelper;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import ar.edu.itba.cep.evaluations_service.services.ResultsService;
import ar.edu.itba.cep.executor.models.ExecutionResponse;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult.Result.*;


/**
 * A component in charge of managing {@link ExerciseSolutionResult}s,
 * sending to run {@link ExerciseSolution}s and setting a result based on an execution results.
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ResultsManager implements ResultsService {

    private final ExerciseSolutionRepository exerciseSolutionRepository;
    private final TestCaseRepository testCaseRepository;
    private final ExerciseSolutionResultRepository exerciseSolutionResultRepository;
    private final ApplicationEventPublisher publisher;


    // ================================================================================================================
    // ResultsService
    // ================================================================================================================

    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (" +
                    "   hasAuthority('TEACHER')" +
                    "       and @exerciseSolutionAuthorizationProvider.isExamOwner(#solutionId, principal)" +
                    ")" +
                    " or (" +
                    "   hasAuthority('STUDENT')" +
                    "       and @exerciseSolutionAuthorizationProvider.isOwner(#solutionId, principal)" +
                    ")"
    )
    @Override
    public List<ExerciseSolutionResult> getResultsForSolution(final long solutionId)
            throws NoSuchEntityException, IllegalEntityStateException {
        final var solution = DataLoadingHelper.loadSolution(exerciseSolutionRepository, solutionId);
        StateVerificationHelper.checkSubmitted(solution.getSubmission());
        return exerciseSolutionResultRepository.find(solution);
    }

    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (" +
                    "   hasAuthority('TEACHER')" +
                    "       and @exerciseSolutionAuthorizationProvider.isExamOwner(#solutionId, principal)" +
                    ")" +
                    " or (" +
                    "   hasAuthority('STUDENT')" +
                    "       and @exerciseSolutionAuthorizationProvider.isOwner(#solutionId, principal)" +
                    ")"
    )
    @Override
    public ExerciseSolutionResult getResultFor(final long solutionId, final long testCaseId)
            throws NoSuchEntityException, IllegalEntityStateException {
        return loadResultFor(solutionId, testCaseId);
    }


    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (" +
                    "   hasAuthority('TEACHER')" +
                    "       and @exerciseSolutionAuthorizationProvider.isExamOwner(#solutionId, principal)" +
                    ")"
    )
    @Override
    @Transactional
    public void retryForSolution(final long solutionId) throws NoSuchEntityException, IllegalEntityStateException {
        final var solution = DataLoadingHelper.loadSolution(exerciseSolutionRepository, solutionId);
        StateVerificationHelper.checkSubmitted(solution.getSubmission());
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

    @PreAuthorize(
            "hasAuthority('ADMIN')" +
                    " or (" +
                    "   hasAuthority('TEACHER')" +
                    "       and @exerciseSolutionAuthorizationProvider.isExamOwner(#solutionId, principal)" +
                    ")"
    )
    @Override
    @Transactional
    public void retryForSolutionAndTestCase(final long solutionId, final long testCaseId)
            throws NoSuchEntityException, IllegalEntityStateException {
        final var result = loadResultFor(solutionId, testCaseId);

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
     * @param event The {@link ExecutionResponseArrivedEvent} to be handled.
     * @throws NoSuchEntityException    If the event contains an {@link ExerciseSolution} id or a {@link TestCase} id
     *                                  of a non existence entity.
     * @throws IllegalArgumentException If the {@code event} is {@code null},
     *                                  or if it contains a {@code null} {@link ExerciseSolution}
     */
    @Transactional
    @EventListener(ExecutionResponseArrivedEvent.class)
    public void receiveExecutionResponse(final ExecutionResponseArrivedEvent event)
            throws NoSuchEntityException, IllegalArgumentException {
        Assert.notNull(event, "The event must not be null");
        processResult(event.getSolutionId(), event.getTestCaseId(), event.getResponse());
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Loads the {@link ExerciseSolutionResult}
     * for the {@link ExerciseSolution} and {@link TestCase} with the given ids.
     *
     * @param solutionId The {@link ExerciseSolution} id.
     * @param testCaseId The {@link TestCase} id.
     * @return The corresponding {@link ExerciseSolutionResult}.
     * @throws NoSuchEntityException       If there is no {@link ExerciseSolution} or {@link TestCase}
     *                                     with the given ids, or if they do not belong to the same
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.Exercise}.
     * @throws IllegalEntityStateException If the {@link ExamSolutionSubmission}
     *                                     belonging to the {@link ExerciseSolution} with the given {@code solutionId}
     *                                     is not submitted.
     */
    private ExerciseSolutionResult loadResultFor(final long solutionId, final long testCaseId)
            throws NoSuchEntityException, IllegalEntityStateException {
        final var solution = DataLoadingHelper.loadSolution(exerciseSolutionRepository, solutionId);
        final var testCase = DataLoadingHelper.loadTestCase(testCaseRepository, testCaseId);
        if (!Objects.equals(solution.getExercise(), testCase.getExercise())) {
            throw new NoSuchEntityException();
        }
        StateVerificationHelper.checkSubmitted(solution.getSubmission());
        // If the result does not exist, it means that something unexpected happened.
        return exerciseSolutionResultRepository.find(solution, testCase)
                .orElseThrow(() -> new IllegalStateException("This should not happen"));
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
     * Processes is performed by checking the encapsulated data in the given {@code executionResponse}.
     *
     * @param solutionId        The id of the referenced {@link ExerciseSolution}.
     * @param testCaseId        The id of the referenced {@link TestCase}.
     * @param executionResponse An {@link ExecutionResponse} with data to be processed.
     * @throws NoSuchEntityException    If there is no {@link ExerciseSolution} with the given {@code solutionId},
     *                                  or if there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalArgumentException If the given {@code executionResponse} is {@code null}.
     */
    private void processResult(final long solutionId, final long testCaseId, final ExecutionResponse executionResponse)
            throws NoSuchEntityException, IllegalArgumentException {
        Assert.notNull(executionResponse, "Event without execution response");
        exerciseSolutionResultRepository.find(solutionId, testCaseId)
                .ifPresentOrElse(
                        solutionResult -> {
                            final var result = getResultFor(
                                    executionResponse,
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
     * Gets the {@link ExerciseSolutionResult.Result} according to the given {@code executionResponse},
     * using the given {@code expectedOutputsSupplier} to retrieve the expected results
     * if the given {@index executionResponse}'s result is {@link ExecutionResponse.ExecutionResult#COMPLETED}.
     *
     * @param executionResponse       The {@link ExecutionResponse} to be analyzed.
     * @param expectedOutputsSupplier The expected outputs to check if the execution is approved or failed
     *                                in case the {@code response} is
     *                                {@link ExecutionResponse.ExecutionResult#COMPLETED}.
     * @return The corresponding {@link ExerciseSolutionResult.Result}.
     * @throws IllegalArgumentException If the given {@link ExecutionResponse}'s result is not known.
     *                                  This can happen if a new value is added and it is not handled here.
     */
    private ExerciseSolutionResult.Result getResultFor(
            final ExecutionResponse executionResponse,
            final Supplier<List<String>> expectedOutputsSupplier) throws IllegalArgumentException {
        switch (executionResponse.getResult()) {
            case COMPLETED:
                return isApproved(executionResponse, expectedOutputsSupplier) ? APPROVED : FAILED;
            case TIMEOUT:
                return TIMED_OUT;
            case COMPILE_ERROR:
                return NOT_COMPILED;
            case INITIALIZATION_ERROR:
                return INITIALIZATION_ERROR;
            case UNKNOWN_ERROR:
                return UNKNOWN_ERROR;
        }
        throw new IllegalArgumentException("Unknown subtype. Have you added a new value to the ExecutionResult enum?");
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
     * Checks whether the given {@code response} is approved.
     *
     * @param response                The {@link ExecutionResponse} from where execution stuff is taken.
     * @param expectedOutputsSupplier A {@link Supplier} of the expected outputs for the execution
     *                                in order to be considered approved.
     * @return {@code true} if the execution is approved, or {@code false} otherwise.
     * @implNote The method checks whether the exit code of the {@link ExecutionResponse} is 0, if there is
     * no data in the standard error output, and if the outputs match the given {@code expectedOutputs}.
     */
    private static boolean isApproved(
            final ExecutionResponse response,
            final Supplier<List<String>> expectedOutputsSupplier) {
        return response.getExitCode() == 0 && expectedOutputsSupplier.get().equals(response.getStdout());
    }
}
