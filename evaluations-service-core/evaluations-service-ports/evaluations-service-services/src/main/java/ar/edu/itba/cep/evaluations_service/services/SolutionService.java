package ar.edu.itba.cep.evaluations_service.services;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;

import java.util.List;
import java.util.Optional;


/**
 * A port into the application that allows {@link ExamSolutionSubmission} and {@link ExerciseSolution} management.
 */
public interface SolutionService {

    // ================================================================================================================
    // Submissions
    // ================================================================================================================

    /**
     * Returns all the {@link ExamSolutionSubmission}s for the {@link Exam} with the given {@code examId},
     * in a paginated view.
     *
     * @param examId        The id of the {@link Exam} to which the returned {@link ExamSolutionSubmission} belongs to.
     * @param pagingRequest The {@link PagingRequest} containing paging data.
     * @return The requested {@link Page} of {@link ExamSolutionSubmission}s.
     * @throws NoSuchEntityException If there is no {@link Exam} with the given {@code examId}.
     */
    Page<ExamSolutionSubmission> getSolutionSubmissionsForExam(final long examId, final PagingRequest pagingRequest)
            throws NoSuchEntityException;

    /**
     * Returns the {@link ExamSolutionSubmission} with the given {@code submissionId}.
     *
     * @param submissionId The id of the {@link ExamSolutionSubmission} to be retrieved.
     * @return An {@link Optional} containing the {@link ExamSolutionSubmission} with the given {@code submissionId}
     * if it exists, or empty otherwise.
     */
    Optional<ExamSolutionSubmission> getSubmission(final long submissionId);

    /**
     * Creates an {@link ExamSolutionSubmission} for the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} for which the {@link ExamSolutionSubmission} is created.
     * @return The created {@link ExamSolutionSubmission}.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws UniqueViolationException    If the currently authenticated user
     *                                     already created an {@link ExamSolutionSubmission} for the {@link Exam}
     *                                     with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam} is not in {@link Exam.State#IN_PROGRESS} state.
     * @apiNote It cannot be executed if the {@link Exam} is not in {@link Exam.State#IN_PROGRESS} state.
     */
    ExamSolutionSubmission createExamSolutionSubmission(final long examId)
            throws NoSuchEntityException, UniqueViolationException, IllegalStateException;

    /**
     * Submits the {@link ExamSolutionSubmission} with the given {@code submissionId}.
     *
     * @param submissionId The id of the {@link ExamSolutionSubmission} to be submitted.
     * @throws NoSuchEntityException If there is no {@link ExamSolutionSubmission} with the given {@code submissionId}.
     * @throws IllegalStateException If the {@link Exam} to which the {@link ExamSolutionSubmission} belongs to
     *                               is not in {@link Exam.State#IN_PROGRESS} state.
     * @apiNote It cannot be executed if the {@link Exam}  to which the {@link ExamSolutionSubmission} belongs to
     * is not in {@link Exam.State#IN_PROGRESS} state.
     */
    void submitSolutions(final long submissionId) throws NoSuchEntityException, IllegalStateException;

    /**
     * Scores the {@link ExamSolutionSubmission} with the given {@code submissionId}.
     *
     * @param submissionId The id of the {@link ExamSolutionSubmission} to be scored.
     * @throws NoSuchEntityException       If there is no {@link ExamSolutionSubmission}
     *                                     with the given {@code submissionId}.
     * @throws IllegalEntityStateException If the given {@link ExamSolutionSubmission} is not submitted,
     *                                     or if it owns an {@link ExerciseSolution}
     *                                     for which there are pending executions.
     */
    void scoreSubmission(final long submissionId) throws NoSuchEntityException, IllegalEntityStateException;


    // ================================================================================================================
    // Solutions
    // ================================================================================================================

    /**
     * Retrieves the {@link ExerciseSolution}s of the {@link ExamSolutionSubmission}
     * with the given {@code submissionId}.
     *
     * @param submissionId The id of the {@link ExamSolutionSubmission}
     *                     of the {@link ExerciseSolution} being retrieved.
     * @return A {@link List} holding the matching {@link ExerciseSolution}s.
     * @throws NoSuchEntityException If there is no {@link ExamSolutionSubmission} with the given {@code submissionId}.
     */
    List<ExerciseSolution> getSolutionsForSubmission(final long submissionId) throws NoSuchEntityException;

    /**
     * Returns the {@link ExerciseSolution} with the given {@code solutionId}.
     *
     * @param solutionId The id of the {@link ExerciseSolution} to be retrieved.
     * @return An {@link Optional} containing the {@link ExerciseSolution} with the given {@code solutionId}
     * if it exists, or empty otherwise.
     */
    Optional<ExerciseSolution> getSolution(final long solutionId);

    /**
     * Modifies the {@link ExerciseSolution} with the given {@code solutionId}.
     *
     * @param solutionId The id of the {@link ExerciseSolution} to be modified.
     * @param answer     The new answer for the {@link ExerciseSolution}.
     * @throws NoSuchEntityException       If there is no {@link ExerciseSolution} with the given {@code solutionId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise} of the solution
     *                                     is not in {@link Exam.State#IN_PROGRESS} state,
     *                                     or if the {@link ExamSolutionSubmission} to which the solution belongs to
     *                                     is already submitted.
     */
    void modifySolution(final long solutionId, final String answer)
            throws NoSuchEntityException, IllegalEntityStateException;
}
