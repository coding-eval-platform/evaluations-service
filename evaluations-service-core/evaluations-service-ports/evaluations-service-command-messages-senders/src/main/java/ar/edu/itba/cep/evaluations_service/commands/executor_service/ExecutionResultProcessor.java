package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;

/**
 * A port into the application that allows processing execution results, in order to set a solution result.
 * This will act as a callback when receiving the results of an execution that was requested
 * by the {@link ExecutorServiceCommandMessageProxy#requestExecution(ExecutionRequest, ExecutionResultReplyData)}
 * method.
 */
public interface ExecutionResultProcessor {

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
     * @throws IllegalArgumentException If any argument is not valid.
     */
    void processExecution(final long solutionId, final long testCaseId, final ExecutionResult executionResult)
            throws NoSuchEntityException, IllegalArgumentException;
}
