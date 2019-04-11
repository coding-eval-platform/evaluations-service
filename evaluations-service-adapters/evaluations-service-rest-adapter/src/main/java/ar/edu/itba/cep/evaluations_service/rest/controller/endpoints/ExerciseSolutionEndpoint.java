package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.ExerciseSolutionDownloadDto;
import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.ExerciseSolutionUploadDto;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import com.bellotapps.webapps_commons.config.JerseyController;
import com.bellotapps.webapps_commons.data_transfer.jersey.annotations.PaginationParam;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Rest Adapter of {@link ExamService},
 * encapsulating {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolution} management.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class ExerciseSolutionEndpoint {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseSolutionEndpoint.class);

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
    public ExerciseSolutionEndpoint(final ExamService examService) {
        this.examService = examService;
    }

    @GET
    @Path(Routes.EXERCISE_SOLUTIONS)
    public Response listSolutions(
            @SuppressWarnings("RSReferenceInspection") @PathParam("exerciseId") final long exerciseId,
            @PaginationParam final PagingRequest pagingRequest) {
        LOGGER.debug("Getting solutions for exercise with id {}", exerciseId);
        final var solutions = examService.listSolutions(exerciseId, pagingRequest)
                .map(ExerciseSolutionDownloadDto::new);
        return Response.ok(solutions).build();
    }

    @POST
    @Path(Routes.EXERCISE_SOLUTIONS)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSolution(
            @Context final UriInfo uriInfo,
            @SuppressWarnings("RSReferenceInspection") @PathParam("exerciseId") final long exerciseId,
            @Valid final ExerciseSolutionUploadDto dto) {
        LOGGER.debug("Creating a solution for exercise with id {}", exerciseId);
        final var solution = examService.createExerciseSolution(exerciseId, dto.getAnswer());
        final var location = uriInfo.getAbsolutePathBuilder()
                .path(Long.toString(solution.getId()))
                .build();
        return Response.created(location).build();
    }
}
