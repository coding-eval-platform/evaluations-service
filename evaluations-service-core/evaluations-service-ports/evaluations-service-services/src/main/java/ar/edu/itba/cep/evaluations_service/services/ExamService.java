package ar.edu.itba.cep.evaluations_service.services;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import com.bellotapps.webapps_commons.persistence.repository_utils.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.PagingRequest;

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
     */
    Exam createExam(final String description, final LocalDateTime startingAt, final Duration duration);

    /**
     * Modifies the {@link Exam} with the given {@code examId}.
     *
     * @param examId      The id of the {@link Exam} to be modified.
     * @param description The new {@link Exam}'s description.
     * @param startingAt  The new {@link LocalDateTime} at which the {@link Exam} starts.
     * @param duration    The new {@link Exam}'s {@link Duration}.
     * @apiNote It cannot be executed once the {@link Exam} has started or finished.
     * The {@code startingAt} value cannot be modified if there is only one hour before starting the {@link Exam}.
     */
    void modifyExam(final long examId, final String description,
                    final LocalDateTime startingAt, final Duration duration);

    /**
     * Deletes the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} to be deleted.
     * @apiNote This method cascades the delete operation
     * (i.e it deletes all {@link Exercise}s belonging to the {@link Exam},
     * together with {@link TestCase}s belonging to the said {@link Exercise}s.).
     * Note that it cannot be executed once the {@link Exam} has started or finished.
     */
    void deleteExam(final long examId);

    /**
     * Lists all the {@link Exercise}s of a given {@code Exam}.
     *
     * @param examId The id of the {@link Exam} whose {@link Exercise}s are being requested.
     * @return A {@link List} containing the {@link Exercise}s of the {@link Exam} with the given {@code examId}.
     */
    List<Exercise> getExercises(final long examId);

    /**
     * Removes all {@link Exercise}s of the {@link Exam} with the given {@code examId}.
     *
     * @param examId The id of the {@link Exam} whose {@link Exercise}s are going to be removed.
     * @apiNote It cannot be executed once the {@link Exam} has started or finished.
     */
    void clearExercises(final long examId);


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    /**
     * Creates an {@link Exercise} for the {@link Exam} with the given {@code examId}.
     *
     * @param examId   The id of the {@link Exam} to which an {@link Exercise} will be added.
     * @param question The question of the {@link Exercise}.
     * @return The created {@link Exercise}.
     * @apiNote It cannot be executed once the {@link Exam} has started or finished.
     */
    Exercise createExercise(final long examId, final String question);

    /**
     * Changes the question to the {@link Exercise}'s with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} whose question will be changed.
     * @param question   The new question.
     * @apiNote It cannot be executed once the {@link Exam} owning the {@link Exercise}
     * with the given {@code exerciseId} has started or finished.
     */
    void changeExerciseQuestion(final long exerciseId, final String question);

    /**
     * Deletes the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} to be deleted.
     * @apiNote This method cascades the delete operation
     * (i.e it deletes all {@link TestCase}s belonging to the {@link Exercise}).
     * It cannot be executed once the {@link Exam} owning the {@link Exercise}
     * with the given {@code exerciseId} has started or finished.
     */
    void deleteExercise(final long exerciseId);

    /**
     * Returns the public {@link TestCase}s of the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} whose public {@link TestCase}s are being requested.
     * @return A {@link List} containing the public {@link TestCase}s
     * of the {@link Exercise} with the given {@code exerciseId}.
     */
    List<TestCase> getPublicTestCases(final long exerciseId);

    /**
     * Returns the private {@link TestCase}s of the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} whose private {@link TestCase}s are being requested.
     * @return A {@link List} containing the private {@link TestCase}s
     * of the {@link Exercise} with the given {@code exerciseId}.
     */
    List<TestCase> getPrivateTestCases(final long exerciseId);

    /**
     * Lists all {@link ExerciseSolution}s for the {@link Exercise} with the given {@code exerciseId},
     * in a paginated view.
     *
     * @param exerciseId    The The id of the {@link Exercise} whose {@link ExerciseSolution}s are being requested.
     * @param pagingRequest The {@link PagingRequest} containing paging data.
     * @return The requested {@link Page} of {@link ExerciseSolution}.
     */
    Page<ExerciseSolution> listSolutions(final long exerciseId, PagingRequest pagingRequest);


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
     * @apiNote It cannot be executed once the {@link Exam} owning the {@link Exercise}
     * with the given {@code exerciseId} has started or finished.
     */
    TestCase createTestCase(final long exerciseId, final TestCase.Visibility visibility,
                            final List<String> inputs, final List<String> expectedOutputs);

    /**
     * Changes the {@link TestCase.Visibility} to the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} whose {@code TestCase.Visibility} will be changed.
     * @param visibility The new {@link TestCase.Visibility} value.
     * @apiNote It cannot be executed once the {@link Exam} owning the {@link Exercise} that owns
     * the {@link TestCase} with the given {@code testCaseId} has started or finished.
     */
    void changeVisibility(final long testCaseId, final TestCase.Visibility visibility);

    /**
     * Changes the inputs {@link List} to the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} to which inputs will be replaced.
     * @param inputs     The {@link List} of inputs for the {@link TestCase}.
     * @apiNote It cannot be executed once the {@link Exam} owning the {@link Exercise} that owns
     * the {@link TestCase} with the given {@code testCaseId} has started or finished.
     */
    void changeInputs(final long testCaseId, final List<String> inputs);

    /**
     * Changes the expected outputs {@link List} to the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} to which expected outputs will be replaced.
     * @param outputs    The {@link List} of expected outputs for the {@link TestCase}.
     * @apiNote It cannot be executed once the {@link Exam} owning the {@link Exercise} that owns
     * the {@link TestCase} with the given {@code testCaseId} has started or finished.
     */
    void changeExpectedOutputs(final long testCaseId, final List<String> outputs);

    /**
     * Removes all the inputs for the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} to which inputs will be removed.
     * @apiNote It cannot be executed once the {@link Exam} owning the {@link Exercise} that owns
     * the {@link TestCase} with the given {@code testCaseId} has started or finished.
     */
    void clearInputs(final long testCaseId);

    /**
     * Removes all the expected outputs for the {@link TestCase} with the given {@code testCaseId}.
     *
     * @param testCaseId The id of the {@link TestCase} to which expected outputs will be removed.
     * @apiNote It cannot be executed once the {@link Exam} owning the {@link Exercise} that owns
     * the {@link TestCase} with the given {@code testCaseId} has started or finished.
     */
    void clearOutputs(final long testCaseId);


    // ================================================================================================================
    // Solutions
    // ================================================================================================================

    /**
     * Creates an {@link ExerciseSolution} for the {@link Exercise} with the given {@code exerciseId}.
     *
     * @param exerciseId The id of the {@link Exercise} for which an {@link ExerciseSolution} will be created.
     * @param answer     The answer to the question of the {@link Exercise}.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise}
     * with the given {@code exerciseId} has not started or has finished.
     */
    ExerciseSolution createExerciseSolution(final long exerciseId, final String answer);


    // ================================================================================================================
    // Solution Results
    // ================================================================================================================

    // TODO: Maybe move it to another interface?

    /**
     * Processes the execution of the {@link ExerciseSolution} with the given {@code solutionId}
     * when being evaluated with the {@link TestCase} with the given {@code testCaseId}.
     * Processes is performed by checking the execution's exit code,
     * the standard outputs and the standard error outputs.
     *
     * @param solutionId The id of the referenced {@link ExerciseSolution}.
     * @param testCaseId The id of the referenced {@link TestCase}.
     * @param exitCode   The execution's exit code.
     * @param stdOut     The standard output generated by the execution.
     * @param stdErr     The standard error output generated by the execution.
     * @apiNote It cannot be executed if the {@link Exam} owning the {@link Exercise}
     * with the given {@code exerciseId} has not started
     */
    void processExecution(final long solutionId, final long testCaseId,
                          final int exitCode, final List<String> stdOut, final List<String> stdErr);
}
