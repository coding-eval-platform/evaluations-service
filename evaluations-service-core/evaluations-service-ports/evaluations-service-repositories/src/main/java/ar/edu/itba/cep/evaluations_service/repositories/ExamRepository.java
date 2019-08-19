package ar.edu.itba.cep.evaluations_service.repositories;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.PagingRepository;

/**
 * A port out of the application that allows {@link Exam} persistence.
 */
public interface ExamRepository extends BasicRepository<Exam, Long>, PagingRepository<Exam, Long> {

    /**
     * Returns the {@link Exam}s owned by the given {@code owner}, in a {@link Page} representation, according to
     * the given {@code pagingRequest}.
     *
     * @param owner         The owner of the {@link Exam}s being returned.
     * @param pagingRequest The {@link PagingRequest} that indicates page number, size, sorting options, etc.
     * @return A {@link Page} with the {@link Exam} owned by the given {@code owner}, configured according to the
     * given {@code pagingRequest}.
     */
    Page<Exam> getOwnedBy(final String owner, final PagingRequest pagingRequest);
}
