package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.messages_sender.ExecutorServiceCommandResultHandler;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Object in charge of processing execution results.
 */
@Service
public class ExecutionProcessor implements ExecutorServiceCommandResultHandler {

    /**
     * Repository for {@link TestCase}s.
     */
    private final TestCaseRepository testCaseRepository;
    /**
     * Repository for {@link ExerciseSolution}s.
     */
    private final ExerciseSolutionRepository exerciseSolutionRepository;
    /**
     * Repository for {@link ExerciseSolutionResult}s.
     */
    private final ExerciseSolutionResultRepository exerciseSolutionResultRepository;

    /**
     * Constructor.
     *
     * @param testCaseRepository               Repository for {@link TestCase}s.
     * @param exerciseSolutionRepository       Repository for {@link ExerciseSolution}s.
     * @param exerciseSolutionResultRepository Repository for {@link ExerciseSolutionResult}s.
     */
    @Autowired
    public ExecutionProcessor(
            final TestCaseRepository testCaseRepository,
            final ExerciseSolutionRepository exerciseSolutionRepository,
            final ExerciseSolutionResultRepository exerciseSolutionResultRepository) {
        this.testCaseRepository = testCaseRepository;
        this.exerciseSolutionRepository = exerciseSolutionRepository;
        this.exerciseSolutionResultRepository = exerciseSolutionResultRepository;
    }

    @Override
    @Transactional
    public void processExecution(final long solutionId, final long testCaseId,
                                 final int exitCode, final List<String> stdOut, final List<String> stdErr)
            throws IllegalArgumentException {
//        // First, validate arguments
        Assert.notNull(stdOut, "The stdout list cannot be null");
        Assert.notNull(stdErr, "The stderr list cannot be null");

        // Load solution and test case (checking if they exist)
        final var solution = DomainHelper.loadSolution(exerciseSolutionRepository, solutionId);
        final var testCase = DomainHelper.loadTestCase(testCaseRepository, testCaseId);

        // State validation is not needed because the existence of a solution proves state validity

        // Check if exit code is zero, if there is no error output and if outputs match the expected outputs
        final var result = exitCode == 0 && stdErr.isEmpty() && testCase.getExpectedOutputs().equals(stdOut) ?
                ExerciseSolutionResult.Result.APPROVED :
                ExerciseSolutionResult.Result.FAILED;

        // Execution processing is finished. Now the result can be saved.
        final var solutionResult = new ExerciseSolutionResult(solution, testCase, result);
        exerciseSolutionResultRepository.save(solutionResult);
    }
}
