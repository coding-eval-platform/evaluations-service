package ar.edu.itba.cep.evaluations_service.spring_data;

import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExerciseSolutionRepository;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A concrete implementation of an {@link ExerciseSolutionRepository}
 * which acts as an adapter for a {@link SpringDataExerciseSolutionRepository}.
 */
@Repository
@AllArgsConstructor
public class SpringDataExerciseSolutionRepositoryAdapter
        implements ExerciseSolutionRepository, BasicRepositoryAdapter<ExerciseSolution, Long> {

    /**
     * A {@link SpringDataExerciseSolutionRepository} to which all operations are delegated.
     */
    private final SpringDataExerciseSolutionRepository repository;


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
    public List<ExerciseSolution> getExerciseSolutions(final ExamSolutionSubmission submission) {
        return repository.getBySubmission(submission);
    }
}
