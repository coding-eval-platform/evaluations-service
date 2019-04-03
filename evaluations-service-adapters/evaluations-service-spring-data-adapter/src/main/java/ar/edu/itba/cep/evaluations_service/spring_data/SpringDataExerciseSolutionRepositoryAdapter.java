package ar.edu.itba.cep.evaluations_service.spring_data;

import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExamRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExerciseSolutionRepository;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.paging_and_sorting.PagingMapper;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * A concrete implementation of a {@link ExerciseSolutionRepository}
 * which acts as an adapter for a {@link SpringDataExerciseSolutionRepository}.
 */
@Repository
public class SpringDataExerciseSolutionRepositoryAdapter
        implements ExerciseSolutionRepository, BasicRepositoryAdapter<ExerciseSolution, Long> {

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


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public CrudRepository<ExerciseSolution, Long> getCrudRepository() {
        return repository;
    }


    // ================================================================================================================
    // ExerciseSolutionRepository specific methods
    // ================================================================================================================

    @Override
    public Page<ExerciseSolution> getExerciseSolutions(final Exercise exercise, final PagingRequest pagingRequest) {
        final var pageable = PagingMapper.map(pagingRequest);
        final var page = repository.getByBelongsTo(exercise, pageable);
        return PagingMapper.map(page);
    }
}
