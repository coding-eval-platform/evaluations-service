package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Evaluation;
import ar.edu.itba.cep.evaluations_service.repositories.EvaluationRepository;
import ar.edu.itba.cep.evaluations_service.services.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manager for {@link Evaluation}s.
 */
@Service
public class EvaluationManager implements EvaluationService {

    /**
     * Repository for {@link Evaluation}s.
     */
    private final EvaluationRepository evaluationRepository;

    /**
     * Constructor.
     *
     * @param evaluationRepository Repository for {@link Evaluation}s.
     */
    @Autowired
    public EvaluationManager(final EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }
}
