package ar.edu.itba.cep.evaluations_service.services;

import ar.edu.itba.cep.evaluations_service.models.Exam;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Wraps an {@link Exam}, together with its maximum score.
 */
public class ExamWithScore extends ExamWrapper {

    /**
     * The max. amount of score for the wrapped {@link Exam}.
     */
    private final int maxScore;

    /**
     * Constructor.
     *
     * @param exam     The {@link Exam} being wrapped.
     * @param maxScore The max. amount of score for the wrapped {@link Exam}.
     */
    public ExamWithScore(final Exam exam, final int maxScore) {
        super(exam);
        this.maxScore = maxScore;
    }


    /**
     * @return The {@link Exam}'s id.
     */
    public long getId() {
        return getExam().getId();
    }

    /**
     * @return The {@link Exam}'s description.
     */
    public String getDescription() {
        return getExam().getDescription();
    }

    /**
     * @return The {@link Exam}'s starting moment.
     */
    public LocalDateTime getStartingAt() {
        return getExam().getStartingAt();
    }

    /**
     * @return The {@link Exam}'s duration.
     */
    public Duration getDuration() {
        return getExam().getDuration();
    }

    /**
     * @return The {@link Exam}'s state.
     */
    public Exam.State getState() {
        return getExam().getState();
    }

    /**
     * @return The max. amount of score for the wrapped {@link Exam}.
     */
    public int getMaxScore() {
        return maxScore;
    }

}
