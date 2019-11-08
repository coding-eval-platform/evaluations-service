package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

/**
 * Data transfer object for receiving {@link ExerciseSolution}s data from an API consumer.
 */
@Getter
public class ExerciseSolutionUploadDto {

    /**
     * The answer for the exercise's question.
     */
    private final String answer;
    /**
     * The compiler flags for the solution.
     */
    private final String compilerFlags;
    /**
     * The name of the file in which the "main" will be placed (i.e the name of the file where the code will be copied).
     */
    private final String mainFileName;


    /**
     * Constructor.
     *
     * @param answer        The answer for the exercise's question.
     * @param compilerFlags The compiler flags for the solution.
     * @param mainFileName  The name of the file in which the "main" will be placed
     *                      (i.e the name of the file where the code will be copied).
     */
    @JsonCreator
    public ExerciseSolutionUploadDto(
            @JsonProperty(value = "answer", access = WRITE_ONLY) final String answer,
            @JsonProperty(value = "compilerFlags", access = WRITE_ONLY) final String compilerFlags,
            @JsonProperty(value = "mainFileName", access = WRITE_ONLY) final String mainFileName) {
        this.answer = answer;
        this.compilerFlags = compilerFlags;
        this.mainFileName = mainFileName;
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
