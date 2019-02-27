package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manager for {@link Exam}s.
 */
@Service
public class ExamManager implements ExamService {

    /**
     * Repository for {@link Exam}s.
     */
    private final ExamRepository examRepository;

    /**
     * Constructor.
     *
     * @param examRepository Repository for {@link Exam}s.
     */
    @Autowired
    public ExamManager(final ExamRepository examRepository) {
        this.examRepository = examRepository;
    }
}
