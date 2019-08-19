package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.services.ExamWithOwners;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data transfer object for sending an {@link Exam}s data to an API consumer, exposing owners.
 */
public class WithOwnersExamDownloadDto extends ExamDownloadDto<ExamWithOwners> {

    /**
     * Constructor.
     *
     * @param examWrapper The wrapper of an {@link Exam} to be, in turn, wrapped in this DTO.
     */
    public WithOwnersExamDownloadDto(final ExamWithOwners examWrapper) {
        super(examWrapper);
    }

    @Override
    public long getId() {
        return getExamWrapper().getId();
    }

    @Override
    public String getDescription() {
        return getExamWrapper().getDescription();
    }

    @Override
    public LocalDateTime getStartingAt() {
        return getExamWrapper().getStartingAt();
    }

    @Override
    public Duration getDuration() {
        return getExamWrapper().getDuration();
    }

    @Override
    public Exam.State getState() {
        return getExamWrapper().getState();
    }

    @Override
    public Instant getActualStartingMoment() {
        return getExamWrapper().getActualStartingMoment();
    }

    @Override
    public Duration getActualDuration() {
        return getExamWrapper().getActualDuration();
    }

    /**
     * @return The owners of the exam.
     */
    @JsonProperty(value = "owners", access = JsonProperty.Access.READ_ONLY)
    public Set<String> getOwners() {
        return getExamWrapper().getOwners();
    }
}
