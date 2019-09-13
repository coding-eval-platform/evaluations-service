package ar.edu.itba.cep.evaluations_service.services;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;

import java.util.List;

/**
 * A port into the application that allows {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
 * management.
 */
public interface ResultsService {

    /**
     * Returns the {@link ExerciseSolutionResult} for the {@link ExerciseSolution} with the given {@code solutionId}.
     *
     * @param solutionId The id of the {@link ExerciseSolution} to which the returned {@link ExerciseSolutionResult}s
     *                   belong to.
     * @return A {@link List} with the {@link ExerciseSolutionResult}s owned by the {@link ExerciseSolution} with
     * the given {@code solutionId}.
     * @throws IllegalEntityStateException If the
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission}
     *                                     to which the {@link ExerciseSolution} with the given {@code solutionId}
     *                                     belongs is not submitted yet.
     * @throws NoSuchEntityException       If there is no {@link ExerciseSolution} with the given {@code solutionId}.
     */
    List<ExerciseSolutionResult> getResultsForSolution(final long solutionId)
            throws NoSuchEntityException, IllegalEntityStateException;

    /**
     * Returns the {@link ExerciseSolutionResult} for the given {@code solutionId} and {@code testCaseId}.
     *
     * @param solutionId The id of the {@link ExerciseSolution} to which the returned {@link ExerciseSolutionResult}
     *                   belongs to.
     * @param testCaseId The id of the {@link ar.edu.itba.cep.evaluations_service.models.TestCase}
     *                   to which the returned {@link ExerciseSolutionResult} belongs to.
     * @return The corresponding {@link ExerciseSolutionResult}.
     * @throws IllegalEntityStateException If the
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission}
     *                                     to which the {@link ExerciseSolution} with the given {@code solutionId}
     *                                     belongs is not submitted yet.
     * @throws NoSuchEntityException       If there is no {@link ExerciseSolution}
     *                                     or {@link ar.edu.itba.cep.evaluations_service.models.TestCase}
     *                                     with the given {@code solutionId} or {@code testCaseId} respectively.
     * @apiNote Note that the returned entity will be created once the
     * {@link ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission} to which the {@link ExerciseSolution}
     * with the given {@code solutionId} belongs is submitted.
     */
    ExerciseSolutionResult getResultFor(final long solutionId, final long testCaseId)
            throws NoSuchEntityException, IllegalEntityStateException;

    /**
     * Sends to execute again the {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}
     * with the given {@code solutionId} (i.e with all the {@link ar.edu.itba.cep.evaluations_service.models.TestCase}s
     * belonging to the {@link ar.edu.itba.cep.evaluations_service.models.Exercise}s of the said solution.
     *
     * @param solutionId The {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}'s id.
     * @throws NoSuchEntityException       If there is no {@link ExerciseSolution} with the given {@code solutionId}.
     * @throws IllegalEntityStateException If there is an
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
     *                                     for the {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}
     *                                     that is not marked yet, or if the
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission}
     *                                     to which the {@link ExerciseSolution} with the given {@code solutionId}
     *                                     belongs is not submitted yet.
     */
    void retryForSolution(final long solutionId) throws NoSuchEntityException, IllegalEntityStateException;

    /**
     * Sends to execute again the {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution} with the given
     * {@code solutionId} using the {@link ar.edu.itba.cep.evaluations_service.models.TestCase} with the given
     * {@code testCaseId}.
     *
     * @param solutionId The {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}'s id.
     * @param testCaseId The {@link ar.edu.itba.cep.evaluations_service.models.TestCase}'s id.
     * @throws NoSuchEntityException       If there is no {@link ExerciseSolution}
     *                                     or {@link ar.edu.itba.cep.evaluations_service.models.TestCase}
     *                                     with the given {@code solutionId} or {@code testCaseId} respectively.
     * @throws IllegalEntityStateException If the
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
     *                                     corresponding to the given {@code solutionId} and {@code testCaseId}
     *                                     is not marked yet, or if the
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission}
     *                                     to which the {@link ExerciseSolution} with the given {@code solutionId}
     *                                     belongs is not submitted yet.
     */
    void retryForSolutionAndTestCase(final long solutionId, final long testCaseId)
            throws NoSuchEntityException, IllegalEntityStateException;
}
