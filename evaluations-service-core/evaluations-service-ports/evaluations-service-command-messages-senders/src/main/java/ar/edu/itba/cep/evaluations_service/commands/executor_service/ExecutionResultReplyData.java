package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import java.util.Objects;

/**
 * Class containing data to handle an execution result.
 */
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


    /**
     * @return The id of the solution to be processed.
     */
    public long getSolutionId() {
        return solutionId;
    }

    /**
     * @return The id of the test case to be processed.
     */
    public long getTestCaseId() {
        return testCaseId;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExecutionResultReplyData)) {
            return false;
        }
        final ExecutionResultReplyData that = (ExecutionResultReplyData) o;
        return solutionId == that.solutionId &&
                testCaseId == that.testCaseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(solutionId, testCaseId);
    }

    @Override
    public String toString() {
        return "ExecutionResultReplyData{" +
                "solutionId=" + solutionId +
                ", testCaseId=" + testCaseId +
                '}';
    }
}
