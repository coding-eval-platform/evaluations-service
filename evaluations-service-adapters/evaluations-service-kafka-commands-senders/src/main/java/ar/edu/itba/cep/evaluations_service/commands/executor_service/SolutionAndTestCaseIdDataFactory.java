package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.executor.client.ExecutionResponseIdDataFactory;
import com.bellotapps.the_messenger.commons.Message;
import org.springframework.stereotype.Component;

/**
 * An {@link ExecutionResponseIdDataFactory} that builds a {@link SolutionAndTestCaseIds} instance from
 * a {@link Message}, taking data from its headers
 * (i.e checks for the {@link ResponseIdDataHeaders#SOLUTION_ID_HEADER} and
 * {@link ResponseIdDataHeaders#TEST_CASE_ID_HEADER} headers).
 */
@Component
public class SolutionAndTestCaseIdDataFactory implements ExecutionResponseIdDataFactory<SolutionAndTestCaseIds> {

    @Override
    public SolutionAndTestCaseIds buildFromMessage(final Message message) {
        final var testCaseId = message.headerValue(ResponseIdDataHeaders.TEST_CASE_ID_HEADER)
                .map(Long::parseLong)
                .orElseThrow(() -> new IllegalArgumentException("Missing test case id")); // TODO: throw?
        final var solutionId = message.headerValue(ResponseIdDataHeaders.SOLUTION_ID_HEADER)
                .map(Long::parseLong)
                .orElseThrow(() -> new IllegalArgumentException("Missing solution id")); // TODO: throw?

        return SolutionAndTestCaseIds.create(solutionId, testCaseId);
    }
}
