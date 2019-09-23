package ar.edu.itba.cep.evaluations_service.commands.executor_service;

import ar.edu.itba.cep.executor.client.ExecutionResponseIdDataMessageBuilderConfigurer;
import com.bellotapps.the_messenger.producer.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * An {@link ExecutionResponseIdDataMessageBuilderConfigurer} that configures the builder in order
 * to set the {@link ResponseIdDataHeaders#SOLUTION_ID_HEADER} and {@link ResponseIdDataHeaders#TEST_CASE_ID_HEADER}
 * headers using data in a {@link SolutionAndTestCaseIds} instance.
 */
@Component
public class SolutionAndTestCaseIdDataMessageBuilderConfigurer
        implements ExecutionResponseIdDataMessageBuilderConfigurer<SolutionAndTestCaseIds> {

    @Override
    public void configureMessageBuilder(final MessageBuilder builder, final SolutionAndTestCaseIds idData) {
        builder.copyHeaders(ResponseIdDataHeaders.SOLUTION_ID_HEADER, ResponseIdDataHeaders.TEST_CASE_ID_HEADER)
                .withHeader(ResponseIdDataHeaders.SOLUTION_ID_HEADER, Long.toString(idData.getSolutionId()))
                .withHeader(ResponseIdDataHeaders.TEST_CASE_ID_HEADER, Long.toString(idData.getTestCaseId()));
    }
}
