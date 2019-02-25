package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

import ar.edu.itba.cep.evaluations_service.models.Evaluation;
import ar.edu.itba.cep.evaluations_service.services.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * API endpoint for {@link Evaluation} management.
 */
@Component
public class EvaluationEndpoint {

    /**
     * The {@link EvaluationService} that will be used to manage {@link Evaluation}s.
     */
    private final EvaluationService evaluationService;

    /**
     * Constructor.
     *
     * @param evaluationService The {@link EvaluationService} that will be used to manage {@link Evaluation}s.
     */
    @Autowired
    public EvaluationEndpoint(final EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }
}
