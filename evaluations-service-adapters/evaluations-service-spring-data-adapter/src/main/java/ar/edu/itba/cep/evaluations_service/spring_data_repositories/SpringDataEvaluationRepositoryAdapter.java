package ar.edu.itba.cep.evaluations_service.spring_data_repositories;

import ar.edu.itba.cep.evaluations_service.repositories.EvaluationRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataEvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A concrete implementation of a {@link EvaluationRepository}
 * which acts as an adapter for a {@link SpringDataEvaluationRepositoryAdapter}.
 */
@Repository
public class SpringDataEvaluationRepositoryAdapter implements EvaluationRepository {

    /**
     * A {@link SpringDataEvaluationRepository} to which all operations are delegated.
     */
    private final SpringDataEvaluationRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataEvaluationRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataEvaluationRepositoryAdapter(final SpringDataEvaluationRepository repository) {
        this.repository = repository;
    }
}
