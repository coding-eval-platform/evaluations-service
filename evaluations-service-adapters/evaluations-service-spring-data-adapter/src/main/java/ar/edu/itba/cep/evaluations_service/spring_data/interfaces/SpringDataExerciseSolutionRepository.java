package ar.edu.itba.cep.evaluations_service.spring_data.interfaces;

import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository for {@link ExerciseSolution}s.
 */
@Repository
public interface SpringDataExerciseSolutionRepository extends CrudRepository<ExerciseSolution, Long> {

    /**
     * Retrieves the {@link ExerciseSolution}s that answers the question of the given {@code exercise},
     * in a paginated view.
     *
     * @param exercise The {@link Exercise} being answered by the returned {@link ExerciseSolution}s.
     * @param pageable The {@link Pageable} containing paging and sorting data.
     * @return A {@link Page} of {@link ExerciseSolution} answering the given {@link Exercise}'s question,
     * meeting the paging and sorting information of the given {@link Pageable}.
     */
    Page<ExerciseSolution> getByExercise(final Exercise exercise, Pageable pageable);
}
