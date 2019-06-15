package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutorServiceCommandMessageProxy;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import org.mockito.Mockito;

/**
 * A base Test class for {@link ExamManager}.
 */
abstract class AbstractExamManagerTest {

    // ================================================================================================================
    // Mocks
    // ================================================================================================================

    /**
     * An {@link ExamRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExamRepository examRepository;
    /**
     * An {@link ExerciseRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseRepository exerciseRepository;
    /**
     * A {@link TestCaseRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final TestCaseRepository testCaseRepository;
    /**
     * An {@link ExerciseSolutionRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseSolutionRepository exerciseSolutionRepository;
    /**
     * An {@link ExerciseSolutionResultRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseSolutionResultRepository exerciseSolutionResultRepository;

    /**
     * An {@link ExecutorServiceCommandMessageProxy} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExecutorServiceCommandMessageProxy executorServiceCommandMessageProxy;


    // ================================================================================================================
    // Exam Manager
    // ================================================================================================================

    /**
     * The {@link ExamManager} being tested.
     */
    /* package */ final ExamManager examManager;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param examRepository                     An {@link ExamRepository}
     *                                           that is injected to the {@link ExamManager}.
     * @param exerciseRepository                 An {@link ExerciseRepository}
     *                                           that is injected to the {@link ExamManager}.
     * @param testCaseRepository                 A {@link TestCaseRepository}
     *                                           that is injected to the {@link ExamManager}.
     * @param exerciseSolutionRepository         An {@link ExerciseSolutionRepository}
     *                                           that is injected to the {@link ExamManager}.
     * @param exerciseSolutionResultRepository   An {@link ExerciseSolutionResultRepository}
     *                                           that is injected to the {@link ExamManager}.
     * @param executorServiceCommandMessageProxy An {@link ExecutorServiceCommandMessageProxy}
     *                                           that is injected to the {@link ExamManager}.
     */
    AbstractExamManagerTest(
            final ExamRepository examRepository,
            final ExerciseRepository exerciseRepository,
            final TestCaseRepository testCaseRepository,
            final ExerciseSolutionRepository exerciseSolutionRepository,
            final ExerciseSolutionResultRepository exerciseSolutionResultRepository,
            final ExecutorServiceCommandMessageProxy executorServiceCommandMessageProxy) {
        this.examRepository = examRepository;
        this.exerciseRepository = exerciseRepository;
        this.testCaseRepository = testCaseRepository;
        this.exerciseSolutionRepository = exerciseSolutionRepository;
        this.exerciseSolutionResultRepository = exerciseSolutionResultRepository;
        this.executorServiceCommandMessageProxy = executorServiceCommandMessageProxy;
        this.examManager = new ExamManager(
                examRepository,
                exerciseRepository,
                testCaseRepository,
                exerciseSolutionRepository,
                exerciseSolutionResultRepository,
                executorServiceCommandMessageProxy
        );
    }

    /**
     * Verifies that there were no interactions with any repository.
     */
    /* package */ void verifyNoInteractionWithAnyMockedRepository() {
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Verifies that interactions with repositories only implies searching for an {@link Exam}.
     *
     * @param examId The id of the {@link Exam} being searched.
     */
    /* package */ void verifyOnlyExamSearch(final long examId) {
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Verifies that interactions with repositories only implies searching for an {@link Exercise}.
     *
     * @param exerciseId The id of the {@link Exercise} being searched.
     */
    /* package */ void verifyOnlyExerciseSearch(final long exerciseId) {
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verify(exerciseRepository, Mockito.only()).findById(exerciseId);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Verifies that interactions with repositories only implies searching for a {@link TestCase}.
     *
     * @param testCaseId The id of the {@link TestCase} being searched.
     */
    /* package */ void verifyOnlyTestCaseSearch(final long testCaseId) {
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.only()).findById(testCaseId);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }
}
