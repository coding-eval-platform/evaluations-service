package ar.edu.itba.cep.evaluations_service.models;

import com.bellotapps.webapps_commons.errors.ConstraintViolationError;
import com.bellotapps.webapps_commons.exceptions.CustomConstraintViolationException;
import com.bellotapps.webapps_commons.validation.annotations.ValidateConstraintsAfter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Represents an exercise.
 */
public class Exercise {

    /**
     * The exercise's id.
     */
    private final long id;
    /**
     * The question being asked.
     */
    @NotNull(message = "The question is missing",
            payload = ConstraintViolationError.ErrorCausePayload.MissingValue.class)
    @Size(min = ValidationConstants.QUESTION_MIN_LENGTH,
            message = "Description too short",
            payload = ConstraintViolationError.ErrorCausePayload.IllegalValue.class)
    private String question;
    /**
     * The {@link Exam} to which this exercise belongs to.
     */
    @NotNull(message = "The exam is missing",
            payload = ConstraintViolationError.ErrorCausePayload.MissingValue.class)
    private final Exam belongsTo;


    /**
     * Constructor.
     *
     * @param question  The question being asked.
     * @param belongsTo The {@link Exam} to which this exercise belongs to.
     * @throws CustomConstraintViolationException If any argument is not valid.
     */
    @ValidateConstraintsAfter
    public Exercise(final String question, final Exam belongsTo) throws CustomConstraintViolationException {
        this.belongsTo = belongsTo;
        this.id = 0;
        this.question = question;
    }


    /**
     * @return The exercise's id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The question being asked.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @return The {@link Exam} to which this exercise belongs to.
     */
    public Exam belongsToExam() {
        return belongsTo;
    }


    /**
     * Changes the question for this exercise.
     *
     * @param question The new question for the exercise.
     * @throws CustomConstraintViolationException If the given {@code question} is not valid.
     */
    @ValidateConstraintsAfter
    public void setQuestion(final String question) throws CustomConstraintViolationException {
        this.question = question;
    }


    // ================================
    // equals, hashcode and toString
    // ================================

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exercise)) {
            return false;
        }
        final var exercise = (Exercise) o;
        return id == exercise.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Exercise [" +
                "ID: " + id + ", " +
                "Question: '" + question + "', " +
                "BelongsTo: " + belongsTo +
                "]";
    }
}
