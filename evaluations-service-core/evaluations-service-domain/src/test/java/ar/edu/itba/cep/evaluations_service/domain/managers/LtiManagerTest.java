package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.events.ExamScoredEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.external_cep_services.lti_service.LtiService;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.lti.ExamScoringRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test class for the {@link LtiManager}.
 */
@ExtendWith(MockitoExtension.class)
class LtiManagerTest {

    // ================================================================================================================
    // Mocks
    // ================================================================================================================

    /**
     * An {@link LtiService} mock that is injected to the {@link LtiManager}.
     */
    private final LtiService ltiService;


    // ================================================================================================================
    // Solutions Manager
    // ================================================================================================================

    /**
     * The {@link LtiManager} being tested.
     */
    private final LtiManager ltiManager;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param ltiService An {@link LtiService} mock that is injected to the {@link LtiManager}.
     */
    LtiManagerTest(
            @Mock(name = "ltiService") final LtiService ltiService) {
        this.ltiService = ltiService;
        this.ltiManager = new LtiManager(ltiService);
    }

    @Test
    void testRequestIsSent(
            @Mock(name = "event") final ExamScoredEvent event,
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "submission") final ExamSolutionSubmission submission) {

        final var examId = TestHelper.validExamId();
        final var subject = TestHelper.validOwner();
        final var score = TestHelper.validScore();

        when(event.getSubmission()).thenReturn(submission);
        when(exam.getId()).thenReturn(examId);
        when(submission.getExam()).thenReturn(exam);
        when(submission.getSubmitter()).thenReturn(subject);
        when(submission.getScore()).thenReturn(score);
        doNothing().when(ltiService).scoreExam(any(ExamScoringRequest.class));

        ltiManager.examScored(event);

        verify(ltiService, only()).scoreExam(argThat(requestMatches(examId, subject, score)));
    }

    /**
     * Creates an {@link ArgumentMatcher} of {@link ExamScoringRequest} to check if the said request
     * contains the given {@code examId}, {@code subject} and {@code score}.
     *
     * @param examId  The exam id to be checked.
     * @param subject The subject to be checked.
     * @param score   The score to be checked.
     * @return The {@link ArgumentMatcher}.
     */
    private static ArgumentMatcher<ExamScoringRequest> requestMatches(
            final long examId, final String subject, final int score) {
        return request -> request.getExamId() == examId
                && request.getSubject().equals(subject)
                && request.getScore() == score;
    }
}
