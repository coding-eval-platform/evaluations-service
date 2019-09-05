package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for sending an {@link ExerciseSolution}s data to an API consumer.
 */
public class ExerciseSolutionDownloadDto {

    /**
     * The {@link ExerciseSolution}'s id.
     */
    private final long id;

    /**
     * The answer to the exercise's question.
     */
    private final String answer;


    /**
     * Constructor.
     *
     * @param solution The {@link ExerciseSolution} whose data will be transferred.
     */
    public ExerciseSolutionDownloadDto(final ExerciseSolution solution) {
        this.id = solution.getId();
        this.answer = solution.getAnswer();
    }


    /**
     * @return The {@link ExerciseSolution}'s id.
     */
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    public long getId() {
        return id;
    }

    /**
     * @return The answer for the exercise's question.
     */
    @JsonProperty(value = "answer", access = JsonProperty.Access.READ_ONLY)
    public String getAnswer() {
        return answer;
    }
}
