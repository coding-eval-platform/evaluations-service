package ar.edu.itba.cep.evaluations_service.commands.executor_service.dto;

import ar.edu.itba.cep.evaluations_service.commands.executor_service.ExecutionResult;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface that marks an object as an execution result data transfer object.
 * Includes subtyping metadata that indicates how to deserialize JSONs into objects that implement this interface.
 *
 * @param <R> The concrete type of {@link ExecutionResult} being wrapped by the implementors of this interface.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = FinishedExecutionResultDto.class,
                name = ExecutionResultDto.FINISHED_STRING_VALUE
        ),
        @JsonSubTypes.Type(
                value = TimedOutExecutionResultDto.class,
                name = ExecutionResultDto.TIMED_OUT_STRING_VALUE
        ),
        @JsonSubTypes.Type(
                value = CompileErrorExecutionResultDto.class,
                name = ExecutionResultDto.COMPILED_ERROR_STRING_VALUE
        ),
        @JsonSubTypes.Type(
                value = InitializationErrorExecutionResultDto.class,
                name = ExecutionResultDto.INITIALIZATION_ERROR_STRING_VALUE
        ),
        @JsonSubTypes.Type(
                value = UnknownErrorExecutionResultDto.class,
                name = ExecutionResultDto.UNKNOWN_ERROR_STRING_VALUE
        ),
})
public interface ExecutionResultDto<R extends ExecutionResult> {

    /**
     * Value that marks a JSON to be deserialized into a {@link FinishedExecutionResultDto}.
     */
    /* package */ String FINISHED_STRING_VALUE = "FINISHED";
    /**
     * Value that marks a JSON to be deserialized into a {@link TimedOutExecutionResultDto}.
     */
    /* package */ String TIMED_OUT_STRING_VALUE = "TIMED_OUT";
    /**
     * Value that marks a JSON to be deserialized into a {@link CompileErrorExecutionResultDto}.
     */
    /* package */ String COMPILED_ERROR_STRING_VALUE = "COMPILE_ERROR";
    /**
     * Value that marks a JSON to be deserialized into a {@link InitializationErrorExecutionResultDto}.
     */
    /* package */ String INITIALIZATION_ERROR_STRING_VALUE = "INITIALIZATION_ERROR";
    /**
     * Value that marks a JSON to be deserialized into a {@link UnknownErrorExecutionResultDto}.
     */
    /* package */ String UNKNOWN_ERROR_STRING_VALUE = "UNKNOWN_ERROR";

    /**
     * Returns the {@link ExecutionResult} subtype being adapted by implementors of this interface.
     *
     * @return The {@link ExecutionResult} subtype being adapted by implementors of this interface.
     */
    R getAdapted();
}
