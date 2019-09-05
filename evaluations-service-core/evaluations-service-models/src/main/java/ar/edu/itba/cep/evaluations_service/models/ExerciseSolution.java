package ar.edu.itba.cep.evaluations_service.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Represents a solution of an exercise.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(doNotUseGetters = true, callSuper = true)
public class ExerciseSolution {

    /**
     * The exercise solution's id.
     */
    private final long id;
    /**
     * The {@link ExamSolutionSubmission} to which this solution is submitted to.
     */
    private final ExamSolutionSubmission submission;
    /**
     * The {@link Exercise} to which it belongs to.
     */
    private final Exercise exercise;
    /**
     * The answer to the question of the {@link Exercise} (i.e the code written by the student).
     */
    private String answer;


    /**
     * Default constructor.
     */
    /* package */ ExerciseSolution() {
        // Initialize final fields with default values.
        this.id = 0;
        this.submission = null;
        this.exercise = null;
        this.answer = null;
    }

    /**
     * Constructor.
     *
     * @param submission The {@link ExamSolutionSubmission} to which this solution is submitted to.
     * @param exercise   The {@link Exercise} to which it belongs to.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public ExerciseSolution(
            final ExamSolutionSubmission submission,
            final Exercise exercise) throws IllegalArgumentException {
        assertSubmission(submission);
        assertExercise(exercise);
        assertSameExam(submission, exercise);
        this.id = 0;
        this.submission = submission;
        this.exercise = exercise;
        this.answer = exercise.getSolutionTemplate();
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code submission} is valid.
     *
     * @param submission The {@link ExamSolutionSubmission} to be checked.
     * @throws IllegalArgumentException If the submission is not valid.
     */
    private static void assertSubmission(final ExamSolutionSubmission submission)
            throws IllegalArgumentException {
        Assert.notNull(submission, "The submission is missing");
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

    /**
     * Asserts that the given {@code submission} and {@code exercise} have the same {@link Exam}.
     *
     * @param submission The {@link ExamSolutionSubmission} to be checked.
     * @param exercise   The {@link Exercise} to be checked.
     * @throws IllegalArgumentException If the {@link Exam}
     *                                  of both the {@code submission} and {@code exercise} is the same.
     */
    private static void assertSameExam(final ExamSolutionSubmission submission, final Exercise exercise)
            throws IllegalArgumentException {
        Assert.isTrue(
                Objects.equals(submission.getExam(), exercise.getExam()),
                "The exam of the submission and the exercise must be the same"
        );
    }
}
