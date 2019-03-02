package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.services.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * API endpoint for {@link Exam} management.
 */
@Component
public class ExamEndpoint {

    /**
     * The {@link ExamService} that will be used to manage {@link Exam}s.
     */
    private final ExamService examService;

    /**
     * Constructor.
     *
     * @param examService The {@link ExamService} that will be used to manage {@link Exam}s.
     */
    @Autowired
    public ExamEndpoint(final ExamService examService) {
        this.examService = examService;
    }
}
