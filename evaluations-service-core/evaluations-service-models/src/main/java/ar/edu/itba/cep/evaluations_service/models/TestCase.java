package ar.edu.itba.cep.evaluations_service.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a test case for an {@link Exercise}.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(doNotUseGetters = true, callSuper = true)
public class TestCase {

    /**
     * The test case's id.
     */
    private final long id;

    /**
     * The program arguments of the test case.
     */
    private List<String> programArguments;

    /**
     * The elements to be passed to the standard input.
     */
    private List<String> stdin;

    /**
     * The expected outputs.
     */
    private List<String> expectedOutputs;

    /**
     * Indicates whether the test case is public or private.
     */
    private Visibility visibility;

    /**
     * The time given to the exercise to execute, in milliseconds.
     */
    private Long timeout;

    /**
     * The {@link Exercise} to which this test case belongs to.
     */
    private final Exercise exercise;


    /**
     * Default constructor.
     */
    /* package */ TestCase() {
        // Initialize final fields with default values.
        this.id = 0;
        this.programArguments = null;
        this.stdin = null;
        this.expectedOutputs = null;
        this.exercise = null;
    }

    /**
     * Constructor.
     *
     * @param visibility       Indicates whether the test case is public or private.
     * @param timeout          The time given to the exercise to execute, in milliseconds.
     * @param programArguments The inputs of the test case.
     * @param stdin            The elements to be passed to the standard input.
     * @param expectedOutputs  The expected outputs.
     * @param exercise         The {@link Exercise} to which this test case belongs to.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public TestCase(
            final Visibility visibility,
            final Long timeout,
            final List<String> programArguments,
            final List<String> stdin,
            final List<String> expectedOutputs,
            final Exercise exercise)
            throws IllegalArgumentException {
        assertVisibility(visibility);
        assertTimeout(timeout);
        assertProgramArgumentsList(programArguments);
        assertStdin(stdin);
        assertExpectedOutputsList(expectedOutputs);
        assertExercise(exercise);
        this.id = 0;
        this.visibility = visibility;
        this.timeout = timeout;
        this.programArguments = Optional.ofNullable(programArguments).map(LinkedList::new).orElse(null);
        this.stdin = Optional.ofNullable(stdin).map(LinkedList::new).orElse(null);
        this.expectedOutputs = Optional.ofNullable(expectedOutputs).map(LinkedList::new).orElse(null);
        this.exercise = exercise;
    }


    /**
     * Updates all fields of this test case.
     *
     * @param visibility       Indicates whether the test case is public or private.
     * @param timeout          The time given to the exercise to execute, in milliseconds.
     * @param programArguments The new {@link List} of program arguments for this test case.
     * @param stdin            The new stdin {@link List}.
     * @param expectedOutputs  The new {@link List} of outputs for this test case.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public void update(
            final TestCase.Visibility visibility,
            final Long timeout,
            final List<String> programArguments,
            final List<String> stdin,
            final List<String> expectedOutputs) throws IllegalArgumentException {
        assertVisibility(visibility);
        assertTimeout(timeout);
        assertProgramArgumentsList(programArguments);
        assertStdin(stdin);
        assertExpectedOutputsList(expectedOutputs);
        this.visibility = visibility;
        this.timeout = timeout;
        this.programArguments = Optional.ofNullable(programArguments).map(LinkedList::new).orElse(null);
        this.stdin = Optional.ofNullable(stdin).map(LinkedList::new).orElse(null);
        this.expectedOutputs = Optional.ofNullable(expectedOutputs).map(LinkedList::new).orElse(null);
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code visibility} is valid.
     *
     * @param visibility The {@link Visibility} to be checked.
     * @throws IllegalArgumentException If the visibility is not valid.
     */
    private static void assertVisibility(final Visibility visibility) throws IllegalArgumentException {
        Assert.notNull(visibility, "The visibility is missing");
    }

    /**
     * Asserts that the given {@code timeout} is valid.
     *
     * @param timeout The timeout to be checked.
     * @throws IllegalArgumentException If the {@code timeout} is not valid.
     */
    private static void assertTimeout(final Long timeout) throws IllegalArgumentException {
        Assert.isTrue(timeout == null || timeout > 0, "The timeout must be null or positive");
    }

    /**
     * Asserts that the given {@code programArguments} {@link List} is valid.
     *
     * @param programArguments The programArguments {@link List} to be checked.
     * @throws IllegalArgumentException If the programArguments {@link List} is not valid.
     */
    private static void assertProgramArgumentsList(final List<String> programArguments) throws IllegalArgumentException {
        Assert.isTrue(
                Objects.isNull(programArguments) || programArguments.stream().noneMatch(Objects::isNull),
                "The list must not contain null elements"
        );
    }

    /**
     * Asserts that the given {@code stdin} {@link List} is valid.
     *
     * @param stdin The stdin {@link List} to be checked.
     * @throws IllegalArgumentException If the stdin {@link List} is not valid.
     */
    private static void assertStdin(final List<String> stdin) throws IllegalArgumentException {
        Assert.isTrue(
                Objects.isNull(stdin) || stdin.stream().noneMatch(Objects::isNull),
                "The list must not contain null elements"
        );
    }

    /**
     * Asserts that the given {@code outputs} {@link List} is valid.
     *
     * @param outputs The outputs {@link List} to be checked.
     * @throws IllegalArgumentException If the outputs {@link List} is not valid.
     */
    private static void assertExpectedOutputsList(final List<String> outputs) throws IllegalArgumentException {
        Assert.notNull(outputs, "The expected outputs list is missing");
        Assert.notEmpty(outputs,
                "The list must not be empty." +
                        " To clear the expected outputs list use TestCase#removeAllExpectedOutputs");
        Assert.isTrue(outputs.stream().noneMatch(Objects::isNull), "The list must not contain null elements");
    }

    /**
     * Asserts that the given {@code exercise} is valid.
     *
     * @param exercise The {@link Exercise} to be checked.
     * @throws IllegalArgumentException If the exercise is not valid.
     */
    private static void assertExercise(final Exercise exercise) throws IllegalArgumentException {
        Assert.notNull(exercise, "The exercise is missing");
    }

    // ================================
    // Helpers
    // ================================


    /**
     * An enum holding visibility values (i.e indicate whether the test case is public or private).
     */
    public enum Visibility {
        /**
         * Indicates that the test case is public
         * (i.e can be seen by the student sitting for the exam).
         */
        PUBLIC,
        /**
         * Indicates that the test case is private
         * (i.e cannot be seen by the student sitting for the exam, and is used to actually evaluate this student).
         */
        PRIVATE,
        ;
    }
}
