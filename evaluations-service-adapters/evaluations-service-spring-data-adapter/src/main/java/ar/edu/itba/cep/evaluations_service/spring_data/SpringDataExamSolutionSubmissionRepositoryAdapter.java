package ar.edu.itba.cep.evaluations_service.spring_data;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.repositories.ExamSolutionSubmissionRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExamSolutionSubmissionRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExerciseRepository;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.paging_and_sorting.PagingMapper;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A concrete implementation of an {@link ExamSolutionSubmissionRepository}
 * which acts as an adapter for a {@link SpringDataExerciseRepository}.
 */
@Repository
public class SpringDataExamSolutionSubmissionRepositoryAdapter
        implements ExamSolutionSubmissionRepository, BasicRepositoryAdapter<ExamSolutionSubmission, Long> {

    /**
     * A {@link SpringDataExamSolutionSubmissionRepository} to which all operations are delegated.
     */
    private final SpringDataExamSolutionSubmissionRepository repository;


    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataExamSolutionSubmissionRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataExamSolutionSubmissionRepositoryAdapter(
            final SpringDataExamSolutionSubmissionRepository repository) {
        this.repository = repository;
    }


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public CrudRepository<ExamSolutionSubmission, Long> getCrudRepository() {
        return repository;
    }


    // ================================================================================================================
    // ExerciseRepository specific methods
    // ================================================================================================================

    @Override
    public Page<ExamSolutionSubmission> getByExam(final Exam exam, final PagingRequest pagingRequest) {
        final var pageable = PagingMapper.map(pagingRequest);
        final var page = repository.getByExam(exam, pageable);
        return PagingMapper.map(page);
    }

    @Override
    public List<ExamSolutionSubmission> getByExamAndState(final Exam exam, final ExamSolutionSubmission.State state) {
        return repository.getByExamAndState(exam, state);
    }

    @Override
    public void deleteExamSubmissions(final Exam exam) {
        repository.deleteByExam(exam);
    }

    @Override
    public Optional<ExamSolutionSubmission> getSubmissionFor(final Exam exam, final String submitter) {
        return repository.getByExamAndSubmitter(exam, submitter);
    }

    @Override
    public boolean existsSubmissionFor(final Exam exam, final String submitter) {
        return repository.existsByExamAndSubmitter(exam, submitter);
    }
}
