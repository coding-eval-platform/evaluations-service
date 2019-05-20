package ar.edu.itba.cep.evaluations_service.commands.executor_service;

/**
 * Class with several constants to be used across the Kafka Command Senders' Executor Service proxy module.
 */
/* package */ class Constants {

    // ================================================================================================================
    // Headers
    // ================================================================================================================

    /**
     * The Solution Id header key. Used together with the {@link #TEST_CASE_ID_HEADER} to match an
     * {@link ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult} with an
     * {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}.
     */
    /* package */ static final String SOLUTION_ID_HEADER = "Solution-Id";
    /**
     * The Test Case Id header key. Used together with the {@link #SOLUTION_ID_HEADER} to match an
     * {@link ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult} with an
     * {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}.
     */
    /* package */ static final String TEST_CASE_ID_HEADER = "TestCase-Id";
    /**
     * The Reply-Channel header key. Asked by the Executor Service in order to receive the response from it.
     */
    /* package */ static final String REPLY_CHANNEL_HEADER = "Reply-Channel";


    // ================================================================================================================
    // Kafka Listening Channels
    // ================================================================================================================

    /**
     * Topic in which the {@link ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult}s
     * command replies are received.
     */
    /* package */ static final String REPLY_CHANNEL = "EvaluationsService-Command-Replies";


    // ================================================================================================================
    // Kafka Sending Channels
    // ================================================================================================================

    /**
     * Topic to which {@link ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionRequest}s are sent
     * to the Executor Service.
     */
    /* package */ static final String EXECUTOR_SERVICE_COMMANDS_CHANNEL = "ExecutorService-Commands";
}
