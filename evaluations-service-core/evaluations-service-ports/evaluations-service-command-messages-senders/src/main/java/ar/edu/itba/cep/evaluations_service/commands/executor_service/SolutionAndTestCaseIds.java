package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.executor.api.ExecutionResponseIdData;
import lombok.Data;

/**
 * Class containing data to handle an {@link ar.edu.itba.cep.executor.models.ExecutionResponse}.
 */
@Data(staticConstructor = "create")
public class SolutionAndTestCaseIds implements ExecutionResponseIdData {

    /**
     * The id of the solution to be processed.
     */
    private final long solutionId;
    /**
     * The id of the test case to be processed.
     */
    private final long testCaseId;
}
