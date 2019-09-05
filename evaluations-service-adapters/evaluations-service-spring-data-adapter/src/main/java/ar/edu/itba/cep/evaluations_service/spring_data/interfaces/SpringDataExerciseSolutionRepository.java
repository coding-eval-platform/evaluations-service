package ar.edu.itba.cep.evaluations_service.spring_data.interfaces;

import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A repository for {@link ExerciseSolution}s.
 */
@Repository
public interface SpringDataExerciseSolutionRepository extends CrudRepository<ExerciseSolution, Long> {

    /**
     * Returns a {@link List} of {@link ExerciseSolution} for the given {@code submission}.
     *
     * @param submission The {@link ExamSolutionSubmission} to which the returned {@link ExerciseSolution}s belongs to.
     * @return A {@link List} with the {@link ExerciseSolution}.
     */
    List<ExerciseSolution> getBySubmission(final ExamSolutionSubmission submission);
}
