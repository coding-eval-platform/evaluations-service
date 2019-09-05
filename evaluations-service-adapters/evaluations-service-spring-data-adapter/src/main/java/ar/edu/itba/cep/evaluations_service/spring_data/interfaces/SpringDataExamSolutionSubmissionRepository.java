package ar.edu.itba.cep.evaluations_service.spring_data.interfaces;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A repository for {@link ExamSolutionSubmission}s.
 */
@Repository
public interface SpringDataExamSolutionSubmissionRepository extends CrudRepository<ExamSolutionSubmission, Long> {

    /**
     * Retrieves the {@link ExamSolutionSubmission}s belonging to the given {@code exam},
     * in a {@link Page} representation, according to the given {@code pageable}.
     *
     * @param exam     The {@link Exam} owning the {@link ExamSolutionSubmission}s being returned.
     * @param pageable The {@link Pageable} that indicates page number, size, sorting options, etc.
     * @return A {@link Page} with the {@link ExamSolutionSubmission} belonging to the given {@code exam},
     * configured according to the given {@code pageable}.
     */
    Page<ExamSolutionSubmission> getByExam(final Exam exam, final Pageable pageable);

    /**
     * Deletes the {@link ExamSolutionSubmission}s belonging to the given {@code exam}.
     *
     * @param exam The {@link Exam} owning the {@link ExamSolutionSubmission}s being deleted.
     */
    void deleteByExam(final Exam exam);

    /**
     * Retrieves the {@link ExamSolutionSubmission} belonging to the given {@code exam} and {@code submitter}.
     *
     * @param exam      The {@link Exam} owning the {@link ExamSolutionSubmission}s being returned.
     * @param submitter The submitter.
     * @return An {@link Optional} containing the matching {@link ExamSolutionSubmission} if it exists,
     * or empty otherwise.
     */
    Optional<ExamSolutionSubmission> getByExamAndSubmitter(final Exam exam, final String submitter);

    /**
     * Indicates whether an {@link ExamSolutionSubmission} exists for the given {@code exam} and {@code submitter}.
     *
     * @param exam      The {@link Exam} to be checked.
     * @param submitter The submitter.
     * @return {@code true} if a matching {@link ExamSolutionSubmission} exists, or {@code false} otherwise.
     */
    boolean existsByExamAndSubmitter(final Exam exam, final String submitter);
}
