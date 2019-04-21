package ar.edu.itba.cep.evaluations_service.messages_sender;

import java.util.List;

/**
 * A port out of the application that allows sending commands to the executor service.
 */
public interface ExecutorServiceCommandProxy {

    /**
     * Requests an execution to the executor service.
     *
     * @param code        The code to be executed.
     * @param inputs      The inputs for the execution.
     * @param handlerData An {@link ExecutionResultHandlerData} with information about how to process the result.
     */
    void requestExecution(final String code, final List<String> inputs, final ExecutionResultHandlerData handlerData);

    /**
     * Class containing data to be used by the handler callback of the
     * {@link #requestExecution(String, List, ExecutionResultHandlerData)} method.
     */
    class ExecutionResultHandlerData {

        /**
         * The id of the solution to be processed.
         */
        private final long solutionId;

        /**
         * The id of the test case to be processed.
         */
        private final long testCaseId;

        /**
         * @param solutionId The id of the solution to be processed.
         * @param testCaseId The id of the test case to be processed.
         */
        public ExecutionResultHandlerData(final long solutionId, final long testCaseId) {
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
    }
}
