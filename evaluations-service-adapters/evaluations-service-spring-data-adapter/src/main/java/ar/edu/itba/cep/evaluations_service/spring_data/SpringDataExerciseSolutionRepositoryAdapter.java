package ar.edu.itba.cep.evaluations_service.spring_data;

import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExamRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExerciseSolutionRepository;
import com.bellotapps.webapps_commons.exceptions.NotImplementedException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
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
        throw new NotImplementedException();
    }

    @Override
    public long count() {
        throw new NotImplementedException();
    }

    @Override
    public boolean existsById(final Long aLong) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public Optional<ExerciseSolution> findById(final Long aLong) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public Iterable<ExerciseSolution> findAll() {
        throw new NotImplementedException();
    }

    @Override
    public <S extends ExerciseSolution> S save(final S entity) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public <S extends ExerciseSolution> void delete(final S entity) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public void deleteById(final Long aLong) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public void deleteAll() {
        throw new NotImplementedException();
    }
}
