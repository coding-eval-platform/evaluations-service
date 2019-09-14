package ar.edu.itba.cep.evaluations_service.domain.events;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import lombok.*;

/**
 * Represents the event
 * of requesting the execution of the answer of an {@link ExerciseSolution} with a {@link TestCase}.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecutionRequestedEvent {

    /**
     * The {@link ExerciseSolution}.
     */
    private final ExerciseSolution solution;
    /**
     * The {@link TestCase}
     */
    private final TestCase testCase;


    /**
     * Builds an {@link ExecutionRequestedEvent} from the given {@code result}.
     *
     * @param result The {@link ExerciseSolutionResult} from which the event will be created.
     * @return The created {@link ExecutionRequestedEvent}.
     */
    public static ExecutionRequestedEvent fromResult(final ExerciseSolutionResult result) {
        return new ExecutionRequestedEvent(result.getSolution(), result.getTestCase());
    }

}
