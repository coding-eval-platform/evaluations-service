package ar.edu.itba.cep.evaluations_service.spring_data_repositories;

import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataExamRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataExerciseSolutionRepository;
import com.bellotapps.webapps_commons.persistence.repository_utils.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.PagingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A concrete implementation of a {@link ExerciseSolutionRepository}
 * which acts as an adapter for a {@link SpringDataExerciseSolutionRepository}.
 */
@Repository
public class SpringDataExerciseSolutionRepositoryAdapter implements ExerciseSolutionRepository {

    /**
     * A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    private final SpringDataExerciseSolutionRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataExerciseSolutionRepositoryAdapter(final SpringDataExerciseSolutionRepository repository) {
        this.repository = repository;
    }


    @Override
    public Page<ExerciseSolution> getExerciseSolutions(final Exercise exercise, final PagingRequest pagingRequest) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean existsById(final Long aLong) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<ExerciseSolution> findById(final Long aLong) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<ExerciseSolution> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends ExerciseSolution> S save(final S entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends ExerciseSolution> void delete(final S entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteById(final Long aLong) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
