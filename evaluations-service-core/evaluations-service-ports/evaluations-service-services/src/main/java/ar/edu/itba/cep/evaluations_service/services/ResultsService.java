package ar.edu.itba.cep.evaluations_service.services;

import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;

/**
 * A port into the application that allows {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
 * management.
 */
public interface ResultsService {

    /**
     * Sends to execute again all the {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}s belonging
     * to the {@link ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission}
     * with the given {@code submissionId}
     * (i.e with all the {@link ar.edu.itba.cep.evaluations_service.models.TestCase}s belonging to the
     * {@link ar.edu.itba.cep.evaluations_service.models.Exercise}s of the
     * {@link ar.edu.itba.cep.evaluations_service.models.Exam} of the said submission).
     *
     * @param submissionId The {@link ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission}'s id.
     * @throws IllegalEntityStateException If there is an
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
     *                                     for an {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}
     *                                     of the
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission}
     *                                     that is not marked yet.
     */
    void retryForSubmission(final long submissionId) throws IllegalEntityStateException;

    /**
     * Sends to execute again the {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}
     * with the given {@code solutionId} (i.e with all the {@link ar.edu.itba.cep.evaluations_service.models.TestCase}s
     * belonging to the {@link ar.edu.itba.cep.evaluations_service.models.Exercise}s of the said solution.
     *
     * @param solutionId The {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}'s id.
     * @throws IllegalEntityStateException If there is an
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
     *                                     for the {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}
     *                                     that is not marked yet.
     */
    void retryForSolution(final long solutionId) throws IllegalEntityStateException;

    ;

    /**
     * Sends to execute again the {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution} with the given
     * {@code solutionId} using the {@link ar.edu.itba.cep.evaluations_service.models.TestCase} with the given
     * {@code testCaseId}.
     *
     * @param solutionId The {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution}'s id.
     * @param testCaseId The {@link ar.edu.itba.cep.evaluations_service.models.TestCase}'s id.
     * @throws IllegalEntityStateException If the
     *                                     {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult}
     *                                     corresponding to the given {@code solutionId} and {@code testCaseId}
     *                                     is not marked yet.
     */
    void retryForSolutionAndTestCase(final long solutionId, final long testCaseId) throws IllegalEntityStateException;
}
