package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.*;
import ar.edu.itba.cep.evaluations_service.models.*;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * Manager for {@link Exam}s.
 */
@Service
@Transactional(readOnly = true)
public class ExamManager implements ExamService, ExecutionResultProcessor {

    /**
     * Repository for {@link Exam}s.
     */
    private final ExamRepository examRepository;
    /**
     * Repository for {@link Exercise}s.
     */
    private final ExerciseRepository exerciseRepository;
    /**
     * Repository for {@link TestCase}s.
     */
    private final TestCaseRepository testCaseRepository;
    /**
     * Repository for {@link ExerciseSolution}s.
     */
    private final ExerciseSolutionRepository exerciseSolutionRepository;
    /**
     * Repository for {@link ExerciseSolutionResult}s.
     */
    private final ExerciseSolutionResultRepository exerciseSolutionResultRepository;
    /**
     * A proxy for the executor service.
     */
    private final ExecutorServiceCommandMessageProxy executorService;


    /**
     * Constructor.
     *
     * @param examRepository                   Repository for {@link Exam}s.
     * @param exerciseRepository               Repository for {@link Exercise}s.
     * @param testCaseRepository               Repository for {@link TestCase}s.
     * @param exerciseSolutionRepository       Repository for {@link ExerciseSolution}s.
     * @param exerciseSolutionResultRepository Repository for {@link ExerciseSolutionResult}s.
     * @param executorService                  A proxy for the executor service.
     */
    @Autowired
    public ExamManager(final ExamRepository examRepository,
                       final ExerciseRepository exerciseRepository,
                       final TestCaseRepository testCaseRepository,
                       final ExerciseSolutionRepository exerciseSolutionRepository,
                       final ExerciseSolutionResultRepository exerciseSolutionResultRepository,
                       final ExecutorServiceCommandMessageProxy executorService) {
        this.examRepository = examRepository;
        this.exerciseRepository = exerciseRepository;
        this.testCaseRepository = testCaseRepository;
        this.exerciseSolutionRepository = exerciseSolutionRepository;
        this.exerciseSolutionResultRepository = exerciseSolutionResultRepository;
        this.executorService = executorService;
    }


    // ================================================================================================================
    // Exams
    // ================================================================================================================

    @Override
    public Page<Exam> listExams(final PagingRequest pagingRequest) {
        return examRepository.findAll(pagingRequest);
    }

    @Override
    public Optional<Exam> getExam(final long examId) {
        return examRepository.findById(examId);
    }

    @Override
    @Transactional
    public Exam createExam(final String description, final LocalDateTime startingAt, final Duration duration)
            throws IllegalArgumentException {
        final var exam = new Exam(description, startingAt, duration);
        return examRepository.save(exam);
    }

    @Override
    @Transactional
    public void modifyExam(final long examId,
                           final String description, final LocalDateTime startingAt, final Duration duration)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {
        final var exam = loadExam(examId);
        exam.update(description, startingAt, duration); // The Exam verifies state by its own.
        examRepository.save(exam);
    }

    @Override
    @Transactional
    public void startExam(final long examId) throws NoSuchEntityException, IllegalEntityStateException {
        final var exam = loadExam(examId);
        exam.startExam(); // The Exam verifies state by its own.
        examRepository.save(exam);
    }

    @Override
    @Transactional
    public void finishExam(final long examId) throws NoSuchEntityException, IllegalEntityStateException {
        final var exam = loadExam(examId);
        exam.finishExam(); // The Exam verifies state by its own.
        examRepository.save(exam);
    }

    @Override
    @Transactional
    public void deleteExam(final long examId) throws IllegalEntityStateException {
        examRepository.findById(examId)
                .ifPresent(exam -> {
                    performExamUpcomingStateVerification(exam);
                    testCaseRepository.deleteExamTestCases(exam);
                    exerciseRepository.deleteExamExercises(exam);
                    examRepository.delete(exam);
                });
    }

    @Override
    public List<Exercise> getExercises(final long examId) throws NoSuchEntityException {
        final var exam = loadExam(examId);
        return exerciseRepository.getExamExercises(exam);
    }

    @Override
    @Transactional
    public void clearExercises(final long examId) throws NoSuchEntityException, IllegalEntityStateException {
        final var exam = loadExam(examId);
        performExamUpcomingStateVerification(exam);
        testCaseRepository.deleteExamTestCases(exam);
        exerciseRepository.deleteExamExercises(exam);
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    @Override
    @Transactional
    public Exercise createExercise(final long examId,
                                   final String question, final Language language, final String solutionTemplate)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {
        final var exam = loadExam(examId);
        performExamUpcomingStateVerification(exam);
        final var exercise = new Exercise(question, language, solutionTemplate, exam);
        return exerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    public void modifyExercise(final long exerciseId,
                               final String question, final Language language, final String solutionTemplate)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {
        final var exercise = loadExercise(exerciseId);
        performExamUpcomingStateVerification(exercise.getExam());
        exercise.update(question, language, solutionTemplate);
        exerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    public void deleteExercise(final long exerciseId) throws IllegalEntityStateException {
        exerciseRepository.findById(exerciseId)
                .ifPresent(exercise -> {
                    performExamUpcomingStateVerification(exercise.getExam());
                    testCaseRepository.deleteExerciseTestCases(exercise);
                    exerciseRepository.delete(exercise);
                });
    }

    @Override
    public List<TestCase> getPublicTestCases(final long exerciseId) throws NoSuchEntityException {
        final var exercise = loadExercise(exerciseId);
        return testCaseRepository.getExercisePublicTestCases(exercise);
    }

    @Override
    public List<TestCase> getPrivateTestCases(final long exerciseId) throws NoSuchEntityException {
        final var exercise = loadExercise(exerciseId);
        return testCaseRepository.getExercisePrivateTestCases(exercise);
    }

    @Override
    public Page<ExerciseSolution> listSolutions(final long exerciseId, final PagingRequest pagingRequest)
            throws NoSuchEntityException {
        final var exercise = loadExercise(exerciseId);
        return exerciseSolutionRepository.getExerciseSolutions(exercise, pagingRequest);

    }


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    @Override
    @Transactional
    public TestCase createTestCase(
            final long exerciseId,
            final TestCase.Visibility visibility,
            final Long timeout,
            final List<String> inputs,
            final List<String> expectedOutputs)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {
        final var exercise = loadExercise(exerciseId);
        performExamUpcomingStateVerification(exercise.getExam());
        return testCaseRepository.save(new TestCase(visibility, timeout, inputs, expectedOutputs, exercise));
    }

    @Override
    @Transactional
    public void modifyTestCase(final long testCaseId,
                               final TestCase.Visibility visibility,
                               final Long timeout,
                               final List<String> inputs,
                               final List<String> expectedOutputs)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {
        final var testCase = loadTestCase(testCaseId);
        performExamUpcomingStateVerification(testCase.getExercise().getExam());
        testCase.update(visibility, timeout, inputs, expectedOutputs);
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void deleteTestCase(final long testCaseId) throws IllegalEntityStateException {
        testCaseRepository.findById(testCaseId)
                .ifPresent(testCase -> {
                    performExamUpcomingStateVerification(testCase.getExercise().getExam());
                    testCaseRepository.delete(testCase);
                });
    }


    // ================================================================================================================
    // Solutions
    // ================================================================================================================

    @Override
    @Transactional
    public ExerciseSolution createExerciseSolution(final long exerciseId, final String answer)
            throws NoSuchEntityException, IllegalEntityStateException, IllegalArgumentException {
        final var exercise = loadExercise(exerciseId);
        // Verify that the exam is in progress in order to create solutions for exercises owned by it.
        if (exercise.getExam().getState() != Exam.State.IN_PROGRESS) {
            throw new IllegalEntityStateException(EXAM_IS_NOT_IN_PROGRESS);
        }
        final var solution = exerciseSolutionRepository.save(new ExerciseSolution(exercise, answer));
        final var privateTestCases = testCaseRepository.getExercisePrivateTestCases(exercise);
        final var publicTestCases = testCaseRepository.getExercisePublicTestCases(exercise);
        Stream.concat(
                privateTestCases.stream(),
                publicTestCases.stream()
        ).forEach(testCase -> sendToRun(solution, testCase));

        return solution;
        // TODO: when authoring becomes available, check that the student did not send a solution already.
    }


    // ================================================================================================================
    // Solution Results
    // ================================================================================================================

    @Override
    @Transactional
    public void processExecution(final long solutionId, final long testCaseId, final ExecutionResult executionResult)
            throws NoSuchEntityException, IllegalArgumentException {
        // First, validate arguments
        Assert.notNull(executionResult, "The execution result must not be null");

        // Load solution and test case (checking if they exist)
        final var solution = loadSolution(solutionId);
        final var testCase = loadTestCase(testCaseId);

        // State validation is not needed because the existence of a solution proves state validity

        // Get the ExerciseSolutionResultCreator corresponding to the given executionResult,
        // and then create the ExerciseSolutionResult to be saved.
        // If the ExecutionResult is an InitializationErrorExecutionResult or an UnknownErrorExecutionResult,
        // the returned Optional will be empty and nothing will happen.
        getResultCreator(executionResult)
                .map(creator -> creator.apply(solution, testCase))
                .ifPresent(exerciseSolutionResultRepository::save)
        ;
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Loads the {@link Exam} with the given {@code id} if it exists.
     *
     * @param id The {@link Exam}'s id.
     * @return The {@link Exam} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link Exam} with the given {@code id}.
     */
    private Exam loadExam(final long id) throws NoSuchEntityException {
        return loadEntity(examRepository::findById, id);
    }

    /**
     * Loads the {@link Exercise} with the given {@code id} if it exists.
     *
     * @param id The {@link Exercise}'s id.
     * @return The {@link Exercise} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link Exercise} with the given {@code id}.
     */
    private Exercise loadExercise(final long id) throws NoSuchEntityException {
        return loadEntity(exerciseRepository::findById, id);
    }

    /**
     * Loads the {@link TestCase} with the given {@code id} if it exists.
     *
     * @param id The {@link TestCase}'s id.
     * @return The {@link TestCase} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link TestCase} with the given {@code id}.
     */
    private TestCase loadTestCase(final long id) throws NoSuchEntityException {
        return loadEntity(testCaseRepository::findById, id);
    }

    /**
     * Loads the {@link ExerciseSolution} with the given {@code id} if it exists.
     *
     * @param id The {@link ExerciseSolution}'s id.
     * @return The {@link ExerciseSolution} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link ExerciseSolution} with the given {@code id}.
     */
    private ExerciseSolution loadSolution(final long id) throws NoSuchEntityException {
        return loadEntity(exerciseSolutionRepository::findById, id);
    }

    /**
     * Loads the entity if type {@code T} with the given {@code id},
     * retrieving it with the given {@code entityRetriever}.
     *
     * @param entityRetriever A {@link Function} that given an {@code id} of type {@code ID}
     *                        retrieves an {@link Optional} of the entity to be loaded.
     * @param id              The id of the entity.
     * @param <T>             The concrete type of the entity.
     * @param <ID>            The concrete type of the entity's id.
     * @return The entity of type {@code T} with the given {@code id}.
     * @throws NoSuchEntityException IF there is no entity of type {@code T} with the given {@code id}.
     */
    private static <T, ID> T loadEntity(final Function<ID, Optional<T>> entityRetriever, final ID id)
            throws NoSuchEntityException {
        return entityRetriever.apply(id).orElseThrow(NoSuchEntityException::new);
    }

    /**
     * Sends to run the given {@code solution}, using the given {@code testCase}.
     *
     * @param solution The {@link ExerciseSolution} to be sent to run.
     * @param testCase The {@link TestCase} with the inputs to be used as running arguments.
     */
    private void sendToRun(final ExerciseSolution solution, final TestCase testCase) {
        final var request = new ExecutionRequest(
                solution.getAnswer(),
                testCase.getInputs(),
                testCase.getTimeout(),
                solution.getExercise().getLanguage());
        final var replyData = new ExecutionResultReplyData(solution.getId(), testCase.getId());
        executorService.requestExecution(request, replyData);
    }

    /**
     * Performs the {@link Exam} state verification (i.e checks if the given {@code exam} can be modified,
     * throwing an {@link IllegalEntityStateError} if its state is not upcoming).
     *
     * @param exam The {@link Exam} to be checked.
     * @throws IllegalEntityStateException If the given {@link Exam}'s state is not {@link Exam.State#UPCOMING}.
     */
    private static void performExamUpcomingStateVerification(final Exam exam) throws IllegalEntityStateException {
        Assert.notNull(exam, "The exam to be checked must not be null");
        if (exam.getState() != Exam.State.UPCOMING) {
            throw new IllegalEntityStateException(EXAM_IS_NOT_UPCOMING);
        }
    }

    /**
     * Gets the {@link ExerciseSolutionResultCreator} corresponding to the given {@link ExecutionResult}.
     *
     * @param result The {@link ExecutionResult} to be analyzed in order to know which
     *               {@link ExerciseSolutionResultCreator} must be returned.
     * @return The {@link ExerciseSolutionResultCreator} corresponding to the given {@link ExecutionResult}.
     */
    private static Optional<ExerciseSolutionResultCreator> getResultCreator(final ExecutionResult result) {
        if (result instanceof FinishedExecutionResult) {
            return Optional.of((s, t) -> createForFinished(s, t, (FinishedExecutionResult) result));
        }
        if (result instanceof TimedOutExecutionResult) {
            return Optional.of((s, t) -> new ExerciseSolutionResult(s, t, ExerciseSolutionResult.Result.TIMED_OUT));
        }
        if (result instanceof CompileErrorExecutionResult) {
            return Optional.of((s, t) -> new ExerciseSolutionResult(s, t, ExerciseSolutionResult.Result.NOT_COMPILED));
        }
        if (result instanceof InitializationErrorExecutionResult || result instanceof UnknownErrorExecutionResult) {
            return Optional.empty(); // TODO: maybe make a solution result for this special cases? retry?
        }
        throw new IllegalArgumentException("Unknown subtype");
    }

    /**
     * Creates an {@link ExerciseSolutionResult} for a {@link FinishedExecutionResult}.
     *
     * @param solution        The {@link ExerciseSolution} corresponding to the created {@link ExerciseSolutionResult}.
     * @param testCase        The {@link TestCase} corresponding to the created {@link ExerciseSolutionResult}.
     * @param executionResult The {@link FinishedExecutionResult} from where execution stuff is taken.
     * @return The created {@link ExerciseSolutionResult} for a {@link FinishedExecutionResult}.
     */
    private static ExerciseSolutionResult createForFinished(
            final ExerciseSolution solution,
            final TestCase testCase,
            final FinishedExecutionResult executionResult) {

        if (executionResult.getExitCode() == 0
                && executionResult.getStderr().isEmpty()
                && testCase.getExpectedOutputs().equals(executionResult.getStdout())) {
            return new ExerciseSolutionResult(solution, testCase, ExerciseSolutionResult.Result.APPROVED);
        }
        return new ExerciseSolutionResult(solution, testCase, ExerciseSolutionResult.Result.FAILED);
    }

    /**
     * Defines behaviour for an object that can create an {@link ExerciseSolutionResult}
     * from an {@link ExerciseSolution} and a {@link TestCase}.
     */
    private interface ExerciseSolutionResultCreator
            extends BiFunction<ExerciseSolution, TestCase, ExerciseSolutionResult> {
    }

    /**
     * An {@link IllegalEntityStateError} that indicates that a certain action that involves an {@link Exam}
     * cannot be performed because the said {@link Exam}'s state is not upcoming (it has started or finished already).
     */
    private final static IllegalEntityStateError EXAM_IS_NOT_UPCOMING =
            new IllegalEntityStateError("The exam is not in upcoming state", "state");

    /**
     * An {@link IllegalEntityStateError} that indicates that a certain action that involves an {@link Exam}
     * cannot be performed because the said {@link Exam}'s state is not in progress
     * (it has not started yet or has finished already).
     */
    private final static IllegalEntityStateError EXAM_IS_NOT_IN_PROGRESS =
            new IllegalEntityStateError("The exam is not in progress state", "state");
}
