package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.Exercise;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for sending an {@link Exercise}s data to an API consumer.
 */
public class ExerciseDownloadDto {

    /**
     * The {@link Exercise}'s id.
     */
    private final long id;

    /**
     * The question for the exercise.
     */
    private final String question;


    /**
     * Constructor.
     *
     * @param exercise The {@link Exercise} whose data will be transferred.
     */
    public ExerciseDownloadDto(final Exercise exercise) {
        this.id = exercise.getId();
        this.question = exercise.getQuestion();
    }


    /**
     * @return The {@link Exercise}'s id.
     */
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    public long getId() {
        return id;
    }

    /**
     * @return The question for the exercise.
     */
    @JsonProperty(value = "question", access = JsonProperty.Access.READ_ONLY)
    public String getQuestion() {
        return question;
    }
}
