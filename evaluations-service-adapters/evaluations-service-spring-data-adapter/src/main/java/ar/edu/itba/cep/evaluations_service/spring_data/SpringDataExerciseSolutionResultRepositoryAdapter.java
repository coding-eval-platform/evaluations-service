package ar.edu.itba.cep.evaluations_service.spring_data;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExerciseSolutionResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A concrete implementation of an {@link ExerciseSolutionResultRepository}
 * which acts as an adapter for a {@link SpringDataExerciseSolutionResultRepository}.
 */
@Repository
public class SpringDataExerciseSolutionResultRepositoryAdapter implements ExerciseSolutionResultRepository {

    /**
     * A {@link SpringDataExerciseSolutionResultRepository} to which all operations are delegated.
     */
    private final SpringDataExerciseSolutionResultRepository repository;


    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataExerciseSolutionResultRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataExerciseSolutionResultRepositoryAdapter(
            final SpringDataExerciseSolutionResultRepository repository) {
        this.repository = repository;
    }


    @Override
    public <S extends ExerciseSolutionResult> S save(final S result) throws IllegalArgumentException {
        return repository.save(result);
    }

    @Override
    public Optional<ExerciseSolutionResult> find(final ExerciseSolution solution, final TestCase testCase) {
        return repository.findBySolutionAndTestCase(solution, testCase);
    }

    @Override
    public Optional<ExerciseSolutionResult> find(final long solutionId, final long testCaseId) {
        return repository.findBySolutionIdAndTestCaseId(solutionId, testCaseId);
    }

    @Override
    public List<ExerciseSolutionResult> find(final ExerciseSolution solution) {
        return repository.findBySolution(solution);
    }
}
