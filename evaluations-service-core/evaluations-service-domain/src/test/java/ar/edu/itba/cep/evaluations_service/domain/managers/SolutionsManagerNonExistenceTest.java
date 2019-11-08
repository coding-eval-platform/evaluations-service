package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link SolutionsManager},
 * containing tests for the non-existence condition
 * (i.e how the manager behaves when trying to operate over entities that do not exist).
 */
@ExtendWith(MockitoExtension.class)
class SolutionsManagerNonExistenceTest extends AbstractSolutionsManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository       An {@link ExamRepository} that is injected to the {@link SolutionsManager}.
     * @param exerciseRepository   An {@link ExerciseRepository} that is injected to the {@link SolutionsManager}.
     * @param submissionRepository An {@link ExamSolutionSubmissionRepository} that is injected to the {@link SolutionsManager}.
     * @param solutionRepository   An {@link ExerciseSolutionRepository} that is injected to the {@link SolutionsManager}.
     * @param publisher            An {@link ApplicationEventPublisher} that is injected to the {@link SolutionsManager}.
     */
    SolutionsManagerNonExistenceTest(
            @Mock(name = "examRepository") final ExamRepository examRepository,
            @Mock(name = "exerciseRepository") final ExerciseRepository exerciseRepository,
            @Mock(name = "submissionRepository") final ExamSolutionSubmissionRepository submissionRepository,
            @Mock(name = "solutionRepository") final ExerciseSolutionRepository solutionRepository,
            @Mock(name = "resultRepository") final ExerciseSolutionResultRepository resultRepository,
            @Mock(name = "eventPublisher") final ApplicationEventPublisher publisher) {
        super(examRepository, exerciseRepository, submissionRepository, solutionRepository, resultRepository, publisher);
    }


    // ================================================================================================================
    // Exam Solution Submission
    // ================================================================================================================

    /**
     * Tests that trying to get {@link ExamSolutionSubmission}s belonging to an {@link Exam} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testGetSubmissionsOfNonExistenceExam() {
        final var examId = TestHelper.validExamId();
        when(examRepository.findById(examId)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> solutionsManager.getSolutionSubmissionsForExam(examId, any(PagingRequest.class)),
                "Trying to get submissions" +
                        " belonging to an exam that does not exist does not throw a NoSuchEntityException"
        );
        verifyOnlyExamSearch(examId);

    }

    /**
     * Tests that searching for an {@link ExamSolutionSubmission} that does not exist does not fail,
     * and returns an empty {@link Optional}.
     */
    @Test
    void testSearchForExamSolutionSubmissionThatDoesNotExist() {
        final var submissionId = TestHelper.validExamSolutionSubmissionId();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());
        Assertions.assertTrue(
                solutionsManager.getSubmission(submissionId).isEmpty(),
                "Searching for a submission that does not exist does not return an empty optional."
        );
        verifyOnlySubmissionSearch(submissionId);
    }

    /**
     * Tests that trying to create an {@link ExamSolutionSubmission} for an {@link Exam} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testCreateSubmissionForNonExistenceExam() {
        final var examId = TestHelper.validExamId();
        when(examRepository.findById(examId)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> solutionsManager.createExamSolutionSubmission(examId),
                "Trying to create a submission" +
                        " belonging to an exam that does not exist does not throw a NoSuchEntityException"
        );
        verifyOnlyExamSearch(examId);
    }

    /**
     * Tests that trying to submit an {@link ExamSolutionSubmission} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testSubmitNonExistenceSubmission() {
        final var submissionId = TestHelper.validExamSolutionSubmissionId();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> solutionsManager.submitSolutions(submissionId),
                "Trying to submit solutions for a submission that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
        verifyOnlySubmissionSearch(submissionId);
    }

    /**
     * Tests that trying to score an {@link ExamSolutionSubmission} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testScoreNonExistenceSubmission() {
        final var submissionId = TestHelper.validExamSolutionSubmissionId();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> solutionsManager.scoreSubmission(submissionId),
                "Trying to score a submission that does not exist does not throw a NoSuchEntityException"
        );
        verifyOnlySubmissionSearch(submissionId);
    }


    // ================================================================================================================
    // Exercises Solutions
    // ================================================================================================================

    /**
     * Tests that trying to get {@link ExerciseSolution}s
     * belonging to an {@link ExamSolutionSubmission} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testGetSolutionsOfNonExistenceSubmission() {
        final var submissionId = TestHelper.validExamSolutionSubmissionId();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> solutionsManager.getSolutionsForSubmission(submissionId),
                "Trying to get solutions for a submission that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
        verifyOnlySubmissionSearch(submissionId);
    }

    /**
     * Tests that searching for an {@link ExerciseSolution} that does not exist does not fail,
     * and returns an empty {@link Optional}.
     */
    @Test
    void testSearchForExerciseSolutionThatDoesNotExist() {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(solutionRepository.findById(solutionId)).thenReturn(Optional.empty());
        Assertions.assertTrue(
                solutionsManager.getSolution(solutionId).isEmpty(),
                "Searching for a solution that does not exist does not return an empty optional."
        );
        verifyOnlySolutionSearch(solutionId);
    }

    /**
     * Tests that modifying an {@link ExerciseSolution} that does not exist throws a {@link NoSuchEntityException}.
     */
    @Test
    void testModifyExerciseSolutionThatDoesNotExist() {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(solutionRepository.findById(solutionId)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> solutionsManager.modifySolution(
                        solutionId,
                        TestHelper.validExerciseSolutionAnswer(),
                        TestHelper.validCompilerFlags(),
                        TestHelper.validMainFileName()
                ),
                "Trying to modify a solution that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
        verifyOnlySolutionSearch(solutionId);
    }
}
