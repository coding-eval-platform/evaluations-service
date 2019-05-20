package ar.edu.itba.cep.evaluations_service.services;

import ar.edu.itba.cep.evaluations_service.models.*;
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
     * @return The requested {@link Page} of {@link Exam}.
     */
    Page<Exam> listExams(final PagingRequest pagingRequest);

    /**
     * Finds the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} being requested.
     * @return An {@link Optional} containing the requested {@link Exam} if it exists, or empty otherwise.
     */
    Optional<Exam> getExam(final long examId);

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
     * @param examId The id The id of the {@link Exam} to be started.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam}'s state is not {@link Exam.State#UPCOMING}.
     * @apiNote It cannot be executed if the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     */
    void startExam(final long examId) throws NoSuchEntityException, IllegalEntityStateException;

    /**
     * Finishes the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id The id of the {@link Exam} to be finished.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam}'s state is not {@link Exam.State#FINISHED}.
     * @apiNote It cannot be executed if the {@link Exam} is not in {@link Exam.State#IN_PROGRESS} state.
     */
    void finishExam(final long examId) throws NoSuchEntityException, IllegalEntityStateException;

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


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    /**
     * Creates an {@link Exercise} for the {@link Exam} with the given {@code examId}.
     *
     * @param examId           The id of the {@link Exam} to which an {@link Exercise} will be added.
     * @param question         The question of the {@link Exercise}.
     * @param language         The {@link Language} in which the answer must be written.
     * @param solutionTemplate The solution template.
     * @return The created {@link Exercise}.
     * @throws NoSuchEntityException       If there is no {@link Exam} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If any argument for the {@link Exercise} is not valid.
     * @apiNote It cannot be executed if the {@link Exam} is not in {@link Exam.State#UPCOMING} state.
     */
    Exercise createExercise(final long examId,
                            final String question, final Language language, final String solutionTemplate)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Modifies the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId       The id of the {@link Exam} to be modified.
     * @param question         The new question for the exercise.
     * @param language         The new language for the exercise.
     * @param solutionTemplate The new solution template for the exercise.
     * @throws NoSuchEntityException       If there is no {@link Exercise} with the given {@code examId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If any argument is not valid.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void modifyExercise(final long exerciseId,
                        final String question, final Language language, final String solutionTemplate)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;


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
     * Lists all {@link ExerciseSolution}s for the {@link Exercise} with the given {@code exerciseId},
     * in a paginated view.
     *
     * @param exerciseId    The The id of the {@link Exercise} whose {@link ExerciseSolution}s are being requested.
     * @param pagingRequest The {@link PagingRequest} containing paging data.
     * @return The requested {@link Page} of {@link ExerciseSolution}.
     * @throws NoSuchEntityException If there is no {@link Exercise} with the given {@code exerciseId}.
     */
    Page<ExerciseSolution> listSolutions(final long exerciseId, PagingRequest pagingRequest)
            throws NoSuchEntityException;


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    /**
     * Creates a {@link TestCase} for the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId      The id of the {@link Exercise} to which a {@link TestCase} will be added.
     * @param visibility      The {@link TestCase.Visibility} of the {@link TestCase}.
     * @param inputs          The inputs of the {@link TestCase}.
     * @param expectedOutputs The expected outputs of the {@link TestCase}.
     * @return The created {@link TestCase}.
     * @throws NoSuchEntityException       If there is no {@link Exercise} with the given {@code exerciseId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If any argument is not valid.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    TestCase createTestCase(final long exerciseId, final TestCase.Visibility visibility,
                            final List<String> inputs, final List<String> expectedOutputs)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Changes the {@link TestCase.Visibility} to the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} whose {@code TestCase.Visibility} will be changed.
     * @param visibility The new {@link TestCase.Visibility} value.
     * @throws NoSuchEntityException       If there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     that owns the {@link TestCase}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If the given {@code visibility} is not valid.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise} that owns the {@link TestCase}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void changeVisibility(final long testCaseId, final TestCase.Visibility visibility)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Changes the inputs {@link List} to the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} to which inputs will be replaced.
     * @param inputs     The {@link List} of inputs for the {@link TestCase}.
     * @throws NoSuchEntityException       If there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     that owns the {@link TestCase}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If the given {@code inputs} {@link List} is not valid.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise} that owns the {@link TestCase}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void changeInputs(final long testCaseId, final List<String> inputs)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Changes the expected outputs {@link List} to the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} to which expected outputs will be replaced.
     * @param outputs    The {@link List} of expected outputs for the {@link TestCase}.
     * @throws NoSuchEntityException       If there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     that owns the {@link TestCase}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @throws IllegalArgumentException    If the given {@code outputs} {@link List} is not valid.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise} that owns the {@link TestCase}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void changeExpectedOutputs(final long testCaseId, final List<String> outputs)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;

    /**
     * Removes all the inputs for the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} to which inputs will be removed.
     * @throws NoSuchEntityException       If there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     that owns the {@link TestCase}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise} that owns the {@link TestCase}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void clearInputs(final long testCaseId) throws NoSuchEntityException, IllegalEntityStateException;

    /**
     * Removes all the expected outputs for the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} to which expected outputs will be removed.
     * @throws NoSuchEntityException       If there is no {@link TestCase} with the given {@code testCaseId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     that owns the {@link TestCase}
     *                                     is not in {@link Exam.State#UPCOMING} state.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise} that owns the {@link TestCase}
     * is not in {@link Exam.State#UPCOMING} state.
     */
    void clearOutputs(final long testCaseId) throws NoSuchEntityException, IllegalEntityStateException;

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


    // ================================================================================================================
    // Solutions
    // ================================================================================================================

    /**
     * Creates an {@link ExerciseSolution} for the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} for which an {@link ExerciseSolution} will be created.
     * @param answer     The answer to the question of the {@link Exercise}.
     * @throws NoSuchEntityException       If there is no {@link Exercise} with the given {@code exerciseId}.
     * @throws IllegalEntityStateException If the {@link Exam} owning the {@link Exercise}
     *                                     is not in {@link Exam.State#IN_PROGRESS} state.
     * @throws IllegalArgumentException    If any argument is not valid.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise}
     * is not in {@link Exam.State#IN_PROGRESS} state.
     */
    ExerciseSolution createExerciseSolution(final long exerciseId, final String answer)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException;
}
