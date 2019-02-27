package ar.edu.itba.cep.evaluations_service.spring_data_repositories;

import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A concrete implementation of a {@link ExamRepository}
 * which acts as an adapter for a {@link SpringDataExamRepositoryAdapter}.
 */
@Repository
public class SpringDataExamRepositoryAdapter implements ExamRepository {

    /**
     * A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    private final SpringDataExamRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataExamRepositoryAdapter(final SpringDataExamRepository repository) {
        this.repository = repository;
    }
}
