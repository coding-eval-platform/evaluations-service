package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.List;

/**
 * A port out of the application that allows {@link TestCase} persistence.
 */
public interface TestCaseRepository extends BasicRepository<TestCase, Long> {

    /**
     * Retrieves the {@link TestCase}s belonging to the given {@code exercise}.
     *
     * @param exercise The {@link Exercise} owning the {@link TestCase}s being returned.
     * @return The {@link TestCase}s belonging to the given {@code exercise}.
     */
    List<TestCase> getAllTestCases(final Exercise exercise);

    /**
     * Returns a {@link List} of public {@link TestCase}s belonging to the given {@code exercise}.
     *
     * @param exercise The {@link Exercise} to which all the public {@link TestCase}s to be returned belongs to.
     * @return A {@link List} containing the public {@link TestCase}s belonging to the given {@link Exercise}.
     */
    List<TestCase> getExercisePublicTestCases(final Exercise exercise);

    /**
     * Returns a {@link List} of private {@link TestCase}s belonging to the given {@code exercise}.
     *
     * @param exercise The {@link Exercise} to which all the private {@link TestCase}s to be returned belongs to.
     * @return A {@link List} containing the private {@link TestCase}s belonging to the given {@link Exercise}.
     */
    List<TestCase> getExercisePrivateTestCases(final Exercise exercise);

    /**
     * Deletes all the {@link TestCase}s belonging to the given {@code exercise}.
     *
     * @param exercise The {@link Exercise} to which all the {@link TestCase}s to be removed belongs to.
     */
    void deleteExerciseTestCases(final Exercise exercise);

    /**
     * Deletes all the {@link TestCase}s belonging to teh {@link Exercise}s that are owned by the given {@code exam}.
     *
     * @param exam The {@link Exam} owning the {@link Exercise}s to which all the {@link TestCase}s to be removed
     *             belongs to.
     */
    void deleteExamTestCases(final Exam exam);
}
