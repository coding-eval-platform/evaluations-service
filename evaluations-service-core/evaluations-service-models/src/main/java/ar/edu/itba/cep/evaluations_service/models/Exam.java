package ar.edu.itba.cep.evaluations_service.models;


import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;


/**
 * Represents an exam.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(doNotUseGetters = true, callSuper = true)
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
     * Default constructor.
     */
    /* package */ Exam() {
        // Initialize final fields with default values.
        this.id = 0;
    }

    /**
     * Constructor.
     *
     * @param description A description for the exam (e.g mid-term exams, final exams, etc.).
     * @param startingAt  {@link LocalDateTime} at which the exam starts.
     * @param duration    {@link Duration} of the exam.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public Exam(final String description, final LocalDateTime startingAt, final Duration duration)
            throws IllegalArgumentException {
        assertDescription(description);
        assertStartingAt(startingAt);
        assertDuration(duration);
        this.id = 0;
        this.description = description;
        this.startingAt = startingAt;
        this.duration = duration;
        this.state = State.UPCOMING;
        this.actualStartingMoment = null;
        this.actualDuration = null;
    }


    /**
     * Changes the description for this exam.
     *
     * @param description The new description for the exam.
     * @throws IllegalEntityStateException If the exam cannot be updated because it's not in upcoming state.
     * @throws IllegalArgumentException    If the given {@code description} is not valid.
     */
    public void setDescription(final String description)
            throws IllegalEntityStateException, IllegalArgumentException {
        assertDescription(description);
        verifyStateForUpdate();
        this.description = description;
    }

    /**
     * Changes the starting moment for this exam.
     *
     * @param startingAt The new {@link LocalDateTime} at which the exam starts.
     * @throws IllegalEntityStateException If the exam cannot be updated because it's not in upcoming state.
     * @throws IllegalArgumentException    If the given {@code startingAt} {@link LocalDateTime} is not valid.
     */
    public void setStartingAt(final LocalDateTime startingAt)
            throws IllegalEntityStateException, IllegalArgumentException {
        assertStartingAt(startingAt);
        verifyStateForUpdate();
        this.startingAt = startingAt;
    }

    /**
     * Changes the duration for this exam.
     *
     * @param duration The new {@link Duration} for the exam.
     * @throws IllegalEntityStateException If the exam cannot be updated because it's not in upcoming state.
     * @throws IllegalArgumentException    If the given {@code duration} is not valid.
     */
    public void setDuration(final Duration duration)
            throws IllegalEntityStateException, IllegalArgumentException {
        assertDuration(duration);
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
     * @throws IllegalArgumentException    If any argument is not valid.
     */
    public void update(final String description, final LocalDateTime startingAt, final Duration duration)
            throws IllegalEntityStateException, IllegalArgumentException {
        assertDescription(description);
        assertStartingAt(startingAt);
        assertDuration(duration);
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
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code description} is valid.
     *
     * @param description The description to be checked.
     * @throws IllegalArgumentException If the description is not valid.
     */
    private static void assertDescription(final String description) throws IllegalArgumentException {
        Assert.notNull(description, "The description is missing");
        Assert.isTrue(description.length() >= ValidationConstants.DESCRIPTION_MIN_LENGTH,
                "The description is too short");
        Assert.isTrue(description.length() <= ValidationConstants.DESCRIPTION_MAX_LENGTH,
                "The description is too long");
    }

    /**
     * Asserts that the given {@code startingAt} {@link LocalDateTime} is valid.
     *
     * @param startingAt The {@link LocalDateTime} to be checked.
     * @throws IllegalArgumentException If the starting at {@link LocalDateTime} is not valid.
     */
    private static void assertStartingAt(final LocalDateTime startingAt) throws IllegalArgumentException {
        Assert.notNull(startingAt, "The starting moment is missing");
        Assert.isTrue(startingAt.isAfter(LocalDateTime.now()), "The starting moment must be in the future");
    }

    /**
     * Asserts that the given {@code duration} is valid.
     *
     * @param duration The duration to be checked.
     * @throws IllegalArgumentException If the duration is not valid.
     */
    private static void assertDuration(final Duration duration) throws IllegalArgumentException {
        Assert.notNull(duration, "The duration is missing");
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
