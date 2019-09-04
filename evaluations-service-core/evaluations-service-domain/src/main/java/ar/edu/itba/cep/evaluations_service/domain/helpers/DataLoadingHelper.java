package ar.edu.itba.cep.evaluations_service.domain.helpers;

import ar.edu.itba.cep.evaluations_service.models.*;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;

import java.util.Optional;
import java.util.function.Function;

/**
 * Helper class that aids in the data loading tasks.
 */
public final class DataLoadingHelper {

    /**
     * Private constructor to avoid instantiation.
     */
    private DataLoadingHelper() {
    }

    /**
     * Loads the {@link Exam} with the given {@code id} if it exists.
     *
     * @param id             The {@link Exam}'s id.
     * @param examRepository The {@link ExamRepository} used to load the {@link Exam}.
     * @return The {@link Exam} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link Exam} with the given {@code id}.
     */
    public static Exam loadExam(final ExamRepository examRepository, final long id) throws NoSuchEntityException {
        return loadEntity(examRepository::findById, id);
    }

    /**
     * Loads the {@link Exercise} with the given {@code id} if it exists.
     *
     * @param id                 The {@link Exercise}'s id.
     * @param exerciseRepository The {@link ExerciseRepository} used to load the {@link Exercise}.
     * @return The {@link Exercise} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link Exercise} with the given {@code id}.
     */
    public static Exercise loadExercise(final ExerciseRepository exerciseRepository, final long id)
            throws NoSuchEntityException {
        return loadEntity(exerciseRepository::findById, id);
    }

    /**
     * Loads the {@link TestCase} with the given {@code id} if it exists.
     *
     * @param id                 The {@link TestCase}'s id.
     * @param testCaseRepository The {@link TestCaseRepository} used to load the {@link TestCase}.
     * @return The {@link TestCase} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link TestCase} with the given {@code id}.
     */
    public static TestCase loadTestCase(final TestCaseRepository testCaseRepository, final long id)
            throws NoSuchEntityException {
        return loadEntity(testCaseRepository::findById, id);
    }

    /**
     * Loads the {@link ExamSolutionSubmission} with the given {@code id} if it exists.
     *
     * @param id                               The {@link ExamSolutionSubmission}'s id.
     * @param examSolutionSubmissionRepository The {@link ExamSolutionSubmissionRepository}
     *                                         used to load the {@link ExamSolutionSubmission}.
     * @return The {@link ExamSolutionSubmission} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link ExamSolutionSubmission} with the given {@code id}.
     */
    public static ExamSolutionSubmission loadExamSolutionSubmission(
            final ExamSolutionSubmissionRepository examSolutionSubmissionRepository,
            final long id) throws NoSuchEntityException {
        return loadEntity(examSolutionSubmissionRepository::findById, id);
    }

    /**
     * Loads the {@link ExerciseSolution} with the given {@code id} if it exists.
     *
     * @param id                         The {@link ExerciseSolution}'s id.
     * @param exerciseSolutionRepository The {@link ExerciseSolutionRepository}
     *                                   used to load the {@link ExerciseSolution}.
     * @return The {@link ExerciseSolution} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link ExerciseSolution} with the given {@code id}.
     */
    public static ExerciseSolution loadSolution(final ExerciseSolutionRepository exerciseSolutionRepository, final long id)
            throws NoSuchEntityException {
        return loadEntity(exerciseSolutionRepository::findById, id);
    }

    /**
     * Loads the entity if type {@code T} with the given {@code id},
     * retrieving it with the given {@code entityRetriever}.
     *
     * @param entityRetriever A {@link Function} that given an {@code id} of type {@code ID}
     *                        retrieves an {@link Optional} of the entity to be loaded.
     * @param id              The id of the entity.
     * @param <T>             The concrete type of the entity.
     * @param <ID>            The concrete type of the entity's id.
     * @return The entity of type {@code T} with the given {@code id}.
     * @throws NoSuchEntityException IF there is no entity of type {@code T} with the given {@code id}.
     */
    public static <T, ID> T loadEntity(final Function<ID, Optional<T>> entityRetriever, final ID id)
            throws NoSuchEntityException {
        return entityRetriever.apply(id).orElseThrow(NoSuchEntityException::new);
    }
}
