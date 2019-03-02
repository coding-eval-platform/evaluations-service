package ar.edu.itba.cep.evaluations_service.spring_data_repositories;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataExamRepository;
import ar.edu.itba.cep.evaluations_service.spring_data_repositories.spring_data_interfaces.SpringDataExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A concrete implementation of a {@link ExerciseRepository}
 * which acts as an adapter for a {@link SpringDataExerciseRepository}.
 */
@Repository
public class SpringDataExerciseRepositoryAdapter implements ExerciseRepository {

    /**
     * A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    private final SpringDataExerciseRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataExamRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataExerciseRepositoryAdapter(final SpringDataExerciseRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<Exercise> getExamExercises(final Exam exam) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteExamExercises(final Exam exam) {
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
    public Optional<Exercise> findById(final Long aLong) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<Exercise> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends Exercise> S save(final S entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends Exercise> void delete(final S entity) throws IllegalArgumentException {
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
