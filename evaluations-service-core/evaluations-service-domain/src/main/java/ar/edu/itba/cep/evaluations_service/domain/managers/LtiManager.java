package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.events.ExamScoredEvent;
import ar.edu.itba.cep.evaluations_service.external_cep_services.lti_service.LtiService;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.lti.ExamScoringRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * A component in charge of sending {@link ExamScoringRequest}s to the LTI service.
 */
@Component
@AllArgsConstructor
public class LtiManager {

    /**
     * The {@link LtiService} to which the {@link ExamScoringRequest} will be sent.
     */
    private final LtiService ltiService;


    /**
     * Handles the given {@code event}.
     *
     * @param event The {@link ExamScoredEvent} to be handled.
     * @throws IllegalArgumentException If the {@code event} is {@code null},
     *                                  if it contains a {@code null} {@link ExamSolutionSubmission},
     *                                  or if it contains an {@link ExamSolutionSubmission} without score.
     */
    @Transactional
    @EventListener(ExamScoredEvent.class)
    public void examScored(final ExamScoredEvent event) throws IllegalArgumentException {
        Assert.notNull(event, "The event must not be null");
        Assert.notNull(event.getSubmission(), "The event contains a null submission");
        Assert.notNull(event.getSubmission().getScore(), "The event contains a submission without score");
        ltiService.scoreExam(examScoringRequest(event.getSubmission()));
    }


    /**
     * Builds an {@link ExamScoringRequest} from the given {@code submission} and {@code score}.
     *
     * @param submission The {@link ExamSolutionSubmission} from where data for the {@link ExamScoringRequest} is taken.
     * @return The created {@link ExamScoringRequest}.
     */
    private static ExamScoringRequest examScoringRequest(final ExamSolutionSubmission submission) {
        return new ExamScoringRequest(submission.getExam().getId(), submission.getSubmitter(), submission.getScore());
    }
}
