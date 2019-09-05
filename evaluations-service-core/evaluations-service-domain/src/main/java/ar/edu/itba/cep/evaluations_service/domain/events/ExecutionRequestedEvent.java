package ar.edu.itba.cep.evaluations_service.domain.events;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the event
 * of requesting the execution of the answer of an {@link ExerciseSolution} with a {@link TestCase}.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@AllArgsConstructor(staticName = "create")
public class ExecutionRequestedEvent {

    /**
     * The {@link ExerciseSolution}.
     */
    private final ExerciseSolution solution;
    /**
     * The {@link TestCase}
     */
    private final TestCase testCase;

}
