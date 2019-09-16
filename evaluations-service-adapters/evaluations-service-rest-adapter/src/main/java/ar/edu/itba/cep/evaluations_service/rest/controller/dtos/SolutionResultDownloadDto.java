package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Data Transfer Object for {@link ExerciseSolutionResult}.
 *
 * @param <T> The concrete type for the result.
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class SolutionResultDownloadDto<T> {

    /**
     * The {@link ar.edu.itba.cep.evaluations_service.models.TestCase}'s id
     * to which the wrapped {@link ExerciseSolutionResult} belongs to.
     */
    private final long testCaseId;
    /**
     * The result (is abstract, as it depends on the solution being marked).
     */
    private final T result;

    /**
     * @return The {@link ar.edu.itba.cep.evaluations_service.models.TestCase}'s id
     * to which the wrapped {@link ExerciseSolutionResult} belongs to.
     */
    @JsonProperty(value = "testCaseId", access = JsonProperty.Access.READ_ONLY)
    public long getTestCaseId() {
        return testCaseId;
    }

    /**
     * @return The result (is abstract, as it depends on the solution being marked).
     */
    @JsonProperty(value = "result", access = JsonProperty.Access.READ_ONLY)
    public T getResult() {
        return result;
    }

    /**
     * Creates the corresponding {@link SolutionResultDownloadDto}
     * according to the given {@code exerciseSolutionResult}.
     *
     * @param exerciseSolutionResult The {@link ExerciseSolutionResult} to be wrapped in this DTO.
     * @return The created DTO.
     * @implNote This method checks whether the given {@code exerciseSolutionResult} is marked
     * (i.e it uses the {@link ExerciseSolutionResult#isMarked()} method).
     */
    public static SolutionResultDownloadDto<?> buildFor(final ExerciseSolutionResult exerciseSolutionResult) {
        return exerciseSolutionResult.isMarked() ?
                new MarkedResult(exerciseSolutionResult) :
                new UnMarkedResult(exerciseSolutionResult);
    }


    /**
     * An extension of {@link SolutionResultDownloadDto} for marked {@link ExerciseSolutionResult}s.
     * It uses the {@link ExerciseSolutionResult#getResult()} values as the concrete type of result.
     */
    private static final class MarkedResult extends SolutionResultDownloadDto<ExerciseSolutionResult.Result> {

        /**
         * Constructor.
         *
         * @param exerciseSolutionResult The {@link ExerciseSolutionResult} to be wrapped in this DTO.
         */
        private MarkedResult(final ExerciseSolutionResult exerciseSolutionResult) {
            super(exerciseSolutionResult.getTestCase().getId(), exerciseSolutionResult.getResult());
        }
    }

    /**
     * An extension of {@link SolutionResultDownloadDto} for unmarked {@link ExerciseSolutionResult}s.
     * It uses a {@link String} with the {@code PENDING} value as the concrete type of result.
     */
    private static final class UnMarkedResult extends SolutionResultDownloadDto<String> {

        /**
         * The value used as a result.
         */
        private static final String RESULT_VALUE = "PENDING";

        /**
         * Constructor.
         *
         * @param exerciseSolutionResult The {@link ExerciseSolutionResult} to be wrapped in this DTO.
         */
        private UnMarkedResult(final ExerciseSolutionResult exerciseSolutionResult) {
            super(exerciseSolutionResult.getTestCase().getId(), RESULT_VALUE);
        }
    }
}
