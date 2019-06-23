package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * Represents an {@link ExecutionResult} for an execution of a code that did not compiled
 * (i.e only for compiled languages).
 */
@Getter
@ToString(doNotUseGetters = true, callSuper = true)
public class CompileErrorExecutionResult implements ExecutionResult {

    /**
     * A {@link List} of {@link String}s that were reported by the compiler on failure.
     */
    private final List<String> compilerErrors;


    /**
     * Constructor.
     *
     * @param compilerErrors A {@link List} of {@link String}s that were reported by the compiler on failure.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public CompileErrorExecutionResult(final List<String> compilerErrors) throws IllegalArgumentException {
        assertCompilerErrors(compilerErrors);

        // TODO: assert language in the ExamManager when checking the exercise

        this.compilerErrors = compilerErrors;
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code compilerErrors} {@link List} is valid.
     *
     * @param compilerErrors The {@link List} with compiler errors to be validated.
     * @throws IllegalArgumentException If the {@link List} is not valid.
     */
    private static void assertCompilerErrors(final List<String> compilerErrors) throws IllegalArgumentException {
        Assert.notNull(compilerErrors, "The compiler errors list must not be null");
        Assert.isTrue(
                compilerErrors.stream().noneMatch(Objects::isNull),
                "The compiler error list must not contain nulls."
        );
    }
}
