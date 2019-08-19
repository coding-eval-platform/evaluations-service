package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.services.ExamWithoutOwners;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Data transfer object for sending an {@link Exam}s data to an API consumer, without exposing owners.
 */
public class NoOwnersExamDownloadDto extends ExamDownloadDto<ExamWithoutOwners> {

    /**
     * Constructor.
     *
     * @param examWrapper The wrapper of an {@link Exam} to be, in turn, wrapped in this DTO.
     */
    public NoOwnersExamDownloadDto(final ExamWithoutOwners examWrapper) {
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
}
