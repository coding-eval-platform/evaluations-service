package ar.edu.itba.cep.evaluations_service.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

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
    private String description;
    /**
     * {@link LocalDateTime} at which the exam starts.
     */
    private LocalDateTime startingAt;
    /**
     * {@link Duration} of the exam.
     */
    private Duration duration;


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


    /**
     * Changes the description for this exam.
     *
     * @param description The new description for the exam.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Changes the starting moment for this exam.
     *
     * @param startingAt The new {@link LocalDateTime} at which the exam starts.
     */
    public void setStartingAt(final LocalDateTime startingAt) {
        this.startingAt = startingAt;
    }

    /**
     * Changes the duration for this exam.
     *
     * @param duration The new {@link Duration} for the exam.
     */
    public void setDuration(final Duration duration) {
        this.duration = duration;
    }

    /**
     * Updates all fields of this exam.
     *
     * @param description The new description for the exam.
     * @param startingAt  The new {@link LocalDateTime} at which the exam starts.
     * @param duration    The new {@link Duration} for the exam.
     */
    public void update(final String description, final LocalDateTime startingAt, final Duration duration) {
        this.description = description;
        this.startingAt = startingAt;
        this.duration = duration;
    }


    // ================================
    // equals, hashcode and toString
    // ================================

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exam)) {
            return false;
        }
        final var exam = (Exam) o;
        return id == exam.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Exam: [" +
                "ID: " + id + ", " +
                "Description: '" + description + "', " +
                "StartingAt: " + startingAt + ", " +
                "Duration: " + duration +
                "]";
    }
}
