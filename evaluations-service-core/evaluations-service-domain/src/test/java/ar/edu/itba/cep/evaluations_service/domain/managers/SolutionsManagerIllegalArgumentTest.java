package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.events.ExamFinishedEvent;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link SolutionsManager}, containing tests for the illegal arguments situations
 * (i.e how the manager behaves when operating with invalid values).
 */
@ExtendWith(MockitoExtension.class)
class SolutionsManagerIllegalArgumentTest extends AbstractSolutionsManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository       An {@link ExamRepository} that is injected to the {@link SolutionsManager}.
     * @param exerciseRepository   An {@link ExerciseRepository} that is injected to the {@link SolutionsManager}.
     * @param submissionRepository An {@link ExamSolutionSubmissionRepository} that is injected to the {@link SolutionsManager}.
     * @param solutionRepository   An {@link ExerciseSolutionRepository} that is injected to the {@link SolutionsManager}.
     * @param publisher            An {@link ApplicationEventPublisher} that is injected to the {@link SolutionsManager}.
     */
    SolutionsManagerIllegalArgumentTest(
            @Mock(name = "examRepository") final ExamRepository examRepository,
            @Mock(name = "exerciseRepository") final ExerciseRepository exerciseRepository,
            @Mock(name = "submissionRepository") final ExamSolutionSubmissionRepository submissionRepository,
            @Mock(name = "solutionRepository") final ExerciseSolutionRepository solutionRepository,
            @Mock(name = "resultRepository") final ExerciseSolutionResultRepository resultRepository,
            @Mock(name = "eventPublisher") final ApplicationEventPublisher publisher) {
        super(examRepository, exerciseRepository, submissionRepository, solutionRepository, resultRepository, publisher);
    }


    // ================================================================================================================
    // Event Listeners
    // ================================================================================================================

    /**
     * Tests the reception of a {@code null} {@link ExamFinishedEvent}.
     */
    @Test
    void testNullExamFinishedEvent() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> solutionsManager.examFinished(null),
                "The reception of a null result event does not throw an IllegalArgumentException"
        );
        verifyNoInteractionsWithMocks();
    }

    /**
     * Tests the reception of a {@code null} {@link ar.edu.itba.cep.evaluations_service.models.Exam}
     * in the {@link ExamFinishedEvent}.
     *
     * @param event The {@link ExamFinishedEvent} with the {@link ar.edu.itba.cep.evaluations_service.models.Exam}
     *              being {@code null}.
     */
    @Test
    void testNullExamInExamFinishedEvent(@Mock(name = "event") final ExamFinishedEvent event) {
        when(event.getExam()).thenReturn(null);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> solutionsManager.examFinished(event),
                "The reception of a null result event does not throw an IllegalArgumentException"
        );
        verifyNoInteractionsWithMocks();
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Performs verifications over all the mocks, checking that there are no interactions with them.
     */
    private void verifyNoInteractionsWithMocks() {
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(submissionRepository);
        verifyZeroInteractions(solutionRepository);
        verifyZeroInteractions(resultRepository);
        verifyZeroInteractions(publisher);
    }
}
