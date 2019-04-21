package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;

/**
 * Helper class for the domain module.
 */
/* package */ class DomainHelper {

    /**
     * Loads the {@link Exam} with the given {@code id} if it exists.
     *
     * @param id The {@link Exam}'s id.
     * @return The {@link Exam} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link Exam} with the given {@code id}.
     */
    /* package */
    static Exam loadExam(final ExamRepository examRepository, final long id) throws NoSuchEntityException {
        return examRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }

    /**
     * Loads the {@link Exercise} with the given {@code id} if it exists.
     *
     * @param id The {@link Exercise}'s id.
     * @return The {@link Exercise} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link Exercise} with the given {@code id}.
     */
    /* package */
    static Exercise loadExercise(final ExerciseRepository exerciseRepository, final long id)
            throws NoSuchEntityException {
        return exerciseRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }

    /**
     * Loads the {@link TestCase} with the given {@code id} if it exists.
     *
     * @param id The {@link TestCase}'s id.
     * @return The {@link TestCase} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link TestCase} with the given {@code id}.
     */
    /* package */
    static TestCase loadTestCase(final TestCaseRepository testCaseRepository, final long id)
            throws NoSuchEntityException {
        return testCaseRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }

    /**
     * Loads the {@link ExerciseSolution} with the given {@code id} if it exists.
     *
     * @param id The {@link ExerciseSolution}'s id.
     * @return The {@link ExerciseSolution} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link ExerciseSolution} with the given {@code id}.
     */
    /* package */
    static ExerciseSolution loadSolution(final ExerciseSolutionRepository exerciseSolutionRepository, final long id)
            throws NoSuchEntityException {
        return exerciseSolutionRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }
}
