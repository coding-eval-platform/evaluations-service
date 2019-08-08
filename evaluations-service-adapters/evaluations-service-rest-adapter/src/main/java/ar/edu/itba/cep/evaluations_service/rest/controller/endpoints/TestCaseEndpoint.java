package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.TestCaseDownloadDto;
import ar.edu.itba.cep.evaluations_service.rest.controller.dtos.TestCaseUploadDto;
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
 * encapsulating {@link ar.edu.itba.cep.evaluations_service.models.TestCase} management.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class TestCaseEndpoint {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseEndpoint.class);

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
    public TestCaseEndpoint(final ExamService examService) {
        this.examService = examService;
    }


    @GET
    @Path(Routes.EXERCISE_PUBLIC_TEST_CASES)
    public Response getPublicTestCases(
            @SuppressWarnings("RSReferenceInspection") @PathParam("exerciseId") final long exerciseId) {
        LOGGER.debug("Getting public test cases for exercise with id {}", exerciseId);
        final var testCases = examService.getPublicTestCases(exerciseId).stream()
                .map(TestCaseDownloadDto::new)
                .collect(Collectors.toList());
        return Response.ok(testCases).build();
    }

    @GET
    @Path(Routes.EXERCISE_PRIVATE_TEST_CASES)
    public Response getPrivateTestCases(
            @SuppressWarnings("RSReferenceInspection") @PathParam("exerciseId") final long exerciseId) {
        LOGGER.debug("Getting private test cases for exercise with id {}", exerciseId);
        final var testCases = examService.getPrivateTestCases(exerciseId).stream()
                .map(TestCaseDownloadDto::new)
                .collect(Collectors.toList());
        return Response.ok(testCases).build();
    }

    @POST
    @Path(Routes.EXERCISE_TEST_CASES)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTestCaseForExercise(
            @Context final UriInfo uriInfo,
            @SuppressWarnings("RSReferenceInspection") @PathParam("exerciseId") final long exerciseId,
            @Valid @ConvertGroup(to = TestCaseUploadDto.Create.class) final TestCaseUploadDto dto) {
        LOGGER.debug("Creating test case for exercise with id {}", exerciseId);
        final var testCase = examService.createTestCase(
                exerciseId,
                dto.getVisibility(),
                dto.getTimeout(),
                dto.getInputs(),
                dto.getExpectedOutputs()
        );
        final var location = uriInfo.getAbsolutePathBuilder()
                .path(Long.toString(testCase.getId()))
                .build();
        return Response.created(location).build();
    }

    @GET
    @Path(Routes.TEST_CASE)
    public Response getTestCaseById(@PathParam("testCaseId") final long testCaseId) {
        LOGGER.debug("Getting test case with id {}", testCaseId);
        return examService.getTestCase(testCaseId)
                .map(TestCaseDownloadDto::new)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(""))
                .build();
    }

    @PUT
    @Path(Routes.TEST_CASE)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyTestCase(
            @SuppressWarnings("RSReferenceInspection") @PathParam("testCaseId") final long testCaseId,
            @Valid @ConvertGroup(to = TestCaseUploadDto.Update.class) final TestCaseUploadDto dto) {
        LOGGER.debug("Updating test case with id {}", testCaseId);
        examService.modifyTestCase(
                testCaseId,
                dto.getVisibility(),
                dto.getTimeout(),
                dto.getInputs(),
                dto.getExpectedOutputs()
        );
        return Response.noContent().build();
    }

    @DELETE
    @Path(Routes.TEST_CASE)
    public Response deleteTestCase(
            @SuppressWarnings("RSReferenceInspection") @PathParam("testCaseId") final long testCaseId) {
        LOGGER.debug("Removing test case with id {}", testCaseId);
        examService.deleteTestCase(testCaseId);
        return Response.noContent().build();
    }
}
