package ar.edu.itba.cep.evaluations_service.spring_data_repositories;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataExamRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataExerciseSolutionResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A concrete implementation of a {@link ExerciseSolutionResultRepository}
 * which acts as an adapter for a {@link SpringDataExerciseSolutionResultRepository}.
 */
@Repository
public class SpringDataExerciseSolutionResultRepositoryAdapter implements ExerciseSolutionResultRepository {

    /**
     * A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    private final SpringDataExerciseSolutionResultRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataExerciseSolutionResultRepositoryAdapter(
            final SpringDataExerciseSolutionResultRepository repository) {
        this.repository = repository;
    }


    @Override
    public <S extends ExerciseSolutionResult> S save(final S result) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<ExerciseSolutionResult> find(final ExerciseSolution solution, final TestCase testCase) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ExerciseSolutionResult> find(final ExerciseSolution solution) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}