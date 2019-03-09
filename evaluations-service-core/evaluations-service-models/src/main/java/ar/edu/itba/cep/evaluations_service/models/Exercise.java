package ar.edu.itba.cep.evaluations_service.models;

import org.springframework.util.Assert;

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
    private String question;

    /**
     * The {@link Exam} to which this exercise belongs to.
     */
    private final Exam belongsTo;


    /**
     * Constructor.
     *
     * @param question  The question being asked.
     * @param belongsTo The {@link Exam} to which this exercise belongs to.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public Exercise(final String question, final Exam belongsTo) throws IllegalArgumentException {
        assertQuestion(question);
        assertExam(belongsTo);
        this.id = 0;
        this.question = question;
        this.belongsTo = belongsTo;
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
     * @throws IllegalArgumentException If the given {@code question} is not valid.
     */
    public void setQuestion(final String question) throws IllegalArgumentException {
        assertQuestion(question);
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


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code question} is valid.
     *
     * @param question The question to be checked.
     * @throws IllegalArgumentException If the question is not valid.
     */
    private static void assertQuestion(final String question) throws IllegalArgumentException {
        Assert.notNull(question, "The question is missing");
        Assert.isTrue(question.length() >= ValidationConstants.QUESTION_MIN_LENGTH,
                "The question is too short");
    }

    /**
     * Asserts that the given {@code exam} is valid.
     *
     * @param exam The {@link Exam} to be checked.
     * @throws IllegalArgumentException If the exam is not valid.
     */
    private static void assertExam(final Exam exam) throws IllegalArgumentException {
        Assert.notNull(exam, "The exam is missing");
    }
}
