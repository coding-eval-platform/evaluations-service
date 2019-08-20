package ar.edu.itba.cep.evaluations_service.spring_data;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataTestCaseRepository;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A concrete implementation of a {@link TestCaseRepository}
 * which acts as an adapter for a {@link SpringDataTestCaseRepository}.
 */
@Repository
public class SpringDataTestCaseRepositoryAdapter
        implements TestCaseRepository, BasicRepositoryAdapter<TestCase, Long> {

    /**
     * A {@link SpringDataTestCaseRepository} to which all operations are delegated.
     */
    private final SpringDataTestCaseRepository repository;


    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataTestCaseRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataTestCaseRepositoryAdapter(final SpringDataTestCaseRepository repository) {
        this.repository = repository;
    }


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public CrudRepository<TestCase, Long> getCrudRepository() {
        return repository;
    }


    // ================================================================================================================
    // TestCaseRepository specific methods
    // ================================================================================================================

    @Override
    public List<TestCase> getExercisePublicTestCases(final Exercise exercise) {
        return repository.getByBelongsToAndVisibility(exercise, TestCase.Visibility.PUBLIC);
    }

    @Override
    public List<TestCase> getExercisePrivateTestCases(final Exercise exercise) {
        return repository.getByBelongsToAndVisibility(exercise, TestCase.Visibility.PRIVATE);
    }

    @Override
    public void deleteExerciseTestCases(final Exercise exercise) {
        repository.deleteByExercise(exercise);
    }

    @Override
    public void deleteExamTestCases(final Exam exam) {
        repository.deleteByExerciseExam(exam);
    }
}
