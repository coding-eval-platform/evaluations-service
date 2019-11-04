package ar.edu.itba.cep.evaluations_service.domain.events;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.lti.ExamScoringRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the event of an {@link Exam} being scored
 * (i.e the score of an {@link ExamSolutionSubmission} has been set).
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@AllArgsConstructor(staticName = "create")
public class ExamScoredEvent {

    /**
     * The {@link ExamSolutionSubmission} from where data for the {@link ExamScoringRequest} is taken.
     */
    private final ExamSolutionSubmission submission;
}
