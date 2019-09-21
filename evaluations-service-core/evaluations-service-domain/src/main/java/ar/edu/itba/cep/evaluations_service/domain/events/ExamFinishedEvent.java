package ar.edu.itba.cep.evaluations_service.domain.events;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the event of an {@link Exam} being finished (i.e {@link Exam#finishExam()} is called).
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@AllArgsConstructor(staticName = "create")
public class ExamFinishedEvent {

    /**
     * The {@link Exam} that has finished.
     */
    private final Exam exam;
}
