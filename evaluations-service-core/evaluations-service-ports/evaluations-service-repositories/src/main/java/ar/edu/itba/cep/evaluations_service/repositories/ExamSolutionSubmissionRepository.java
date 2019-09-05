package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.Optional;

/**
 * A port out of the application that allows {@link ExamSolutionSubmission} persistence.
 */
public interface ExamSolutionSubmissionRepository extends BasicRepository<ExamSolutionSubmission, Long> {

    /**
     * Retrieves the {@link ExamSolutionSubmission}s belonging to the given {@code exam},
     * in a {@link Page} representation, according to the given {@code pagingRequest}.
     *
     * @param exam          The {@link Exam} owning the {@link ExamSolutionSubmission}s being returned.
     * @param pagingRequest The {@link PagingRequest} that indicates page number, size, sorting options, etc.
     * @return A {@link Page} with the {@link ExamSolutionSubmission} belonging to the given {@code exam},
     * configured according to the given {@code pagingRequest}.
     */
    Page<ExamSolutionSubmission> getByExam(final Exam exam, final PagingRequest pagingRequest);

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

    /**
     * Indicates whether an {@link ExamSolutionSubmission} exists for the given {@code exam} and {@code submitter}.
     *
     * @param exam      The {@link Exam} to be checked.
     * @param submitter The submitter.
     * @return {@code true} if a matching {@link ExamSolutionSubmission} exists, or {@code false} otherwise.
     */
    boolean existsSubmissionFor(final Exam exam, final String submitter);
}
