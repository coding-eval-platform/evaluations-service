package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.rest.controller.data_transfer.Java8DurationToMinutesSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Data transfer object for {@link Exam}s.
 */
/* package */ abstract class ExamDownloadDto<W> {

    /**
     * The wrapper of an {@link Exam} to be, in turn, wrapped in this DTO.
     */
    private final W examWrapper;


    /**
     * Constructor.
     *
     * @param examWrapper The wrapper of an {@link Exam} to be, in turn, wrapped in this DTO.
     */
    /* package */ ExamDownloadDto(final W examWrapper) {
        this.examWrapper = examWrapper;
    }


    /**
     * @return The wrapper of an {@link Exam} to be, in turn, wrapped in this DTO.
     */
    /* package */ W getExamWrapper() {
        return examWrapper;
    }


    /**
     * @return The {@link Exam}'s id.
     */
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    public abstract long getId();

    /**
     * @return The description for the exam (e.g mid-term exams, final exams, etc.).
     */
    @JsonProperty(value = "description", access = JsonProperty.Access.READ_ONLY)
    public abstract String getDescription();

    /**
     * @return {@link LocalDateTime} at which the exam starts.
     */
    @JsonProperty(value = "startingAt", access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = Constants.STARTING_AT_DATE_PATTERN, timezone = Constants.STARTING_AT_TIME_ZONE)
    public abstract LocalDateTime getStartingAt();

    /**
     * @return {@link Duration} of the exam.
     */
    @JsonProperty(value = "duration", access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = Java8DurationToMinutesSerializer.class)
    public abstract Duration getDuration();

    /**
     * @return The exam's {@link Exam.State} (i.e upcoming, in progress or finished).
     */
    @JsonProperty(value = "state", access = JsonProperty.Access.READ_ONLY)
    public abstract Exam.State getState();

    /**
     * @return The actual {@link Instant} at which the exam really started.
     */
    @JsonProperty(value = "actualStartingMoment", access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = Constants.STARTING_AT_DATE_PATTERN, timezone = Constants.STARTING_AT_TIME_ZONE)
    public abstract Instant getActualStartingMoment();

    /**
     * @return The actual {@link Duration} of the exam.
     */
    @JsonProperty(value = "actualDuration", access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = Java8DurationToMinutesSerializer.class)
    public abstract Duration getActualDuration();
}
