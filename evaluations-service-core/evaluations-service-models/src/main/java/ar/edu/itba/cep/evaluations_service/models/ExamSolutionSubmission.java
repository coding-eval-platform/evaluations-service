package ar.edu.itba.cep.evaluations_service.models;

import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * Represents a solution submission for an {@link Exam}.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(doNotUseGetters = true, callSuper = true)
public class ExamSolutionSubmission {

    /**
     * The exam solution's id.
     */
    private final long id;
    /**
     * The {@link Exam} to which it belongs to.
     */
    private final Exam exam;
    /**
     * The one submitting the solution for the {@link Exam}.
     */
    private final String submitter;
    /**
     * The state of this {@link Exam} solution submission.
     */
    private State state;


    /**
     * Default constructor.
     */
    /* package */ ExamSolutionSubmission() {
        // Initialize final fields with default values.
        this.id = 0;
        this.exam = null;
        this.submitter = null;
    }

    /**
     * Constructor.
     *
     * @param exam      The {@link Exam} to which it belongs to.
     * @param submitter The one submitting the solution for the {@link Exam}.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public ExamSolutionSubmission(final Exam exam, final String submitter) throws IllegalArgumentException {
        assertExam(exam);
        assertSubmitter(submitter);
        this.id = 0;
        this.exam = exam;
        this.submitter = submitter;
        this.state = State.UNPLACED;
    }


    /**
     * Changes the state of this {@link Exam} solution submission to "submitted".
     *
     * @throws IllegalEntityStateException If the state is already "submitted".
     */
    public void submit() throws IllegalEntityStateException {
        if (state == State.SUBMITTED) {
            throw new IllegalEntityStateException(ALREADY_SUBMITTED);
        }
        this.state = State.SUBMITTED;
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code exam} is valid.
     *
     * @param exam The {@link Exam} to be checked.
     * @throws IllegalArgumentException If the exam is not valid.
     */
    private static void assertExam(final Exam exam) throws IllegalArgumentException {
        Assert.notNull(exam, "The exam is missing");
    }

    /**
     * Asserts that the given {@code submitter} is valid.
     *
     * @param submitter The submitter to be checked.
     * @throws IllegalArgumentException If the submitter is not valid.
     */
    private static void assertSubmitter(final String submitter) throws IllegalArgumentException {
        Assert.hasText(submitter, "The submitter must have text");
    }


    // ================================
    // ExamSolutionSubmission states
    // ================================

    /**
     * An enum containing the states in which an {@link ExamSolutionSubmission} can be:
     * unplaced (waiting for exercises to be completed), or submitted (all solutions are delivered).
     */
    public enum State {
        /**
         * The student still is completing exercises.
         */
        UNPLACED,
        /**
         * All exercises were solved and delivered.
         */
        SUBMITTED,
        ;
    }


    // ================================
    // Errors
    // ================================

    /**
     * Indicates that an {@link Exam} solution submission has been already placed.
     */
    private static final IllegalEntityStateError ALREADY_SUBMITTED =
            new IllegalEntityStateError("The submission has been already placed", "state");
}
