package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.ValidationConstants;
import ar.edu.itba.cep.evaluations_service.rest.controller.data_transfer.Java8DurationToMinutesDeserializer;
import ar.edu.itba.cep.evaluations_service.rest.controller.validation.PositiveDuration;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.IllegalValue;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Data transfer object for receiving {@link Exam}s data from an API consumer.
 */
@Getter
public class ExamUploadDto {

    /**
     * The description for the exam (e.g mid-term exams, final exams, etc.).
     */
    @NotNull(message = "The description is missing.", payload = MissingValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    @Size(message = "Description too short", payload = IllegalValue.class,
            min = ValidationConstants.DESCRIPTION_MIN_LENGTH,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    @Size(message = "Description too long", payload = IllegalValue.class,
            max = ValidationConstants.DESCRIPTION_MAX_LENGTH,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final String description;

    /**
     * {@link LocalDateTime} at which the exam starts.
     */
    @NotNull(message = "The startingAt value is missing", payload = MissingValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    @Future(message = "The starting moment must be in the future", payload = IllegalValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final LocalDateTime startingAt;

    /**
     * {@link Duration} of the exam.
     */
    @NotNull(message = "The duration is missing", payload = MissingValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    @PositiveDuration(message = "The duration must be positive", payload = IllegalValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final Duration duration;


    /**
     * Constructor.
     *
     * @param description The description for the exam (e.g mid-term exams, final exams, etc.).
     * @param startingAt  {@link LocalDateTime} at which the exam starts.
     * @param duration    {@link Duration} of the exam.
     */
    @JsonCreator
    public ExamUploadDto(
            @JsonProperty(value = "description", access = JsonProperty.Access.WRITE_ONLY) final String description,
            @JsonProperty(value = "startingAt", access = JsonProperty.Access.WRITE_ONLY) final LocalDateTime startingAt,
            @JsonProperty(value = "duration", access = JsonProperty.Access.WRITE_ONLY)
            @JsonDeserialize(using = Java8DurationToMinutesDeserializer.class) final Duration duration) {
        this.description = description;
        this.startingAt = startingAt;
        this.duration = duration;
    }


    // ================================================================================================================
    // Validation groups
    // ================================================================================================================

    /**
     * Validation group for the create operation.
     */
    public interface Create {
    }

    /**
     * Validation group for the update operation.
     */
    public interface Update {
    }
}
