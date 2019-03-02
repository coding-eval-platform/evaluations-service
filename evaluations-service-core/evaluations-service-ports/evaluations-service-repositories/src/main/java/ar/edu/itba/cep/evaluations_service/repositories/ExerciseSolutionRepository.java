package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import com.bellotapps.webapps_commons.persistence.repository_utils.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.PagingRequest;

/**
 * A port out of the application that allows {@link ExerciseSolution} persistence.
 */
public interface ExerciseSolutionRepository extends BasicRepository<ExerciseSolution, Long> {

    /**
     * Returns a {@link Page} of {@link ExerciseSolution} according to the given {@code pagingRequest},
     * for the given {@link Exercise}.
     *
     * @param exercise      The {@link Exercise} being answered by the returned {@link ExerciseSolution}s.
     * @param pagingRequest The {@link PagingRequest} that indicates page number, size, sorting options, etc.
     * @return A {@link Page} of {@link ExerciseSolution}s.
     */
    Page<ExerciseSolution> getExerciseSolutions(final Exercise exercise, final PagingRequest pagingRequest);
}
