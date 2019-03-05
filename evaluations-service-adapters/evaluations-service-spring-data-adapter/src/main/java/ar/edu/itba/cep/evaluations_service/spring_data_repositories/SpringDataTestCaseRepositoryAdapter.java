package ar.edu.itba.cep.evaluations_service.spring_data_repositories;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataExamRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataTestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A concrete implementation of a {@link TestCaseRepository}
 * which acts as an adapter for a {@link SpringDataTestCaseRepository}.
 */
@Repository
public class SpringDataTestCaseRepositoryAdapter implements TestCaseRepository {

    /**
     * A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    private final SpringDataTestCaseRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataTestCaseRepositoryAdapter(final SpringDataTestCaseRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<TestCase> getExercisePublicTestCases(final Exercise exercise) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<TestCase> getExercisePrivateTestCases(final Exercise exercise) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteExerciseTestCases(final Exercise exercise) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteExamTestCases(final Exam exam) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean existsById(final Long aLong) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<TestCase> findById(final Long aLong) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<TestCase> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends TestCase> S save(final S entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends TestCase> void delete(final S entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteById(final Long aLong) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
