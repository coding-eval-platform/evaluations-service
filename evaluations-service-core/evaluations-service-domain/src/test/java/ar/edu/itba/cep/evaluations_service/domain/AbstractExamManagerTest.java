package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * A base Test class for {@link ExamManager}.
 */
abstract class AbstractExamManagerTest {

    // ================================================================================================================
    // Mocks
    // ================================================================================================================

    /**
     * A mocked {@link ExamRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExamRepository examRepository;
    /**
     * A mocked {@link ExerciseRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseRepository exerciseRepository;
    /**
     * A mocked {@link TestCaseRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final TestCaseRepository testCaseRepository;
    /**
     * A mocked {@link ExamRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseSolutionRepository exerciseSolutionRepository;
    /**
     * A mocked {@link ExerciseSolutionResultRepository} that is injected to the {@link ExamManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseSolutionResultRepository exerciseSolutionResultRepository;


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
     * @param examRepository                   A mocked {@link ExamRepository}
     *                                         that is injected to the {@link ExamManager}.
     * @param exerciseRepository               A mocked {@link ExerciseRepository}
     *                                         that is injected to the {@link ExamManager}.
     * @param testCaseRepository               A mocked {@link TestCaseRepository}
     *                                         that is injected to the {@link ExamManager}.
     * @param exerciseSolutionRepository       A mocked {@link ExamRepository}
     *                                         that is injected to the {@link ExamManager}.
     * @param exerciseSolutionResultRepository A mocked {@link ExerciseSolutionResultRepository}
     *                                         that is injected to the {@link ExamManager}.
     */
    AbstractExamManagerTest(
            @Mock final ExamRepository examRepository,
            @Mock final ExerciseRepository exerciseRepository,
            @Mock final TestCaseRepository testCaseRepository,
            @Mock final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock final ExerciseSolutionResultRepository exerciseSolutionResultRepository) {
        this.examRepository = examRepository;
        this.exerciseRepository = exerciseRepository;
        this.testCaseRepository = testCaseRepository;
        this.exerciseSolutionRepository = exerciseSolutionRepository;
        this.exerciseSolutionResultRepository = exerciseSolutionResultRepository;
        this.examManager = new ExamManager(
                examRepository,
                exerciseRepository,
                testCaseRepository,
                exerciseSolutionRepository,
                exerciseSolutionResultRepository
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
