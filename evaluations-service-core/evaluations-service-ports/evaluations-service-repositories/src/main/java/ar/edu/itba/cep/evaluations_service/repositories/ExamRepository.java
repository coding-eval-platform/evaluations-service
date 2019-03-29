package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.PagingRepository;

/**
 * A port out of the application that allows {@link Exam} persistence.
 */
public interface ExamRepository extends BasicRepository<Exam, Long>, PagingRepository<Exam, Long> {
}
