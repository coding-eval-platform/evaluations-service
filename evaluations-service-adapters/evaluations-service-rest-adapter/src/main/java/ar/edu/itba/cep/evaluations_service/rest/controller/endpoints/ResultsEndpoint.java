package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.SolutionResultDownloadDto;
import ar.edu.itba.cep.evaluations_service.services.ResultsService;
import com.bellotapps.webapps_commons.config.JerseyController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

/**
 * Rest Adapter of {@link ResultsService},
 * encapsulating {@link ar.edu.itba.cep.evaluations_service.models.ExerciseSolutionResult} management.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class ResultsEndpoint {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultsEndpoint.class);

    /**
     * The {@link ResultsService} being wrapped.
     */
    private final ResultsService resultsService;

    /**
     * Constructor.
     *
     * @param resultsService The {@link ResultsService} being wrapped.
     */
    @Autowired
    public ResultsEndpoint(final ResultsService resultsService) {
        this.resultsService = resultsService;
    }

    @GET
    @Path(Routes.SOLUTION_RESULTS)
    public Response getResultsFor(@PathParam("solutionId") final long solutionId) {
        LOGGER.debug("Getting results for solution with id {}", solutionId);

        final var results = resultsService.getResultsForSolution(solutionId)
                .stream()
                .map(SolutionResultDownloadDto::buildFor)
                .collect(Collectors.toList());
        return Response.ok(results).build();
    }

    @GET
    @Path(Routes.SOLUTION_TEST_CASE_RESULT)
    public Response getResultsFor(
            @PathParam("solutionId") final long solutionId,
            @PathParam("testCaseId") final long testCaseId) {
        LOGGER.debug("Getting result for solution with id {} and test case with id {}", solutionId, testCaseId);

        final var result = resultsService.getResultFor(solutionId, testCaseId);
        final var dto = SolutionResultDownloadDto.buildFor(result);
        return Response.ok(dto).build();
    }


    @PUT
    @Path(Routes.RETRY_SOLUTION_EXECUTION)
    public Response retryExecution(@PathParam("solutionId") final long solutionId) {
        LOGGER.debug("Retrying execution for solution with id {}", solutionId);
        resultsService.retryForSolution(solutionId);
        return Response.noContent().build();
    }

    @PUT
    @Path(Routes.RETRY_SOLUTION_TEST_CASE_EXECUTION)
    public Response retryExecution(
            @PathParam("solutionId") final long solutionId,
            @PathParam("testCaseId") final long testCaseId) {
        LOGGER.debug("Retrying execution for solution with id {} and test case with id {}", solutionId, testCaseId);
        resultsService.retryForSolutionAndTestCase(solutionId, testCaseId);
        return Response.noContent().build();
    }
}
