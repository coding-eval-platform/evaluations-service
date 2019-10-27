package ar.edu.itba.cep.evaluations_service.services;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.executor.models.Language;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * A port into the application that allows {@link Exam} management.
 */
public interface ExamService {

    // ================================================================================================================
    // Exams
    // ================================================================================================================

    /**
     * Lists all {@link Exam}s in a paginated view.
     *
     * @param pagingRequest The {@link PagingRequest} containing paging data.
     * @return The requested {@link Page} of {@link Exam} (wrapped in {@link ExamWithoutOwners} instances).
     */
    Page<ExamWithoutOwners> listAllExams(final PagingRequest pagingRequest);

    /**
     * Lists all {@link Exam}s belonging to the currently authenticated user, in a paginated view.
     *
     * @param pagingRequest The {@link PagingRequest} containing paging data.
     * @return The requested {@link Page} of {@link Exam} (wrapped in {@link ExamWithoutOwners} instances).
     */
    Page<ExamWithoutOwners> listMyExams(final PagingRequest pagingRequest);

    /**
     * Finds the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} being requested.
     * @return An {@link Optional} containing the requested {@link Exam} if it exists
     * (wrapped in an {@link ExamWithOwners} instance), or empty otherwise.
     */
    Optional<ExamWithOwners> getExam(final long examId);

    /**
     * Finds the {@link Exam} with the given {@code examId}, wrapping the result in an {@link ExamWithScore} instance.
     *
     * @param examId The id of the {@link Exam} being requested.
     * @return An {@link Optional} containing the requested {@link Exam} if it exists
     * (wrapped in an {@link ExamWithScore} instance), or empty otherwise.
     */
    Optional<ExamWithScore> getExamWithScore(final long examId);

    /**
     * Creates an {@link Exam} with the given values.
     *
     * @param description The {@link Exam}'s description.
     * @param startingAt  The {@link LocalDateTime} at which the {@link Exam} starts.
     * @param duration    The {@link Exam}'s {@link Duration}.
     * @return The created {@link Exam}.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    Exam createExam(final String description, final LocalDateTime startingAt, final Duration duration)
            throws IllegalArgumentException;

    /**
     * Modifies the {@link Exam} with the given {@code examId}.
     *
     * @param examId      The id of the {@link Exam} to be modified.
     * @param description The new {@link Exam}'s description.
     * @param startingAt  The new {@link LocalDateTime} at which the {@link Exam} starts.
     * @param duration    The new {@link Exam}'s {@link Duration}.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If any argument is not valid.
     * @apiNote It cannot be executed if the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     */
    void modifyExam(final long examId, final String description,
                    final LocalDateTime startingAt, final Duration duration)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Starts the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} to be started.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam}'s state is not {@link Exam.State#UPCOMING}.
     * @apiNote It cannot be executed if the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     */
    void startExam(final long examId) throws NoSuchEntityException, IllegalEntityStateException;

    /**
     * Finishes the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} to be finished.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam}'s state is not {@link Exam.State#FINISHED}.
     * @apiNote It cannot be executed if the {@link Exam} is not in {@link Exam.State#IN_PROGRESS} state.
     */
    void finishExam(final long examId) throws NoSuchEntityException, IllegalEntityStateException;

    /**
     * Adds the given {@code owner} to the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} to which an owner will be added.
     * @param owner  The owner to be added.
     * @throws NoSuchEntityException    If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalArgumentException If the given {@code owner} is invalid.
     */
    void addOwnerToExam(final long examId, final String owner) throws NoSuchEntityException, IllegalArgumentException;

    /**
     * Removes the given {@code owner} from the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} to which an owner will be removed.
     * @param owner  The owner to be removed.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalEntityStateException If when executing this method the exam has only one owner.
     */
    void removeOwnerFromExam(final long examId, final String owner)
            throws NoSuchEntityException, IllegalEntityStateException;

    /**
     * Deletes the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} to be deleted.
     * @throws IllegalEntityStateException If the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     * @apiNote This method cascades the delete operation
     * (i.e it deletes all {@link Exercise}s belonging to the {@link Exam},
     * together with {@link TestCase}s belonging to the said {@link Exercise}s.).
     * It cannot be executed if the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     */
    void deleteExam(final long examId) throws IllegalEntityStateException;


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    /**
     * Lists all the {@link Exercise}s of a given {@code Exam}.
     *
     * @param examId The id of the {@link Exam} whose {@link Exercise}s are being requested.
     * @return A {@link List} containing the {@link Exercise}s of the {@link Exam} with the given {@code examId}.
     * @throws NoSuchEntityException If there is no {@link Exam} with the given {@code examId}.
     */
    List<Exercise> getExercises(final long examId) throws NoSuchEntityException;

    /**
     * Removes all {@link Exercise}s of the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} whose {@link Exercise}s are going to be removed.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     * @apiNote It cannot be executed if the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     */
    void clearExercises(final long examId) throws NoSuchEntityException, IllegalEntityStateException;

    /**
     * Creates an {@link Exercise} for the {@link Exam} with the given {@code examId}.
     *
     * @param examId           The id of the {@link Exam} to which an {@link Exercise} will be added.
     * @param question         The question of the {@link Exercise}.
     * @param language         The {@link Language} in which the answer must be written.
     * @param solutionTemplate The solution template.
     * @param awardedScore     The awarded score for the {@link Exercise}.
     * @return The created {@link Exercise}.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If any argument for the {@link Exercise} is not valid.
     * @apiNote It cannot be executed if the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     */
    Exercise createExercise(
            final long examId,
            final String question,
            final Language language,
            final String solutionTemplate,
            final int awardedScore) throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Finds the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} being requested.
     * @return An {@link Optional} containing the requested {@link Exercise} if it exists, or empty otherwise.
     */
    Optional<Exercise> getExercise(final long exerciseId);

    /**
     * Modifies the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId       The id of the {@link Exercise} to be modified.
     * @param question         The new question for the exercise.
     * @param language         The new language for the exercise.
     * @param solutionTemplate The new solution template for the exercise.
     * @param awardedScore     The new awarded score for the {@link Exercise}.
     * @throws NoSuchEntityException       If there is no {@link Exercise} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If any argument is not valid.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void modifyExercise(
            final long exerciseId,
            final String question,
            final Language language,
            final String solutionTemplate,
            final int awardedScore) throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Deletes the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} to be deleted.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @apiNote This method cascades the delete operation
     * (i.e it deletes all {@link TestCase}s belonging to the {@link Exercise}).
     * It cannot be executed if the {@link Exam} owning the {@link Exercise}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void deleteExercise(final long exerciseId) throws IllegalEntityStateException;


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    /**
     * Returns the public {@link TestCase}s of the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} whose public {@link TestCase}s are being requested.
     * @return A {@link List} containing the public {@link TestCase}s
     * of the {@link Exercise} with the given {@code exerciseId}.
     * @throws NoSuchEntityException If there is no {@link Exercise} with the given {@code exerciseId}.
     */
    List<TestCase> getPublicTestCases(final long exerciseId) throws NoSuchEntityException;

    /**
     * Returns the private {@link TestCase}s of the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} whose private {@link TestCase}s are being requested.
     * @return A {@link List} containing the private {@link TestCase}s
     * of the {@link Exercise} with the given {@code exerciseId}.
     * @throws NoSuchEntityException If there is no {@link Exercise} with the given {@code exerciseId}.
     */
    List<TestCase> getPrivateTestCases(final long exerciseId) throws NoSuchEntityException;

    /**
     * Creates a {@link TestCase} for the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId       The id of the {@link Exercise} to which a {@link TestCase} will be added.
     * @param visibility       The {@link TestCase.Visibility} of the {@link TestCase}.
     * @param timeout          The timeout of the {@link TestCase}.
     * @param programArguments The program arguments of the {@link TestCase}.
     * @param stdin            The stdin of the {@link TestCase}.
     * @param expectedOutputs  The expected outputs of the {@link TestCase}.
     * @return The created {@link TestCase}.
     * @throws NoSuchEntityException       If there is no {@link Exercise} with the given {@code exerciseId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If any argument is not valid.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    TestCase createTestCase(
            final long exerciseId,
            final TestCase.Visibility visibility,
            final Long timeout,
            final List<String> programArguments,
            final List<String> stdin,
            final List<String> expectedOutputs
    ) throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Finds the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} being requested.
     * @return An {@link Optional} containing the requested {@link TestCase} if it exists, or empty otherwise.
     */
    Optional<TestCase> getTestCase(final long testCaseId);

    /**
     * Modifies the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId       The id of the {@link TestCase} being modified.
     * @param visibility       The new {@link TestCase.Visibility} for the {@link TestCase}.
     * @param timeout          The new timeout for the {@link TestCase}.
     * @param programArguments The new program arguments {@link List} for the {@link TestCase}.
     * @param stdin            The new stdin {@link List} for the {@link TestCase}.
     * @param expectedOutputs  The new expected outputs {@link List} for the {@link TestCase}.
     * @throws NoSuchEntityException       If there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     that owns the {@link TestCase}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If the given {@code visibility} is not valid.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise} that owns the {@link TestCase}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void modifyTestCase(
            final long testCaseId,
            final TestCase.Visibility visibility,
            final Long timeout,
            final List<String> programArguments,
            final List<String> stdin,
            final List<String> expectedOutputs
    ) throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Deletes the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} to be deleted.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     that owns the {@link TestCase}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise} that owns the {@link TestCase}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void deleteTestCase(final long testCaseId) throws IllegalEntityStateException;
}
