package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Test class for {@link SolutionsManager},
 * containing tests for the uniqueness condition
 * (i.e how the manager behaves when trying to create entities that already exist with a given set of values).
 */
@ExtendWith(MockitoExtension.class)
class SolutionsManagerUniquenessTest extends AbstractSolutionsManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository       An {@link ExamRepository} that is injected to the {@link SolutionsManager}.
     * @param exerciseRepository   An {@link ExerciseRepository} that is injected to the {@link SolutionsManager}.
     * @param submissionRepository An {@link ExamSolutionSubmissionRepository} that is injected to the {@link SolutionsManager}.
     * @param solutionRepository   An {@link ExerciseSolutionRepository} that is injected to the {@link SolutionsManager}.
     * @param publisher            An {@link ApplicationEventPublisher} that is injected to the {@link SolutionsManager}.
     */
    SolutionsManagerUniquenessTest(
            @Mock(name = "examRepository") final ExamRepository examRepository,
            @Mock(name = "exerciseRepository") final ExerciseRepository exerciseRepository,
            @Mock(name = "submissionRepository") final ExamSolutionSubmissionRepository submissionRepository,
            @Mock(name = "solutionRepository") final ExerciseSolutionRepository solutionRepository,
            @Mock(name = "resultRepository") final ExerciseSolutionResultRepository resultRepository,
            @Mock(name = "eventPublisher") final ApplicationEventPublisher publisher) {
        super(examRepository, exerciseRepository, submissionRepository, solutionRepository, resultRepository, publisher);
    }


    /**
     * Tests that an {@link ExamSolutionSubmission} is not created when another one exists for a given
     * "(exam, submitter)" pair.
     *
     * @param exam            A mocked {@link Exam} (i.e the one to which the submission belongs).
     * @param authentication  A mocked {@link Authentication} that will hold a mocked principal.
     * @param securityContext A mocked {@link SecurityContext} to be retrieved from the {@link SecurityContextHolder}.
     */
    @Test
    void testCreateSubmissionWhenAlreadyExists(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "authentication") final Authentication authentication,
            @Mock(name = "securityContext") final SecurityContext securityContext) {
        final var examId = TestHelper.validExamId();
        final var submitter = TestHelper.validOwner();
        when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(submissionRepository.existsSubmissionFor(exam, submitter)).thenReturn(true);
        TestHelper.setupSecurityContext(submitter, authentication, securityContext);
        Assertions.assertThrows(
                UniqueViolationException.class,
                () -> solutionsManager.createExamSolutionSubmission(examId),
                "Creating another submission for a given (exam, submitter) pair is being allowed."
        );
        verify(examRepository, only()).findById(examId);
        verifyZeroInteractions(solutionRepository);
        verify(submissionRepository, times(1)).existsSubmissionFor(exam, submitter);
        verifyZeroInteractions(solutionRepository);
        verifyZeroInteractions(resultRepository);
        verifyZeroInteractions(publisher);
        TestHelper.clearSecurityContext();
    }
}
