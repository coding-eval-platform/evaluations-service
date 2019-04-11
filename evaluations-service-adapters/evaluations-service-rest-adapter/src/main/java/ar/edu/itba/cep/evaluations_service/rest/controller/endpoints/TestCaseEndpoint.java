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
                dto.getInputs(),
                dto.getOutputs()
        );
        final var location = uriInfo.getAbsolutePathBuilder()
                .path(Long.toString(testCase.getId()))
                .build();
        return Response.created(location).build();
    }

    @PUT
    @Path(Routes.TEST_CASE_VISIBILITY)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeVisibility(
            @SuppressWarnings("RSReferenceInspection") @PathParam("testCaseId") final long testCaseId,
            @Valid @ConvertGroup(to = TestCaseUploadDto.ChangeVisibility.class) final TestCaseUploadDto dto) {
        LOGGER.debug("Changing visibility for test case with id {}", testCaseId);
        examService.changeVisibility(testCaseId, dto.getVisibility());
        return Response.noContent().build();
    }

    @PUT
    @Path(Routes.TEST_CASE_INPUTS)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeInputs(
            @SuppressWarnings("RSReferenceInspection") @PathParam("testCaseId") final long testCaseId,
            @Valid @ConvertGroup(to = TestCaseUploadDto.ChangeInputs.class) final TestCaseUploadDto dto) {
        LOGGER.debug("Changing inputs for test case with id {}", testCaseId);
        examService.changeInputs(testCaseId, dto.getInputs());
        return Response.noContent().build();
    }

    @PUT
    @Path(Routes.TEST_CASE_EXPECTED_OUTPUTS)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeExpectedOutputs(
            @SuppressWarnings("RSReferenceInspection") @PathParam("testCaseId") final long testCaseId,
            @Valid @ConvertGroup(to = TestCaseUploadDto.ChangeExpectedOutputs.class) final TestCaseUploadDto dto) {
        LOGGER.debug("Changing expected outputs for test case with id {}", testCaseId);
        examService.changeExpectedOutputs(testCaseId, dto.getOutputs());
        return Response.noContent().build();
    }

    @DELETE
    @Path(Routes.TEST_CASE_INPUTS)
    public Response clearInputs(
            @SuppressWarnings("RSReferenceInspection") @PathParam("testCaseId") final long testCaseId) {
        LOGGER.debug("Removing all inputs for test case with id {}", testCaseId);
        examService.clearInputs(testCaseId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(Routes.TEST_CASE_EXPECTED_OUTPUTS)
    public Response clearExpectedOutputs(
            @SuppressWarnings("RSReferenceInspection") @PathParam("testCaseId") final long testCaseId) {
        LOGGER.debug("Removing all expected outputs for test case with id {}", testCaseId);
        examService.clearOutputs(testCaseId);
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
