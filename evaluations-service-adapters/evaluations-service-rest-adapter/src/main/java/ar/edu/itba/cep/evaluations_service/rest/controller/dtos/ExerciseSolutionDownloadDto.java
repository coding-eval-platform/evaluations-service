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
     * The id of the {@link ar.edu.itba.cep.evaluations_service.models.Exercise} the solutions belongs to.
     */
    private final long exerciseId;
    /**
     * The answer to the exercise's question.
     */
    private final String answer;
    /**
     * The compiler flags of the solution.
     */
    private final String compilerFlags;
    /**
     * The name of the file in which the "main" will be placed (i.e the name of the file where the code will be copied).
     */
    private final String mainFileName;


    /**
     * Constructor.
     *
     * @param solution The {@link ExerciseSolution} whose data will be transferred.
     */
    public ExerciseSolutionDownloadDto(final ExerciseSolution solution) {
        this.id = solution.getId();
        this.exerciseId = solution.getExercise().getId();
        this.answer = solution.getAnswer();
        this.compilerFlags = solution.getCompilerFlags();
        this.mainFileName = solution.getMainFileName();
    }


    /**
     * @return The {@link ExerciseSolution}'s id.
     */
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    public long getId() {
        return id;
    }

    /**
     * @return The id of the {@link ar.edu.itba.cep.evaluations_service.models.Exercise} the solutions belongs to.
     */
    @JsonProperty(value = "exerciseId", access = JsonProperty.Access.READ_ONLY)
    public long getExerciseId() {
        return exerciseId;
    }

    /**
     * @return The answer for the exercise's question.
     */
    @JsonProperty(value = "answer", access = JsonProperty.Access.READ_ONLY)
    public String getAnswer() {
        return answer;
    }

    /**
     * @return The compiler flags of the solution.
     */
    @JsonProperty(value = "compilerFlags", access = JsonProperty.Access.READ_ONLY)
    public String getCompilerFlags() {
        return compilerFlags;
    }

    /**
     * @return The name of the file in which the "main" will be placed
     * (i.e the name of the file where the code will be copied).
     */
    @JsonProperty(value = "mainFileName", access = JsonProperty.Access.READ_ONLY)
    public String getMainFileName() {
        return mainFileName;
    }
}
