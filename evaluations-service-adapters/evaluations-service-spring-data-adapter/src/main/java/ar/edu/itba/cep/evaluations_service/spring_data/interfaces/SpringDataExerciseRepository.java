package ar.edu.itba.cep.evaluations_service.spring_data.interfaces;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A repository for {@link Exercise}s.
 */
@Repository
public interface SpringDataExerciseRepository extends CrudRepository<Exercise, Long> {

    /**
     * Retrieves the {@link Exercise}s belonging to the given {@code exam}.
     *
     * @param exam The {@link Exam} owning the {@link Exercise}s being returned.
     * @return The {@link Exercise}s belonging to the given {@code exam}.
     */
    List<Exercise> getByExam(final Exam exam);

    /**
     * Deletes the {@link Exercise}s belonging to the given {@code exam}.
     *
     * @param exam The {@link Exam} owning the {@link Exercise}s being deleted.
     */
    void deleteByExam(final Exam exam);
}
