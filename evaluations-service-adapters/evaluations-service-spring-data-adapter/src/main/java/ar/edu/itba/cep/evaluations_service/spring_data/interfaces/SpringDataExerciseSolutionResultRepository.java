package ar.edu.itba.cep.evaluations_service.spring_data.interfaces;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A repository for {@link ExerciseSolutionResult}s.
 */
@Repository
public interface SpringDataExerciseSolutionResultRepository extends CrudRepository<ExerciseSolutionResult, Long> {

    /**
     * Retrieves the {@link ExerciseSolutionResult} that matches the given {@code testCase} - {@code solution} tuple.
     *
     * @param exerciseSolution The {@link ExerciseSolution}.
     * @param testCase         The {@link TestCase}.
     * @return An {@link Optional} containing the matching {@link ExerciseSolutionResult} if it exists,
     * or empty otherwise.
     */
    Optional<ExerciseSolutionResult> findBySolutionAndTestCase(
            final ExerciseSolution exerciseSolution,
            final TestCase testCase
    );

    /**
     * Retrieves all the {@link ExerciseSolutionResult}s belonging to the given {@code solution}.
     *
     * @param solution The {@link ExerciseSolution} owning the {@link ExerciseSolutionResult}s being returned.
     * @return A {@link List} containing the {@link ExerciseSolutionResult}s belonging to the given {@code solution}.
     */
    List<ExerciseSolutionResult> findBySolution(final ExerciseSolution solution);
}
