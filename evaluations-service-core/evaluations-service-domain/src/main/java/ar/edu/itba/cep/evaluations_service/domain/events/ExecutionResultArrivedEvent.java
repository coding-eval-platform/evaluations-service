package ar.edu.itba.cep.evaluations_service.domain.events;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the event of receiving an {@link ExecutionResult}.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@AllArgsConstructor(staticName = "create")
public class ExecutionResultArrivedEvent {

    /**
     * The id of the solution to which the execution belongs to.
     */
    private final long solutionId;
    /**
     * The id of the test case to which the execution belongs to.
     */
    private final long testCaseId;
    /**
     * The {@link ExecutionResult} that has arrived.
     */
    private final ExecutionResult result;
}
