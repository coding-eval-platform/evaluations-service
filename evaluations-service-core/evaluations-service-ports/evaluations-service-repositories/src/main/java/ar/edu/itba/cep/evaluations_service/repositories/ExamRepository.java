package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.Exam;

/**
 * A port out of the application that allows {@link Exam} persistence.
 */
public interface ExamRepository extends BasicRepository<Exam, Long>, PagingRepository<Exam, Long> {
}
