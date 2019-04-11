package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.ValidationConstants;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.IllegalValue;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Data transfer object for receiving {@link ExerciseSolution}s data from an API consumer.
 */
public class ExerciseSolutionUploadDto {

    /**
     * The answer for the exercise's question.
     */
    @NotNull(message = "The answer is missing.", payload = MissingValue.class,
            groups = {
                    Create.class,
            }
    )
    @Size(message = "Answer too short", payload = IllegalValue.class,
            min = ValidationConstants.ANSWER_MIN_LENGTH,
            groups = {
                    Create.class,
            }
    )
    private final String answer;


    /**
     * Constructor.
     *
     * @param answer The answer for the exercise's question.
     */
    @JsonCreator
    public ExerciseSolutionUploadDto(
            @JsonProperty(
                    value = "answer",
                    access = JsonProperty.Access.WRITE_ONLY
            ) final String answer) {
        this.answer = answer;
    }


    /**
     * @return The answer for the exercise's question.
     */
    public String getAnswer() {
        return answer;
    }


    // ================================================================================================================
    // Validation groups
    // ================================================================================================================

    /**
     * Validation group for the create operation.
     */
    public interface Create {
    }
}
