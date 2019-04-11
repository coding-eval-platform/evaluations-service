package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.ExerciseDownloadDto;
import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.ExerciseUploadDto;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import com.bellotapps.webapps_commons.config.JerseyController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.stream.Collectors;

/**
 * Rest adapter of {@link ExamService},
 * encapsulating {@link ar.edu.itba.cep.evaluations_service.models.Exercise} management.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class ExerciseEndpoint {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseEndpoint.class);

    /**
     * The {@link ExamService} being wrapped.
     */
    private final ExamService examService;

    /**
     * Constructor.
     *
     * @param examService The {@link ExamService} being wrapped.
     */
    @Autowired
    public ExerciseEndpoint(final ExamService examService) {
        this.examService = examService;
    }


    @GET
    @Path(Routes.EXAM_EXERCISES)
    public Response getExamExercises(@SuppressWarnings("RSReferenceInspection") @PathParam("examId") final long examId) {
        LOGGER.debug("Getting exercises for exam with id {}", examId);
        final var exercises = examService.getExercises(examId).stream()
                .map(ExerciseDownloadDto::new)
                .collect(Collectors.toList());
        return Response.ok(exercises).build();
    }

    @DELETE
    @Path(Routes.EXAM_EXERCISES)
    public Response deleteExamExercises(@SuppressWarnings("RSReferenceInspection") @PathParam("examId") final long examId) {
        LOGGER.debug("Deleting exercises for exam with id {}", examId);
        examService.clearExercises(examId);
        return Response.noContent().build();
    }

    @POST
    @Path(Routes.EXAM_EXERCISES)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExerciseForExam(
            @Context final UriInfo uriInfo,
            @SuppressWarnings("RSReferenceInspection") @PathParam("examId") final long examId,
            @Valid @ConvertGroup(to = ExerciseUploadDto.Create.class) final ExerciseUploadDto dto) {
        LOGGER.debug("Creating exercise for exam with id {}", examId);
        final var exercise = examService.createExercise(examId, dto.getQuestion());
        final var location = uriInfo.getAbsolutePathBuilder()
                .path(Long.toString(exercise.getId()))
                .build();
        return Response.created(location).build();
    }

    @PUT
    @Path(Routes.EXERCISE_QUESTION)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeQuestion(
            @SuppressWarnings("RSReferenceInspection") @PathParam("exerciseId") final long exerciseId,
            @Valid @ConvertGroup(to = ExerciseUploadDto.ChangeQuestion.class) final ExerciseUploadDto dto) {
        LOGGER.debug("Changing question for exercise with id {}", exerciseId);
        examService.changeExerciseQuestion(exerciseId, dto.getQuestion());
        return Response.noContent().build();
    }

    @DELETE
    @Path(Routes.EXERCISE)
    public Response deleteExercise(
            @SuppressWarnings("RSReferenceInspection") @PathParam("exerciseId") final long exerciseId) {
        LOGGER.debug("Deleting exercise with id {}", exerciseId);
        examService.deleteExercise(exerciseId);
        return Response.noContent().build();
    }
}
