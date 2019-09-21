package ar.edu.itba.cep.evaluations_service.domain.events;

import ar.edu.itba.cep.executor.models.ExecutionResponse;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the event of receiving an {@link ExecutionResponse}.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@AllArgsConstructor(staticName = "create")
public class ExecutionResponseArrivedEvent {

    /**
     * The id of the solution to which the execution belongs to.
     */
    private final long solutionId;
    /**
     * The id of the test case to which the execution belongs to.
     */
    private final long testCaseId;
    /**
     * The {@link ExecutionResponse} that has arrived.
     */
    private final ExecutionResponse response;
}
