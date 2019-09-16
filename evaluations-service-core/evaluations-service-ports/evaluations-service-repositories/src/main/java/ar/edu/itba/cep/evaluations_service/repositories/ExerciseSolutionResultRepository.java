package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.models.TestCase;

import java.util.List;
import java.util.Optional;

/**
 * A port out of the application that allows {@link ExerciseSolution} persistence.
 */
public interface ExerciseSolutionResultRepository {

    /**
     * Saves the given {@code result}. Use the returned entity instance for further operation.
     *
     * @param result The {@link ExerciseSolutionResult} to be saved.
     * @param <S>    Concrete type of {@link ExerciseSolutionResult}.
     * @return The saved {@link ExerciseSolutionResult}.
     * @throws IllegalArgumentException If the given {@code result} is {@code null}.
     */
    <S extends ExerciseSolutionResult> S save(final S result) throws IllegalArgumentException;

    /**
     * Finds the {@link ExerciseSolutionResult} for the given {@code solution} – {@code testCase} tuple.
     *
     * @param solution The {@link ExerciseSolution} referenced in the returned {@link ExerciseSolutionResult}.
     * @param testCase The {@link TestCase} referenced in the returned {@link ExerciseSolutionResult}.
     * @return An {@link Optional} containing the requested {@link ExerciseSolutionResult} if it exists,
     * or empty otherwise.
     */
    Optional<ExerciseSolutionResult> find(final ExerciseSolution solution, final TestCase testCase);

    /**
     * Finds the {@link ExerciseSolutionResult} for the given {@code testCase} - {@code solution} tuple,
     * searching by those entities' ids.
     *
     * @param solutionId The {@link ExerciseSolution}'s id.
     * @param testCaseId The {@link TestCase}'s id.
     * @return An {@link Optional} containing the matching {@link ExerciseSolutionResult} if it exists,
     * or empty otherwise.
     */
    Optional<ExerciseSolutionResult> find(final long solutionId, final long testCaseId);

    /**
     * Finds all the {@link ExerciseSolutionResult} for a given {@code solution}.
     *
     * @param solution The {@link ExerciseSolution} referenced in the returned {@link ExerciseSolutionResult}s.
     * @return A {@link List} containing all the {@link ExerciseSolutionResult}
     * that reference the given {@link ExerciseSolution}
     */
    List<ExerciseSolutionResult> find(final ExerciseSolution solution);
}
