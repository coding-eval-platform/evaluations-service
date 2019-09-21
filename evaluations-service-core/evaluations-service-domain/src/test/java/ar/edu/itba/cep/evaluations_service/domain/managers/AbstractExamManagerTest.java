package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

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
     * An {@link ApplicationEventPublisher} that is injected to the {@link ResultsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ApplicationEventPublisher publisher;


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
     * @param examRepository     An {@link ExamRepository} that is injected to the {@link ExamManager}.
     * @param exerciseRepository An {@link ExerciseRepository} that is injected to the {@link ExamManager}.
     * @param testCaseRepository A {@link TestCaseRepository} that is injected to the {@link ExamManager}.
     * @param publisher          An {@link ApplicationEventPublisher} that is injected to the {@link ResultsManager}.
     */
    AbstractExamManagerTest(
            final ExamRepository examRepository,
            final ExerciseRepository exerciseRepository,
            final TestCaseRepository testCaseRepository,
            final ApplicationEventPublisher publisher) {
        this.examRepository = examRepository;
        this.exerciseRepository = exerciseRepository;
        this.testCaseRepository = testCaseRepository;
        this.publisher = publisher;
        this.examManager = new ExamManager(examRepository, exerciseRepository, testCaseRepository, publisher);
    }

    /**
     * Verifies that there were no interactions with any repository.
     */
    /* package */ void verifyNoInteractionWithAnyMock() {
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(testCaseRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Verifies that interactions with repositories only implies searching for an {@link Exam}.
     *
     * @param examId The id of the {@link Exam} being searched.
     */
    /* package */ void verifyOnlyExamSearch(final long examId) {
        verify(examRepository, only()).findById(examId);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(testCaseRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Verifies that interactions with repositories only implies searching for an {@link Exercise}.
     *
     * @param exerciseId The id of the {@link Exercise} being searched.
     */
    /* package */ void verifyOnlyExerciseSearch(final long exerciseId) {
        verifyZeroInteractions(examRepository);
        verify(exerciseRepository, only()).findById(exerciseId);
        verifyZeroInteractions(testCaseRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Verifies that interactions with repositories only implies searching for a {@link TestCase}.
     *
     * @param testCaseId The id of the {@link TestCase} being searched.
     */
    /* package */ void verifyOnlyTestCaseSearch(final long testCaseId) {
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verify(testCaseRepository, only()).findById(testCaseId);
        verifyZeroInteractions(publisher);
    }
}
