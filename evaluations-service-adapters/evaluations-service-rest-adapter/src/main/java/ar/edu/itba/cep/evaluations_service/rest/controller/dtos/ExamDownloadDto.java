package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.rest.controller.data_transfer.Java8ISOLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Data transfer object for sending an {@link Exam}s data to an API consumer.
 */
public class ExamDownloadDto {

    /**
     * The {@link Exam}'s id.
     */
    private final long id;

    /**
     * The description of the exam (e.g mid-term exams, final exams, etc.).
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
     * The exam's {@link Exam.State} (i.e upcoming, in progress or finished).
     */
    private final Exam.State state;

    /**
     * The actual {@link Instant} at which the exam really started.
     */
    private final Instant actualStartingMoment;

    /**
     * The actual {@link Duration} of the exam.
     */
    private final Duration actualDuration;


    /**
     * Constructor.
     *
     * @param exam The {@link Exam} whose data will be transferred.
     */
    public ExamDownloadDto(final Exam exam) {
        this.id = exam.getId();
        this.description = exam.getDescription();
        this.startingAt = exam.getStartingAt();
        this.duration = exam.getDuration();
        this.state = exam.getState();
        this.actualStartingMoment = exam.getActualStartingMoment();
        this.actualDuration = exam.getActualDuration();
    }


    /**
     * @return The {@link Exam}'s id.
     */
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    public long getId() {
        return id;
    }

    /**
     * @return The description for the exam (e.g mid-term exams, final exams, etc.).
     */
    @JsonProperty(value = "description", access = JsonProperty.Access.READ_ONLY)
    public String getDescription() {
        return description;
    }

    /**
     * @return {@link LocalDateTime} at which the exam starts.
     */
    @JsonProperty(value = "startingAt", access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = Java8ISOLocalDateTimeSerializer.class)
    public LocalDateTime getStartingAt() {
        return startingAt;
    }

    /**
     * @return {@link Duration} of the exam.
     */
    @JsonProperty(value = "duration", access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = DurationSerializer.class)
    public Duration getDuration() {
        return duration;
    }

    /**
     * @return The exam's {@link Exam.State} (i.e upcoming, in progress or finished).
     */
    @JsonProperty(value = "state", access = JsonProperty.Access.READ_ONLY)
    public Exam.State getState() {
        return state;
    }

    /**
     * @return The actual {@link Instant} at which the exam really started.
     */
    @JsonProperty(value = "actualStartingMoment", access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = InstantSerializer.class)
    public Instant getActualStartingMoment() {
        return actualStartingMoment;
    }

    /**
     * @return The actual {@link Duration} of the exam.
     */
    @JsonProperty(value = "actualDuration", access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = DurationSerializer.class)
    public Duration getActualDuration() {
        return actualDuration;
    }
}
