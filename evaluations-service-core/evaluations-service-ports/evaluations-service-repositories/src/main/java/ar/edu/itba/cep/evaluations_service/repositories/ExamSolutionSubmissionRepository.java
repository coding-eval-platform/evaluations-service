package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.List;
import java.util.Optional;

/**
 * A port out of the application that allows {@link ExamSolutionSubmission} persistence.
 */
public interface ExamSolutionSubmissionRepository extends BasicRepository<ExamSolutionSubmission, Long> {

    /**
     * Retrieves the {@link ExamSolutionSubmission}s belonging to the given {@code exam}.
     *
     * @param exam The {@link Exam} owning the {@link ExamSolutionSubmission}s being returned.
     * @return The {@link ExamSolutionSubmission}s belonging to the given {@code exam}.
     */
    List<ExamSolutionSubmission> getByExam(final Exam exam);

    /**
     * Deletes the {@link ExamSolutionSubmission}s belonging to the given {@code exam}.
     *
     * @param exam The {@link Exam} owning the {@link ExamSolutionSubmission}s being deleted.
     */
    void deleteExamSubmissions(final Exam exam);

    /**
     * Retrieves the {@link ExamSolutionSubmission} belonging to the given {@code exam} and {@code submitter}.
     *
     * @param exam      The {@link Exam} owning the {@link ExamSolutionSubmission}s being returned.
     * @param submitter The submitter.
     * @return An {@link Optional} containing the matching {@link ExamSolutionSubmission} if it exists,
     * or empty otherwise.
     */
    Optional<ExamSolutionSubmission> getSubmissionFor(final Exam exam, final String submitter);
}
