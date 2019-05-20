package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.Language;
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
     * The question of the exercise.
     */
    private final String question;

    /**
     * The {@link Language} of the exercise.
     */
    private final Language language;

    /**
     * The solution template of the exercise.
     */
    private final String solutionTemplate;


    /**
     * Constructor.
     *
     * @param exercise The {@link Exercise} whose data will be transferred.
     */
    public ExerciseDownloadDto(final Exercise exercise) {
        this.id = exercise.getId();
        this.question = exercise.getQuestion();
        this.language = exercise.getLanguage();
        this.solutionTemplate = exercise.getSolutionTemplate();
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

    /**
     * @return The {@link Language} of the exercise.
     */
    @JsonProperty(value = "language", access = JsonProperty.Access.READ_ONLY)
    public Language getLanguage() {
        return language;
    }

    /**
     * @return The solution template of the exercise.
     */
    @JsonProperty(value = "solutionTemplate", access = JsonProperty.Access.READ_ONLY)
    public String getSolutionTemplate() {
        return solutionTemplate;
    }
}
