package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.executor.models.ExecutionRequest;
import ar.edu.itba.cep.executor.models.ExecutionResponse;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;

/**
 * A port into the application that allows processing {@link ExecutionResponse}s, in order to set a solution result.
 * This will act as a callback when receiving the results of an execution that was requested
 * by the {@link ExecutorServiceCommandMessageProxy#requestExecution(ExecutionRequest, ExecutionResponseReplyData)}
 * method.
 */
public interface ExecutionResponseProcessor {

    /**
     * Process the given {@code response}, with the given {@code solutionId}
     * when being evaluated with the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param solutionId The id of the referenced {@link ExerciseSolution}.
     * @param testCaseId The id of the referenced {@link TestCase}.
     * @param response   The {@link ExecutionResponse} to be processed.
     * @throws NoSuchEntityException    If there is no {@link ExerciseSolution} with the given {@code solutionId},
     *                                  or if there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    void processResponse(final long solutionId, final long testCaseId, final ExecutionResponse response)
            throws IllegalArgumentException;
}
