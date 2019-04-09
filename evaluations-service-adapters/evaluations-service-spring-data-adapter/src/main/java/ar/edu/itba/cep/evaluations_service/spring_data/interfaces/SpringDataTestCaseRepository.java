package ar.edu.itba.cep.evaluations_service.spring_data.interfaces;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A repository for {@link TestCase}s.
 */
@Repository
public interface SpringDataTestCaseRepository extends CrudRepository<TestCase, Long> {

    /**
     * Retrieves the {@link TestCase}s belonging to the given {@code exercise}, applying visibility filter.
     *
     * @param exercise   The {@link Exercise} owning the {@link TestCase}s being returned.
     * @param visibility The {@link TestCase.Visibility} of the retrieved {@link TestCase}s.
     * @return The {@link TestCase}s belonging to the given {@code exercise}, with the given {@code visibility}.
     */
    @Query(value = "SELECT DISTINCT tc " +
            "       FROM TestCase tc " +
            "           LEFT JOIN FETCH tc.inputs " +
            "           LEFT JOIN FETCH tc.expectedOutputs " +
            "       WHERE tc.belongsTo = :exercise AND tc.visibility = :visibility")
    List<TestCase> getByBelongsToAndVisibility(
            @Param("exercise") final Exercise exercise,
            @Param("visibility") final TestCase.Visibility visibility
    );


    /**
     * Deletes the {@link TestCase}s belonging to the given {@code exercise}.
     *
     * @param exercise The {@link Exercise} owning the {@link TestCase}s being deleted.
     */
    void deleteByBelongsTo(final Exercise exercise);

    /**
     * Deletes the {@link TestCase}s belonging to {@link Exercise}s owned by the given {@code exam}.
     *
     * @param exam The {@link Exam} owning the {@link Exercise}s that own the {@link TestCase}s being deleted.
     */
    void deleteByBelongsToBelongsTo(final Exam exam);
}
