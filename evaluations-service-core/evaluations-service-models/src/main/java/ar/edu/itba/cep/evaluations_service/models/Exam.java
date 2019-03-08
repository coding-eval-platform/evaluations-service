package ar.edu.itba.cep.evaluations_service.models;

import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;

import java.time.Duration;
import java.time.Instant;
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
     * The exam's {@link State} (i.e upcoming, in progress or finished).
     */
    private State state;
    /**
     * The actual {@link Instant} at which the exam really started.
     */
    private Instant actualStartingMoment;
    /**
     * The actual {@link Duration} of the exam.
     */
    private Duration actualDuration;


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
        this.state = State.UPCOMING;
        this.actualStartingMoment = null;
        this.actualDuration = null;
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
     * @return The exam's {@link State} (i.e upcoming, in progress or finished).
     */
    public State getState() {
        return state;
    }

    /**
     * @return The actual {@link Instant} at which the exam really started.
     */
    public Instant getActualStartingMoment() {
        return actualStartingMoment;
    }

    /**
     * @return The actual {@link Duration} of the exam.
     */
    public Duration getActualDuration() {
        return actualDuration;
    }

    /**
     * Changes the description for this exam.
     *
     * @param description The new description for the exam.
     * @throws IllegalEntityStateException If the exam cannot be updated because it's not in upcoming state.
     */
    public void setDescription(final String description) throws IllegalEntityStateException {
        verifyStateForUpdate();
        this.description = description;
    }

    /**
     * Changes the starting moment for this exam.
     *
     * @param startingAt The new {@link LocalDateTime} at which the exam starts.
     * @throws IllegalEntityStateException If the exam cannot be updated because it's not in upcoming state.
     */
    public void setStartingAt(final LocalDateTime startingAt) throws IllegalEntityStateException {
        verifyStateForUpdate();
        this.startingAt = startingAt;
    }

    /**
     * Changes the duration for this exam.
     *
     * @param duration The new {@link Duration} for the exam.
     * @throws IllegalEntityStateException If the exam cannot be updated because it's not in upcoming state.
     */
    public void setDuration(final Duration duration) throws IllegalEntityStateException {
        verifyStateForUpdate();
        this.duration = duration;
    }

    /**
     * Updates all fields of this exam.
     *
     * @param description The new description for the exam.
     * @param startingAt  The new {@link LocalDateTime} at which the exam starts.
     * @param duration    The new {@link Duration} for the exam.
     * @throws IllegalEntityStateException If the exam cannot be updated because it's not in upcoming state.
     */
    public void update(final String description, final LocalDateTime startingAt, final Duration duration)
            throws IllegalEntityStateException {
        verifyStateForUpdate();
        this.description = description;
        this.startingAt = startingAt;
        this.duration = duration;
    }

    /**
     * Starts the exam.
     *
     * @throws IllegalEntityStateException If the exam is not in {@link State#UPCOMING} state.
     */
    public void startExam() throws IllegalEntityStateException {
        if (this.state != State.UPCOMING) {
            throw new IllegalEntityStateException(UPCOMING_STATE_FOR_STARTING);
        }
        this.state = State.IN_PROGRESS;
        this.actualStartingMoment = Instant.now();
    }

    /**
     * Finishes the exam.
     *
     * @throws IllegalEntityStateException If the exam is not in {@link State#IN_PROGRESS} state.
     */
    public void finishExam() throws IllegalEntityStateException {
        if (this.state != State.IN_PROGRESS) {
            throw new IllegalEntityStateException(IN_PROGRESS_STATE_FOR_FINISHING);
        }
        this.state = State.FINISHED;
        this.actualDuration = Duration.between(actualStartingMoment, Instant.now());
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


    // ================================
    // Helpers
    // ================================

    /**
     * Checks whether the exam can be modified.
     *
     * @throws IllegalEntityStateException If the exam cannot be modified.
     */
    private void verifyStateForUpdate() throws IllegalEntityStateException {
        if (state != State.UPCOMING) {
            throw new IllegalEntityStateException(UPCOMING_STATE_FOR_MODIFICATIONS);
        }
    }


    // ================================
    // Exam states
    // ================================

    /**
     * An enum containing the different states in which an exam can be.
     */
    public enum State {
        /**
         * Indicates that an exam has not started yet.
         */
        UPCOMING,
        /**
         * Indicates that the exam is being taken right now.
         */
        IN_PROGRESS,
        /**
         * Indicates that the exam has already finished.
         */
        FINISHED,
        ;
    }


    // ================================
    // Errors
    // ================================

    /**
     * Indicates that an exam cannot be modified if its state is not "upcoming".
     */
    private static final IllegalEntityStateError UPCOMING_STATE_FOR_MODIFICATIONS =
            new IllegalEntityStateError("The exam must be upcoming to be modified", "state");

    /**
     * Indicates that an exam cannot be started if its state is not "upcoming".
     */
    private static final IllegalEntityStateError UPCOMING_STATE_FOR_STARTING =
            new IllegalEntityStateError("The exam must be upcoming to be started", "state");

    /**
     * Indicates that an exam cannot be finished if its state is not "in progress".
     */
    private static final IllegalEntityStateError IN_PROGRESS_STATE_FOR_FINISHING =
            new IllegalEntityStateError("The exam must be in progress to be finished", "state");
}
