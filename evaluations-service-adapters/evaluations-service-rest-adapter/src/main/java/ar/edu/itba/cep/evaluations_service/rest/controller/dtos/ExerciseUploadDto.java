package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ValidationConstants;
import ar.edu.itba.cep.executor.models.Language;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.IllegalValue;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Data transfer object for receiving {@link Exercise}s data from an API consumer.
 */
@Getter
public class ExerciseUploadDto {

    /**
     * The question for the exercise.
     */
    @NotNull(message = "The question is missing.", payload = MissingValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    @Size(message = "Question too short", payload = IllegalValue.class,
            min = ValidationConstants.QUESTION_MIN_LENGTH,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final String question;

    /**
     * The {@link Language} for the exercise.
     */
    @NotNull(message = "The language is missing.", payload = MissingValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final Language language;

    /**
     * The solution template for the exercise.
     */
    private final String solutionTemplate;

    @Positive(message = "The awarded score must be positive.", payload = IllegalValue.class,
            groups = {
                    Create.class,
                    Update.class,
            }
    )
    private final Integer awardedScore;


    /**
     * Constructor.
     *
     * @param question         The question for the exercise.
     * @param language         The {@link Language} for the exercise.
     * @param solutionTemplate The solution template for the exercise.
     * @param awardedScore     The awarded score for the exercise.
     */
    @JsonCreator
    public ExerciseUploadDto(
            @JsonProperty(
                    value = "question",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final String question,
            @JsonProperty(
                    value = "language",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final Language language,
            @JsonProperty(
                    value = "solutionTemplate",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final String solutionTemplate,
            @JsonProperty(
                    value = "awardedScore",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final int awardedScore) {
        this.question = question;
        this.language = language;
        this.solutionTemplate = solutionTemplate;
        this.awardedScore = awardedScore;
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
