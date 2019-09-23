package ar.edu.itba.cep.evaluations_service.commands.executor_service;

/**
 * Class with several constants to be used across the Kafka Command Senders' Executor Service proxy module.
 */
/* package */ class ResponseIdDataHeaders {

    // ================================================================================================================
    // Headers
    // ================================================================================================================

    /**
     * The Solution Id header key. Used together with the {@link #TEST_CASE_ID_HEADER} to match an
     * {@link ar.edu.itba.cep.executor.models.ExecutionResponse} with an
     * {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}.
     */
    /* package */ static final String SOLUTION_ID_HEADER = "Solution-Id";
    /**
     * The Test Case Id header key. Used together with the {@link #SOLUTION_ID_HEADER} to match an
     * {@link ar.edu.itba.cep.executor.models.ExecutionResponse} with an
     * {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}.
     */
    /* package */ static final String TEST_CASE_ID_HEADER = "TestCase-Id";
}
