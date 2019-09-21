package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class containing data to handle an {@link ar.edu.itba.cep.executor.models.ExecutionResponse}.
 */
@Getter
@EqualsAndHashCode
@ToString(doNotUseGetters = true)
public class ExecutionResponseReplyData {

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
    public ExecutionResponseReplyData(final long solutionId, final long testCaseId) {
        this.solutionId = solutionId;
        this.testCaseId = testCaseId;
    }
}
