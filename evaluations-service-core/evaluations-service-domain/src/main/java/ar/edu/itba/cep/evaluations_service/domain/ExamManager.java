package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.messages_sender.ExecutorServiceCommandProxy;
import ar.edu.itba.cep.evaluations_service.messages_sender.ExecutorServiceCommandProxy.ExecutionResultHandlerData;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
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
     * An {@link ExecutorServiceCommandProxy} to request execution of {@link ExerciseSolution}s.
     */
    private final ExecutorServiceCommandProxy executorServiceCommandProxy;

    /**
     * Constructor.
     *
     * @param examRepository              Repository for {@link Exam}s.
     * @param exerciseRepository          Repository for {@link Exercise}s.
     * @param testCaseRepository          Repository for {@link TestCase}s.
     * @param exerciseSolutionRepository  Repository for {@link ExerciseSolution}s.
     * @param executorServiceCommandProxy An {@link ExecutorServiceCommandProxy}
     *                                    to request execution of {@link ExerciseSolution}s.
     */
    @Autowired
    public ExamManager(final ExamRepository examRepository,
                       final ExerciseRepository exerciseRepository,
                       final TestCaseRepository testCaseRepository,
                       final ExerciseSolutionRepository exerciseSolutionRepository,
                       final ExecutorServiceCommandProxy executorServiceCommandProxy) {
        this.examRepository = examRepository;
        this.exerciseRepository = exerciseRepository;
        this.testCaseRepository = testCaseRepository;
        this.exerciseSolutionRepository = exerciseSolutionRepository;
        this.executorServiceCommandProxy = executorServiceCommandProxy;
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
        final var exam = DomainHelper.loadExam(examRepository, examId);
        exam.update(description, startingAt, duration); // The Exam verifies state by its own.
        examRepository.save(exam);
    }

    @Override
    @Transactional
    public void startExam(final long examId) throws IllegalEntityStateException {
        final var exam = DomainHelper.loadExam(examRepository, examId);
        exam.startExam(); // The Exam verifies state by its own.
        examRepository.save(exam);
    }

    @Override
    @Transactional
    public void finishExam(final long examId) throws IllegalEntityStateException {
        final var exam = DomainHelper.loadExam(examRepository, examId);
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
        final var exam = DomainHelper.loadExam(examRepository, examId);
        return exerciseRepository.getExamExercises(exam);
    }

    @Override
    @Transactional
    public void clearExercises(final long examId) throws IllegalEntityStateException {
        final var exam = DomainHelper.loadExam(examRepository, examId);
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
        final var exam = DomainHelper.loadExam(examRepository, examId);
        performExamUpcomingStateVerification(exam);
        final var exercise = new Exercise(question, exam);
        return exerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    public void changeExerciseQuestion(final long exerciseId, final String question)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var exercise = DomainHelper.loadExercise(exerciseRepository, exerciseId);
        performExamUpcomingStateVerification(exercise.getExam());
        exercise.setQuestion(question);
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
    public List<TestCase> getPublicTestCases(final long exerciseId) {
        final var exercise = DomainHelper.loadExercise(exerciseRepository, exerciseId);
        return testCaseRepository.getExercisePublicTestCases(exercise);
    }

    @Override
    public List<TestCase> getPrivateTestCases(final long exerciseId) {
        final var exercise = DomainHelper.loadExercise(exerciseRepository, exerciseId);
        return testCaseRepository.getExercisePrivateTestCases(exercise);
    }

    @Override
    public Page<ExerciseSolution> listSolutions(final long exerciseId, final PagingRequest pagingRequest) {
        final var exercise = DomainHelper.loadExercise(exerciseRepository, exerciseId);
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
        final var exercise = DomainHelper.loadExercise(exerciseRepository, exerciseId);
        performExamUpcomingStateVerification(exercise.getExam());
        final var testCase = new TestCase(visibility, exercise);
        testCase.setInputs(inputs);
        testCase.setExpectedOutputs(expectedOutputs);
        return testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void changeVisibility(final long testCaseId, final TestCase.Visibility visibility)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var testCase = DomainHelper.loadTestCase(testCaseRepository, testCaseId);
        performExamUpcomingStateVerification(testCase.getExercise().getExam());
        testCase.setVisibility(visibility);
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void changeInputs(final long testCaseId, final List<String> inputs)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var testCase = DomainHelper.loadTestCase(testCaseRepository, testCaseId);
        performExamUpcomingStateVerification(testCase.getExercise().getExam());
        testCase.setInputs(inputs);
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void changeExpectedOutputs(final long testCaseId, final List<String> outputs)
            throws IllegalEntityStateException, IllegalArgumentException {
        final var testCase = DomainHelper.loadTestCase(testCaseRepository, testCaseId);
        performExamUpcomingStateVerification(testCase.getExercise().getExam());
        testCase.setExpectedOutputs(outputs);
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void clearInputs(final long testCaseId) throws IllegalEntityStateException {
        final var testCase = DomainHelper.loadTestCase(testCaseRepository, testCaseId);
        performExamUpcomingStateVerification(testCase.getExercise().getExam());
        testCase.removeAllInputs();
        testCaseRepository.save(testCase);
    }

    @Override
    @Transactional
    public void clearOutputs(final long testCaseId) throws IllegalEntityStateException {
        final var testCase = DomainHelper.loadTestCase(testCaseRepository, testCaseId);
        performExamUpcomingStateVerification(testCase.getExercise().getExam());
        testCase.removeAllExpectedOutputs();
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
            throws IllegalEntityStateException, IllegalArgumentException {
        final var exercise = DomainHelper.loadExercise(exerciseRepository, exerciseId);
        // Verify that the exam is in progress in order to create solutions for exercises owned by it.
        if (exercise.getExam().getState() != Exam.State.IN_PROGRESS) {
            throw new IllegalEntityStateException(EXAM_IS_NOT_IN_PROGRESS);
        }
        final var solution = exerciseSolutionRepository.save(new ExerciseSolution(exercise, answer));
        final var testCases = testCaseRepository.getExercisePrivateTestCases(exercise);

        testCases.forEach(testCase -> executorServiceCommandProxy
                .requestExecution(
                        answer,
                        testCase.getInputs(),
                        new ExecutionResultHandlerData(solution.getId(), testCase.getId())
                )
        );
        return solution;
    }

    // ================================================================================================================
    // Helpers
    // ================================================================================================================


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
