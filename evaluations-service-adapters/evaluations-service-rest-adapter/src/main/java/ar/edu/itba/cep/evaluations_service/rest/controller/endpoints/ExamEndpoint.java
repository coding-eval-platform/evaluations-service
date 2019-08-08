package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.ExamDownloadDto;
import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.ExamUploadDto;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import com.bellotapps.webapps_commons.config.JerseyController;
import com.bellotapps.webapps_commons.data_transfer.jersey.annotations.PaginationParam;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
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

/**
 * Rest Adapter of {@link ExamService},
 * encapsulating {@link ar.edu.itba.cep.evaluations_service.models.Exam} management.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class ExamEndpoint {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExamEndpoint.class);

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
    public ExamEndpoint(final ExamService examService) {
        this.examService = examService;
    }


    @GET
    @Path(Routes.EXAMS)
    public Response listExams(@PaginationParam final PagingRequest pagingRequest) {
        LOGGER.debug("Getting exams");
        final var exams = examService.listExams(pagingRequest).map(ExamDownloadDto::new);
        return Response.ok(exams.content()).build();
    }

    @GET
    @Path(Routes.EXAM)
    public Response getExamById(@PathParam("examId") final long examId) {
        LOGGER.debug("Getting exam with id {}", examId);
        return examService.getExam(examId)
                .map(ExamDownloadDto::new)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(""))
                .build();
    }

    @POST
    @Path(Routes.EXAMS)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExam(
            @Context final UriInfo uriInfo,
            @Valid @ConvertGroup(to = ExamUploadDto.Create.class) final ExamUploadDto dto) {
        LOGGER.debug("Creating new exam");
        final var exam = examService.createExam(
                dto.getDescription(),
                dto.getStartingAt(),
                dto.getDuration()
        );
        final var location = uriInfo.getAbsolutePathBuilder()
                .path(Long.toString(exam.getId()))
                .build();
        return Response.created(location).build();
    }

    @PUT
    @Path(Routes.EXAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyExam(
            @PathParam("examId") final long examId,
            @Valid @ConvertGroup(to = ExamUploadDto.Update.class) final ExamUploadDto dto) {
        LOGGER.debug("Updating exam with id {}", examId);
        examService.modifyExam(examId, dto.getDescription(), dto.getStartingAt(), dto.getDuration());
        return Response.noContent().build();
    }

    @PUT
    @Path(Routes.EXAM_START)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response startExam(@PathParam("examId") final long examId) {
        LOGGER.debug("Starting exam with id {}", examId);
        examService.startExam(examId);
        return Response.noContent().build();
    }

    @PUT
    @Path(Routes.EXAM_FINISH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finishExam(@PathParam("examId") final long examId) {
        LOGGER.debug("Finishing exam with id {}", examId);
        examService.finishExam(examId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(Routes.EXAM)
    public Response deleteExam(@PathParam("examId") final long examId) {
        LOGGER.debug("Deleting exam with id {}", examId);
        examService.deleteExam(examId);
        return Response.noContent().build();
    }
}
