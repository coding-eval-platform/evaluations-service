package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExamSolutionSubmissionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

/**
 * A base Test class for {@link SolutionsManager}.
 */
abstract class AbstractSolutionsManagerTest {

    // ================================================================================================================
    // Mocks
    // ================================================================================================================

    /**
     * An {@link ExamRepository} that is injected to the {@link SolutionsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExamRepository examRepository;
    /**
     * An {@link ExerciseRepository} that is injected to the {@link SolutionsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseRepository exerciseRepository;
    /**
     * An {@link ExamSolutionSubmissionRepository} that is injected to the {@link SolutionsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExamSolutionSubmissionRepository examSolutionSubmissionRepository;
    /**
     * An {@link ExerciseSolutionRepository} that is injected to the {@link SolutionsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseSolutionRepository exerciseSolutionRepository;

    /**
     * An {@link ApplicationEventPublisher} that is injected to the {@link SolutionsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ApplicationEventPublisher publisher;


    // ================================================================================================================
    // Solutions Manager
    // ================================================================================================================

    /**
     * The {@link ExamManager} being tested.
     */
    /* package */ final SolutionsManager solutionsManager;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param examRepository     An {@link ExamRepository} that is injected to the {@link ExamManager}.
     * @param exerciseRepository An {@link ExerciseRepository} that is injected to the {@link ExamManager}.
     */
    AbstractSolutionsManagerTest(
            final ExamRepository examRepository,
            final ExerciseRepository exerciseRepository,
            final ExamSolutionSubmissionRepository examSolutionSubmissionRepository,
            final ExerciseSolutionRepository exerciseSolutionRepository,
            final ApplicationEventPublisher publisher) {
        this.examRepository = examRepository;
        this.exerciseRepository = exerciseRepository;
        this.examSolutionSubmissionRepository = examSolutionSubmissionRepository;
        this.exerciseSolutionRepository = exerciseSolutionRepository;
        this.publisher = publisher;
        this.solutionsManager = new SolutionsManager(
                examRepository,
                exerciseRepository,
                examSolutionSubmissionRepository,
                exerciseSolutionRepository,
                publisher
        );
    }


    /**
     * Verifies that interactions with repositories only implies searching for an {@link Exam}.
     *
     * @param examId The id of the {@link Exam} being searched.
     */
    /* package */ void verifyOnlyExamSearch(final long examId) {
        verify(examRepository, only()).findById(examId);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(examSolutionSubmissionRepository);
        verifyZeroInteractions(exerciseSolutionRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Verifies that interactions with repositories only implies searching for an {@link ExamSolutionSubmission}.
     *
     * @param submissionId The id of the {@link ExamSolutionSubmission} being searched.
     */
    /* package */ void verifyOnlySubmissionSearch(final long submissionId) {
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verify(examSolutionSubmissionRepository, only()).findById(submissionId);
        verifyZeroInteractions(exerciseSolutionRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Verifies that interactions with repositories only implies searching for an {@link ExerciseSolution}.
     *
     * @param solutionId The id of the {@link ExerciseSolution} being searched.
     */
    /* package */ void verifyOnlySolutionSearch(final long solutionId) {
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(examSolutionSubmissionRepository);
        verify(exerciseSolutionRepository, only()).findById(solutionId);
        verifyZeroInteractions(publisher);
    }
}
