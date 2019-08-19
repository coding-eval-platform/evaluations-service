package ar.edu.itba.cep.evaluations_service.spring_data.interfaces;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository for {@link Exam}s.
 */
@Repository
public interface SpringDataExamRepository extends PagingAndSortingRepository<Exam, Long> {

    /**
     * Returns the {@link Exam}s owned by the given {@code owner}, in a {@link Page} representation, according to
     * the given {@code pageable}.
     *
     * @param owner    The owner of the {@link Exam}s being returned.
     * @param pageable The {@link Pageable} that indicates page number, size, sorting options, etc.
     * @return A {@link Page} with the {@link Exam} owned by the given {@code owner}, configured according to the
     * given {@code pageable}.
     */
    Page<Exam> findByOwners(final String owner, final Pageable pageable);
}
