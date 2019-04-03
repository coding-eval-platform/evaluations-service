package ar.edu.itba.cep.evaluations_service.domain;

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


/**
 * Manager for {@link Exam}s.
 */
@Service
@Transactional(readOnly = true)
public class ExamManager implements ExamService {

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
            throws IllegalEntityStateException, IllegalArgumentException {
        final var exam = loadExam(examId);
        exam.update(description, startingAt, duration); // The Exam verifies state by its own.
        examRepository.save(exam);
    }

    @Override
    @Transactional
    public void startExam(final long examId) throws IllegalEntityStateException {
        final var exam = loadExam(examId);
        exam.startExam(); // The Exam verifies state by its own.
        examRepository.save(exam);
    }

    @Override
    @Transactional
    public void finishExam(final long examId) throws IllegalEntityStateException {
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
    public List<Exercise> getExercises(final long examId) {
        final var exam = loadExam(examId);
        return exerciseRepository.getExamExercises(exam);
    }

    @Override
    @Transactional
    public void clearExercises(final long examId) throws IllegalEntityStateException {
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
    public Exercise createExercise(final long examId, final String question)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var exam = loadExam(examId);
        performExamUpcomingStateVerification(exam);
        final var exercise = new Exercise(question, exam);
        return exerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    public void changeExerciseQuestion(final long exerciseId, final String question)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var exercise = loadExercise(exerciseId);
        performExamUpcomingStateVerification(exercise.belongsToExam());
        exercise.setQuestion(question);
        exerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    public void deleteExercise(final long exerciseId) throws IllegalEntityStateException {
        exerciseRepository.findById(exerciseId)
                .ifPresent(exercise -> {
                    performExamUpcomingStateVerification(exercise.belongsToExam());
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
    @Transactional
    public TestCase createTestCase(final long exerciseId,
                                   final TestCase.Visibility visibility,
                                   final List<String> inputs, final List<String> expectedOutputs)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var exercise = loadExercise(exerciseId);
        performExamUpcomingStateVerification(exercise.belongsToExam());
        final var testCase = new TestCase(visibility, exercise);
        testCase.setInputs(inputs);
        testCase.setExpectedOutputs(expectedOutputs);
        return testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void changeVisibility(final long testCaseId, final TestCase.Visibility visibility)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var testCase = loadTestCase(testCaseId);
        performExamUpcomingStateVerification(testCase.belongsToExercise().belongsToExam());
        testCase.setVisibility(visibility);
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void changeInputs(final long testCaseId, final List<String> inputs)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var testCase = loadTestCase(testCaseId);
        performExamUpcomingStateVerification(testCase.belongsToExercise().belongsToExam());
        testCase.setInputs(inputs);
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void changeExpectedOutputs(final long testCaseId, final List<String> outputs)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var testCase = loadTestCase(testCaseId);
        performExamUpcomingStateVerification(testCase.belongsToExercise().belongsToExam());
        testCase.setExpectedOutputs(outputs);
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void clearInputs(final long testCaseId) throws IllegalEntityStateException {
        final var testCase = loadTestCase(testCaseId);
        performExamUpcomingStateVerification(testCase.belongsToExercise().belongsToExam());
        testCase.removeAllInputs();
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void clearOutputs(final long testCaseId) throws IllegalEntityStateException {
        final var testCase = loadTestCase(testCaseId);
        performExamUpcomingStateVerification(testCase.belongsToExercise().belongsToExam());
        testCase.removeAllExpectedOutputs();
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void deleteTestCase(final long testCaseId) throws IllegalEntityStateException {
        testCaseRepository.findById(testCaseId)
                .ifPresent(testCase -> {
                    performExamUpcomingStateVerification(testCase.belongsToExercise().belongsToExam());
                    testCaseRepository.delete(testCase);
                });
    }

    // ================================================================================================================
    // Solutions
    // ================================================================================================================

    @Override
    @Transactional
    public ExerciseSolution createExerciseSolution(final long exerciseId, final String answer)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var exercise = loadExercise(exerciseId);
        // Verify that the exam is in progress in order to create solutions for exercises owned by it.
        if (exercise.belongsToExam().getState() != Exam.State.IN_PROGRESS) {
            throw new IllegalEntityStateException(EXAM_IS_NOT_IN_PROGRESS);
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
    @Transactional
    public void processExecution(final long solutionId, final long testCaseId,
                                 final int exitCode, final List<String> stdOut, final List<String> stdErr)
            throws IllegalArgumentException {
        // First, validate arguments
        Assert.notNull(stdOut, "The stdout list cannot be null");
        Assert.notNull(stdErr, "The stderr list cannot be null");

        // Load solution and test case (checking if they exist)
        final var solution = loadSolution(solutionId);
        final var testCase = loadTestCase(testCaseId);

        // State validation is not needed because the existence of a solution proves state validity

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
