package ar.edu.itba.cep.evaluations_service.external_cep_services.evaluations_service;

import ar.edu.itba.cep.evaluations_service.external_cep_services.config.RestExternalCepServicesConfig;
import ar.edu.itba.cep.evaluations_service.external_cep_services.lti_service.LtiService;
import ar.edu.itba.cep.lti.ExamScoringRequest;
import ar.edu.itba.cep.lti.constants.Paths;
import ar.edu.itba.cep.lti.dtos.ExamScoringRequestDto;
import com.bellotapps.webapps_commons.exceptions.ExternalServiceException;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * A port out of the application that allows sending requests to the LTI service.
 */
@Component
public class RestTemplateLtiService implements LtiService {

    /**
     * The {@link RestTemplate} used to communicate with the LTI service.
     */
    private final RestTemplate restTemplate;
    /**
     * The {@link URI} that has to be accessed in order to communicate the score of an exam to the LTI service.
     */
    private final URI scoreUri;


    /**
     * Constructor.
     *
     * @param restTemplate The {@link RestTemplate} used to communicate with the LTI service.
     * @param properties   An {@link RestExternalCepServicesConfig.LtiServiceProperties} instance used to get
     *                     the LTI service's base url.
     */
    public RestTemplateLtiService(
            final RestTemplate restTemplate,
            final RestExternalCepServicesConfig.LtiServiceProperties properties) {
        Assert.notNull(properties, "The properties instance must not be null");
        Assert.hasText(properties.getBaseUrl(), "The LTI service's base url must not be blank");
        this.restTemplate = restTemplate;
        this.scoreUri = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .path(Paths.EXAM_SCORING_PATH)
                .build()
                .toUri()
        ;

    }

    @Override
    public void scoreExam(final ExamScoringRequest examScoringRequest) {
        final var entity = new HttpEntity<>(ExamScoringRequestDto.fromModel(examScoringRequest));
        try {
            restTemplate.put(scoreUri, entity);
        } catch (final Throwable e) {
            throw new ExternalServiceException(
                    "lti-service",
                    "Unexpected error when communicating with the LTI service",
                    e
            );
        }
    }
}
