package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class containing data to handle an execution result.
 */
@Getter
@EqualsAndHashCode
@ToString(doNotUseGetters = true)
public class ExecutionResultReplyData {

    /**
     * The id of the solution to be processed.
     */
    private final long solutionId;

    /**
     * The id of the test case to be processed.
     */
    private final long testCaseId;


    /**
     * Constructor.
     *
     * @param solutionId The id of the solution to be processed.
     * @param testCaseId The id of the test case to be processed.
     */
    public ExecutionResultReplyData(final long solutionId, final long testCaseId) {
        this.solutionId = solutionId;
        this.testCaseId = testCaseId;
    }
}
