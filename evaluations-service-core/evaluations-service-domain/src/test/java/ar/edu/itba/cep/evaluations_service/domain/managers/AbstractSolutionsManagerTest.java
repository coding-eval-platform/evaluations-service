package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

/**
 * A base Test class for {@link SolutionsManager}.
 */
abstract class AbstractSolutionsManagerTest {

    // ================================================================================================================
    // Mocks
    // ================================================================================================================

    /* package */ final ExamRepository examRepository;
    /* package */ final ExerciseRepository exerciseRepository;
    /* package */ final ExamSolutionSubmissionRepository submissionRepository;
    /* package */ final ExerciseSolutionRepository solutionRepository;
    /* package */ final ExerciseSolutionResultRepository resultRepository;
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
     * @param examRepository       An {@link ExamRepository} to be injected to the {@link ExamManager}.
     * @param exerciseRepository   An {@link ExerciseRepository} to be injected to the {@link ExamManager}.
     * @param submissionRepository An {@link ExamSolutionSubmissionRepository} to be injected to the {@link ExamManager}.
     * @param solutionRepository   An {@link ExerciseSolutionRepository} to be injected to the {@link ExamManager}.
     * @param resultRepository     An {@link ExerciseSolutionResultRepository} to be injected to the {@link ExamManager}.
     * @param publisher            An {@link ApplicationEventPublisher} to be injected to the {@link ExamManager}.
     */
    AbstractSolutionsManagerTest(
            final ExamRepository examRepository,
            final ExerciseRepository exerciseRepository,
            final ExamSolutionSubmissionRepository submissionRepository,
            final ExerciseSolutionRepository solutionRepository,
            final ExerciseSolutionResultRepository resultRepository,
            final ApplicationEventPublisher publisher) {
        this.examRepository = examRepository;
        this.exerciseRepository = exerciseRepository;
        this.submissionRepository = submissionRepository;
        this.solutionRepository = solutionRepository;
        this.resultRepository = resultRepository;
        this.publisher = publisher;
        this.solutionsManager = new SolutionsManager(
                examRepository,
                exerciseRepository,
                submissionRepository,
                solutionRepository,
                resultRepository,
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
        verifyZeroInteractions(submissionRepository);
        verifyZeroInteractions(solutionRepository);
        verifyZeroInteractions(resultRepository);
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
        verify(submissionRepository, only()).findById(submissionId);
        verifyZeroInteractions(solutionRepository);
        verifyZeroInteractions(resultRepository);
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
        verifyZeroInteractions(submissionRepository);
        verify(solutionRepository, only()).findById(solutionId);
        verifyZeroInteractions(resultRepository);
        verifyZeroInteractions(publisher);
    }
}
