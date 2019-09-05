package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.ExamSolutionsSubmissionDownloadDto;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import ar.edu.itba.cep.evaluations_service.services.SolutionService;
import com.bellotapps.webapps_commons.config.JerseyController;
import com.bellotapps.webapps_commons.data_transfer.jersey.annotations.PaginationParam;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Rest Adapter of {@link SolutionService}, encapsulating {@link ExamSolutionSubmission} management.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class ExamSolutionSubmissionEndpoint {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExamSolutionSubmissionEndpoint.class);

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
    public ExamSolutionSubmissionEndpoint(final SolutionService solutionService) {
        this.solutionService = solutionService;
    }


    @GET
    @Path(Routes.EXAM_SOLUTIONS_SUBMISSIONS_BY_EXAM)
    public Response listSubmissions(
            @PathParam("examId") final long examId,
            @PaginationParam final PagingRequest pagingRequest) {
        LOGGER.debug("Getting solutions submissions for exam with id {}", examId);
        final var submissions = solutionService.getSolutionSubmissionsForExam(examId, pagingRequest)
                .map(ExamSolutionsSubmissionDownloadDto::new)
                .content();
        return Response.ok(submissions).build();
    }

    @GET
    @Path(Routes.EXAM_SOLUTIONS_SUBMISSION)
    public Response getSubmissionById(@PathParam("submissionId") final long submissionId) {
        LOGGER.debug("Getting solutions submission with id {}", submissionId);
        return solutionService.getSubmission(submissionId)
                .map(ExamSolutionsSubmissionDownloadDto::new)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(""))
                .build();
    }

    @POST
    @Path(Routes.EXAM_SOLUTIONS_SUBMISSIONS_BY_EXAM)
    public Response createSolution(@Context final UriInfo uriInfo, @PathParam("examId") final long examId) {
        LOGGER.debug("Creating a solution submission for exam with id {}", examId);
        final var submission = solutionService.createExamSolutionSubmission(examId);
        final var location = uriInfo.getBaseUriBuilder()
                .path(Routes.EXAM_SOLUTIONS_SUBMISSIONS)
                .path(Long.toString(submission.getId()))
                .build();
        return Response.created(location).build();
    }

    @PUT
    @Path(Routes.SUBMIT_SOLUTION)
    public Response submitSolutions(
            @Context final UriInfo uriInfo,
            @PathParam("submissionId") final long submissionId) {
        LOGGER.debug("Submitting submission with id {}", submissionId);
        solutionService.submitSolutions(submissionId);
        return Response.noContent().build();
    }
}
