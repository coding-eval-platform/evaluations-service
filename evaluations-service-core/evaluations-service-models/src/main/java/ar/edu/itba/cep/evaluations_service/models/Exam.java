package ar.edu.itba.cep.evaluations_service.models;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents an exam.
 */
public class Exam {

    /**
     * The exam's id.
     */
    private final long id;

    /**
     * A description for the exam (e.g mid-term exams, final exams, etc.).
     */
    private final String description;

    /**
     * {@link LocalDateTime} at which the exam starts.
     */
    private final LocalDateTime startingAt;

    /**
     * {@link Duration} of the exam.
     */
    private final Duration duration;


    /**
     * Constructor.
     *
     * @param description A description for the exam (e.g mid-term exams, final exams, etc.).
     * @param startingAt  {@link LocalDateTime} at which the exam starts.
     * @param duration    {@link Duration} of the exam.
     */
    public Exam(final String description, final LocalDateTime startingAt, final Duration duration) {
        this.id = 0;
        this.description = description;
        this.startingAt = startingAt;
        this.duration = duration;
    }


    /**
     * @return The exam's id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return A description for the exam (e.g mid-term exams, final exams, etc.).
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return {@link LocalDateTime} at which the exam starts.
     */
    public LocalDateTime getStartingAt() {
        return startingAt;
    }

    /**
     * @return {@link Duration} of the exam.
     */
    public Duration getDuration() {
        return duration;
    }
}
