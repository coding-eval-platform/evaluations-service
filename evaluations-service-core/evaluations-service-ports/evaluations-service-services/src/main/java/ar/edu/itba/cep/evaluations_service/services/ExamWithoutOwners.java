package ar.edu.itba.cep.evaluations_service.services;

import ar.edu.itba.cep.evaluations_service.models.Exam;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Wraps an {@link Exam}, without exposing its owners.
 */
public class ExamWithoutOwners extends ExamWrapper {

    /**
     * Constructor.
     *
     * @param exam The {@link Exam} being wrapped.
     */
    public ExamWithoutOwners(final Exam exam) {
        super(exam);
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
     * @return The {@link Exam}'s real starting moment.
     */
    public Instant getActualStartingMoment() {
        return getExam().getActualStartingMoment();
    }

    /**
     * @return The {@link Exam}'s real duration.
     */
    public Duration getActualDuration() {
        return getExam().getActualDuration();
    }
}
