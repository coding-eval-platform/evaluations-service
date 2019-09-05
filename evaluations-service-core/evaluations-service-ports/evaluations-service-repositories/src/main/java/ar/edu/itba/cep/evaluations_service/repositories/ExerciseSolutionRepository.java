package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.List;

/**
 * A port out of the application that allows {@link ExerciseSolution} persistence.
 */
public interface ExerciseSolutionRepository extends BasicRepository<ExerciseSolution, Long> {

    /**
     * Returns a {@link List} of {@link ExerciseSolution} for the given {@code submission}.
     *
     * @param submission The {@link ExamSolutionSubmission} to which the returned {@link ExerciseSolution}s belongs to.
     * @return A {@link List} with the {@link ExerciseSolution}.
     */
    List<ExerciseSolution> getExerciseSolutions(final ExamSolutionSubmission submission);
}
