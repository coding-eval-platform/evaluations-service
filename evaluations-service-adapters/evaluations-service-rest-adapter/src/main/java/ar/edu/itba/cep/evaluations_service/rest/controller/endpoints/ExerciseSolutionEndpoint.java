package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.ExerciseSolutionDownloadDto;
import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.ExerciseSolutionUploadDto;
import ar.edu.itba.cep.evaluations_service.services.SolutionService;
import com.bellotapps.webapps_commons.config.JerseyController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

/**
 * Rest Adapter of {@link SolutionService}, encapsulating {@link ExerciseSolution} management.
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
     * The {@link SolutionService} being wrapped.
     */
    private final SolutionService solutionService;

    /**
     * Constructor.
     *
     * @param solutionService The {@link SolutionService} being wrapped.
     */
    @Autowired
    public ExerciseSolutionEndpoint(final SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    @GET
    @Path(Routes.SOLUTIONS)
    public Response listSolutionsForSubmission(@PathParam("submissionId") final long submissionId) {
        LOGGER.debug("Getting solutions for submission with id {}", submissionId);
        final var solutions = solutionService.getSolutionsForSubmission(submissionId)
                .stream()
                .map(ExerciseSolutionDownloadDto::new)
                .collect(Collectors.toList());
        return Response.ok(solutions).build();
    }

    @GET
    @Path(Routes.SOLUTION)
    public Response getSolution(@PathParam("solutionId") final long solutionId) {
        LOGGER.debug("Getting solution with id {}", solutionId);
        return solutionService.getSolution(solutionId)
                .map(ExerciseSolutionDownloadDto::new)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(""))
                .build();
    }

    @PUT
    @Path(Routes.SOLUTION)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSolution(
            @PathParam("solutionId") final long solutionId,
            @Valid @ConvertGroup(to = ExerciseSolutionUploadDto.Modify.class) final ExerciseSolutionUploadDto dto) {
        LOGGER.debug("Updating solution with id {}", solutionId);
        solutionService.modifySolution(solutionId, dto.getAnswer(), dto.getCompilerFlags(), dto.getMainFileName());
        return Response.noContent().build();
    }
}
