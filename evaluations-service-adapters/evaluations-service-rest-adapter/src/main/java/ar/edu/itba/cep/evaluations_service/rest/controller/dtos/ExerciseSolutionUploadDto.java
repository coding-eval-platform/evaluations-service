package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for receiving {@link ExerciseSolution}s data from an API consumer.
 */
public class ExerciseSolutionUploadDto {

    /**
     * The answer for the exercise's question.
     */
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
    public interface Modify {
    }
}
