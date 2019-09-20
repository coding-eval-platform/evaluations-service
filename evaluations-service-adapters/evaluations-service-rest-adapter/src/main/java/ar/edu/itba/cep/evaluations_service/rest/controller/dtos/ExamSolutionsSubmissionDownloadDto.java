package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for sending an {@link ExamSolutionSubmission}s data to an API consumer.
 */
public class ExamSolutionsSubmissionDownloadDto {

    /**
     * The {@link ExamSolutionSubmission}'s id.
     */
    private final long id;
    /**
     * The {@link ExamSolutionSubmission}'s submitter.
     */
    private final String submitter;
    /**
     * The {@link ExamSolutionSubmission}'s score.
     */
    private final Integer score;


    /**
     * Constructor.
     *
     * @param submission The {@link ExamSolutionSubmission} whose data will be transferred.
     */
    public ExamSolutionsSubmissionDownloadDto(final ExamSolutionSubmission submission) {
        this.id = submission.getId();
        this.submitter = submission.getSubmitter();
        this.score = submission.getScore();
    }


    /**
     * @return The {@link ExamSolutionSubmission}'s id.
     */
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    public long getId() {
        return id;
    }

    /**
     * @return The {@link ExamSolutionSubmission}'s submitter.
     */
    @JsonProperty(value = "submitter", access = JsonProperty.Access.READ_ONLY)
    public String getSubmitter() {
        return submitter;
    }

    /**
     * @return The {@link ExamSolutionSubmission}'s score.
     */
    @JsonProperty(value = "score", access = JsonProperty.Access.READ_ONLY)
    public Integer getScore() {
        return score;
    }
}
