package ar.edu.itba.cep.evaluations_service.spring_data;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExerciseRepository;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * A concrete implementation of an {@link ExerciseRepository}
 * which acts as an adapter for a {@link SpringDataExerciseRepository}.
 */
@Repository
public class SpringDataExerciseRepositoryAdapter
        implements ExerciseRepository, BasicRepositoryAdapter<Exercise, Long> {

    /**
     * A {@link SpringDataExerciseRepository} to which all operations are delegated.
     */
    private final SpringDataExerciseRepository repository;


    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataExerciseRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataExerciseRepositoryAdapter(final SpringDataExerciseRepository repository) {
        this.repository = repository;
    }


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public CrudRepository<Exercise, Long> getCrudRepository() {
        return repository;
    }


    // ================================================================================================================
    // ExerciseRepository specific methods
    // ================================================================================================================

    @Override
    public List<Exercise> getExamExercises(final Exam exam) {
        return repository.getByExam(exam);
    }

    @Override
    public void deleteExamExercises(final Exam exam) {
        repository.deleteByExam(exam);
    }
}
