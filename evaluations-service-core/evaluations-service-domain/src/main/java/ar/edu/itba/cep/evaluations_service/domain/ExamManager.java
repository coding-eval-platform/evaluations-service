package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.*;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.persistence.repository_utils.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.PagingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Manager for {@link Exam}s.
 */
@Service
public class ExamManager implements ExamService {

    /**
     * Indicates the amount of hours before an {@link Exam} starts in which it cannot be modified its startingAt value.
     */
    private final static long HOURS_BEFORE = 1;

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
     * Constructor.
     *
     * @param examRepository                   Repository for {@link Exam}s.
     * @param exerciseRepository               Repository for {@link Exercise}s.
     * @param testCaseRepository               Repository for {@link TestCase}s.
     * @param exerciseSolutionRepository       Repository for {@link ExerciseSolution}s.
     * @param exerciseSolutionResultRepository Repository for {@link ExerciseSolutionResult}s.
     */
    @Autowired
    public ExamManager(final ExamRepository examRepository,
                       final ExerciseRepository exerciseRepository,
                       final TestCaseRepository testCaseRepository,
                       final ExerciseSolutionRepository exerciseSolutionRepository,
                       final ExerciseSolutionResultRepository exerciseSolutionResultRepository) {
        this.examRepository = examRepository;
        this.exerciseRepository = exerciseRepository;
        this.testCaseRepository = testCaseRepository;
        this.exerciseSolutionRepository = exerciseSolutionRepository;
        this.exerciseSolutionResultRepository = exerciseSolutionResultRepository;
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
    public Exam createExam(final String description, final LocalDateTime startingAt, final Duration duration) {
        final var exam = new Exam(description, startingAt, duration);
        return examRepository.save(exam);
    }

    @Override
    public void modifyExam(final long examId,
                           final String description, final LocalDateTime startingAt, final Duration duration) {
        final var exam = loadExam(examId);
        // First, verify if the exam can be modified
        if (cannotBeModified(exam)) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        // Then verify if the startingAt value can be modified (only if it is different from the actual value)
        final var actualStartingAt = exam.getStartingAt();
        if (!actualStartingAt.equals(startingAt)
                && LocalDateTime.now().isAfter(actualStartingAt.minusHours(HOURS_BEFORE))) {
            throw new IllegalEntityStateException(HOURS_BEFORE_FOR_STARTING_AT);
        }

        // Up to here we know that the exam can be modified
        exam.update(description, startingAt, duration);
        examRepository.save(exam);
    }

    @Override
    public void deleteExam(final long examId) {
        examRepository.findById(examId)
                .ifPresent(exam -> {
                    if (cannotBeModified(exam)) {
                        throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
                    }
                    testCaseRepository.deleteExamTestCases(exam);
                    exerciseRepository.deleteExamExercises(exam);
                    examRepository.delete(exam);
                });
    }

    @Override
    public List<Exercise> getExercises(final long examId) {
        final var exam = loadExam(examId);
        return exerciseRepository.getExamExercises(exam);
    }

    @Override
    public void clearExercises(final long examId) {
        final var exam = loadExam(examId);
        if (cannotBeModified(exam)) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        exerciseRepository.deleteExamExercises(exam);
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    @Override
    public Exercise createExercise(final long examId, final String question) {
        final var exam = loadExam(examId);
        if (cannotBeModified(exam)) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        final var exercise = new Exercise(question, exam);
        return exerciseRepository.save(exercise);
    }

    @Override
    public void changeExerciseQuestion(final long exerciseId, final String question) {
        final var exercise = loadExercise(exerciseId);
        if (cannotBeModified(exercise.belongsToExam())) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        exercise.setQuestion(question);
        exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExercise(final long exerciseId) {
        exerciseRepository.findById(exerciseId)
                .ifPresent(exercise -> {
                    if (cannotBeModified(exercise.belongsToExam())) {
                        throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
                    }
                    testCaseRepository.deleteExerciseTestCases(exercise);
                    exerciseRepository.delete(exercise);
                });

    }

    @Override
    public List<TestCase> getPublicTestCases(final long exerciseId) {
        final var exercise = loadExercise(exerciseId);
        return testCaseRepository.getExercisePublicTestCases(exercise);
    }

    @Override
    public List<TestCase> getPrivateTestCases(final long exerciseId) {
        final var exercise = loadExercise(exerciseId);
        return testCaseRepository.getExercisePrivateTestCases(exercise);
    }

    @Override
    public Page<ExerciseSolution> listSolutions(final long exerciseId, final PagingRequest pagingRequest) {
        final var exercise = loadExercise(exerciseId);
        return exerciseSolutionRepository.getExerciseSolutions(exercise, pagingRequest);
    }


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    @Override
    public TestCase createTestCase(final long exerciseId,
                                   final TestCase.Visibility visibility,
                                   final List<String> inputs, final List<String> expectedOutputs) {
        final var exercise = loadExercise(exerciseId);
        if (cannotBeModified(exercise.belongsToExam())) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        final var testCase = new TestCase(visibility, exercise);
        testCase.setInputs(inputs);
        testCase.setExpectedInputs(expectedOutputs);
        return testCaseRepository.save(testCase);
    }

    @Override
    public void changeVisibility(final long testCaseId, final TestCase.Visibility visibility) {
        final var testCase = loadTestCase(testCaseId);
        if (cannotBeModified(testCase.belongsToExercise().belongsToExam())) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        testCase.setVisibility(visibility);
        testCaseRepository.save(testCase);
    }

    @Override
    public void changeInputs(final long testCaseId, final List<String> inputs) {
        final var testCase = loadTestCase(testCaseId);
        if (cannotBeModified(testCase.belongsToExercise().belongsToExam())) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        testCase.setInputs(inputs);
        testCaseRepository.save(testCase);
    }

    @Override
    public void changeExpectedOutputs(final long testCaseId, final List<String> outputs) {
        final var testCase = loadTestCase(testCaseId);
        if (cannotBeModified(testCase.belongsToExercise().belongsToExam())) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        testCase.setExpectedInputs(outputs);
        testCaseRepository.save(testCase);
    }

    @Override
    public void clearInputs(final long testCaseId) {
        final var testCase = loadTestCase(testCaseId);
        if (cannotBeModified(testCase.belongsToExercise().belongsToExam())) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        testCase.removeAllInputs();
        testCaseRepository.save(testCase);
    }

    @Override
    public void clearOutputs(final long testCaseId) {
        final var testCase = loadTestCase(testCaseId);
        if (cannotBeModified(testCase.belongsToExercise().belongsToExam())) {
            throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
        }
        testCase.removeAllExpectedOutputs();
        testCaseRepository.save(testCase);
    }

    @Override
    public void deleteTestCase(final long testCaseId) {
        testCaseRepository.findById(testCaseId)
                .ifPresent(testCase -> {
                    if (cannotBeModified(testCase.belongsToExercise().belongsToExam())) {
                        throw new IllegalEntityStateException(EXAM_ALREADY_STARTED);
                    }
                    testCaseRepository.delete(testCase);
                });
    }

    // ================================================================================================================
    // Solutions
    // ================================================================================================================

    @Override
    public ExerciseSolution createExerciseSolution(final long exerciseId, final String answer) {
        final var exercise = loadExercise(exerciseId);
        final var exam = exercise.belongsToExam();
        final var startingAt = exam.getStartingAt();
        final var finishingAt = startingAt.plus(exam.getDuration());
        final var now = LocalDateTime.now();
        if (now.isBefore(startingAt)) {
            throw new IllegalEntityStateException(EXAM_HAS_NOT_STARTED);
        }
        if (now.isAfter(finishingAt)) {
            throw new IllegalEntityStateException(EXAM_HAS_FINISHED);
        }
        final var solution = new ExerciseSolution(exercise, answer);
        return exerciseSolutionRepository.save(solution);

        // TODO: send code to run!
        // TODO: when authoring becomes available, check that the student did not send a solution already.
    }


    // ================================================================================================================
    // Solution Results
    // ================================================================================================================

    @Override
    public void processExecution(final long solutionId, final long testCaseId,
                                 final int exitCode, final List<String> stdOut, final List<String> stdErr) {
        // First, validate arguments
        Assert.notNull(stdOut, "The stdout list cannot be null");
        Assert.notNull(stdErr, "The stderr list cannot be null");

        // Load solution and test case (checking if they exist)
        final var solution = loadSolution(solutionId);
        final var testCase = loadTestCase(testCaseId);

        // Check if exit code is zero, if there is no error output and if outputs match the expected outputs
        final var result = exitCode == 0 && stdErr.isEmpty() && testCase.getExpectedOutputs().equals(stdOut) ?
                ExerciseSolutionResult.Result.APPROVED :
                ExerciseSolutionResult.Result.FAILED;

        // Execution processing is finished. Now the result can be saved.
        final var solutionResult = new ExerciseSolutionResult(solution, testCase, result);
        exerciseSolutionResultRepository.save(solutionResult);
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
        return examRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }

    /**
     * Loads the {@link Exercise} with the given {@code id} if it exists.
     *
     * @param id The {@link Exercise}'s id.
     * @return The {@link Exercise} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link Exercise} with the given {@code id}.
     */
    private Exercise loadExercise(final long id) throws NoSuchEntityException {
        return exerciseRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }

    /**
     * Loads the {@link TestCase} with the given {@code id} if it exists.
     *
     * @param id The {@link TestCase}'s id.
     * @return The {@link TestCase} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link TestCase} with the given {@code id}.
     */
    private TestCase loadTestCase(final long id) throws NoSuchEntityException {
        return testCaseRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }

    /**
     * Loads the {@link ExerciseSolution} with the given {@code id} if it exists.
     *
     * @param id The {@link ExerciseSolution}'s id.
     * @return The {@link ExerciseSolution} with the given {@code id}.
     * @throws NoSuchEntityException If there is no {@link ExerciseSolution} with the given {@code id}.
     */
    private ExerciseSolution loadSolution(final long id) throws NoSuchEntityException {
        return exerciseSolutionRepository.findById(id).orElseThrow(NoSuchEntityException::new);
    }


    /**
     * Checks whether the given {@code exam} cannot be modified anymore because it already started.
     *
     * @param exam The {@link Exam} to be checked.
     * @return {@code true} if the {@link Exam} cannot be modified, or {@code false} otherwise.
     */
    private boolean cannotBeModified(final Exam exam) {
        Assert.notNull(exam, "The exam to be checked must not be null");
        return LocalDateTime.now().isAfter(exam.getStartingAt());
    }

    /**
     * An {@link IllegalEntityStateError} that indicates that an exam cannot be modified or deleted
     * because it already started.
     */
    private final static IllegalEntityStateError EXAM_ALREADY_STARTED =
            new IllegalEntityStateError("The exam already started", "startingAt");

    /**
     * Indicates if the {@link #HOURS_BEFORE} value is {@code 1}.
     * It is used for internal error messaging purposes.
     */
    private final static boolean ONLY_ONE_HOUR = HOURS_BEFORE == 1;

    /**
     * An {@link IllegalEntityStateError} that indicates that the startingAt value for an exam cannot be modified
     * because the actual moment is in the period before the said exam starts
     * (given by the {@link #HOURS_BEFORE} value).
     */
    private final static IllegalEntityStateError HOURS_BEFORE_FOR_STARTING_AT =
            new IllegalEntityStateError("The exam's starting moment cannot be modified if there " +
                    (ONLY_ONE_HOUR ? "is" : "are") +
                    " only " + HOURS_BEFORE + " hour" + (ONLY_ONE_HOUR ? "" : "s") +
                    " before the exam starts", "startingAt");

    private final static IllegalEntityStateError EXAM_HAS_NOT_STARTED =
            new IllegalEntityStateError("The exam has not started yet, so solutions cannot be created",
                    "startingAt");

    private final static IllegalEntityStateError EXAM_HAS_FINISHED =
            new IllegalEntityStateError("The exam has already finished, so solutions cannot be created",
                    "startingAt", "duration");
}
