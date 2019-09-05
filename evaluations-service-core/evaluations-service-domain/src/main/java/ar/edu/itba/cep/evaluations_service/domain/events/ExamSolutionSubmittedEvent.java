package ar.edu.itba.cep.evaluations_service.domain.events;

import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the event of an {@link ExamSolutionSubmission} being submitted.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@AllArgsConstructor(staticName = "create")
public class ExamSolutionSubmittedEvent {

    /**
     * The {@link ExamSolutionSubmission} that is being submitted.
     */
    private final ExamSolutionSubmission submission;
}
