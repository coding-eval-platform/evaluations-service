package ar.edu.itba.cep.evaluations_service.spring_data;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExamRepository;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.paging_and_sorting.PagingMapper;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.PagingRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * A concrete implementation of a {@link ExamRepository}
 * which acts as an adapter for a {@link SpringDataExamRepositoryAdapter}.
 */
@Repository
public class SpringDataExamRepositoryAdapter
        implements ExamRepository, BasicRepositoryAdapter<Exam, Long>, PagingRepositoryAdapter<Exam, Long> {

    /**
     * A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    private final SpringDataExamRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataExamRepositoryAdapter(final SpringDataExamRepository repository) {
        this.repository = repository;
    }


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public CrudRepository<Exam, Long> getCrudRepository() {
        return repository;
    }

    @Override
    public PagingAndSortingRepository<Exam, Long> getPagingAndSortingRepository() {
        return repository;
    }


    // ================================================================================================================
    // ExamRepository specific methods
    // ================================================================================================================

    @Override
    public Page<Exam> getOwnedBy(final String owner, final PagingRequest pagingRequest) {
        final var pageable = PagingMapper.map(pagingRequest);
        final var page = repository.findByOwners(owner, pageable);
        return PagingMapper.map(page);
    }
}
