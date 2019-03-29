package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.List;

/**
 * A port out of the application that allows {@link Exercise} persistence.
 */
public interface ExerciseRepository extends BasicRepository<Exercise, Long> {

    /**
     * Returns a {@link List} of {@link Exercise}s belonging to the given {@code exam}.
     *
     * @param exam The {@link Exam} to which all the {@link Exercise}s to be returned belongs to.
     * @return A {@link List} containing the {@link Exercise}s belonging to the given {@link Exam}.
     */
    List<Exercise> getExamExercises(final Exam exam);

    /**
     * Deletes all the {@link Exercise}s belonging to the given {@code exam}.
     *
     * @param exam The {@link Exam} to which all the {@link Exercise}s to be removed belongs to.
     */
    void deleteExamExercises(final Exam exam);
}
