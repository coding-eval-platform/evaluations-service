package ar.edu.itba.cep.evaluations_service.spring_data;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExamRepository;
import ar.edu.itba.cep.evaluations_service.spring_data.interfaces.SpringDataExerciseRepository;
import com.bellotapps.webapps_commons.exceptions.NotImplementedException;
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
        throw new NotImplementedException();
    }

    @Override
    public void deleteExamExercises(final Exam exam) {
        throw new NotImplementedException();
    }

    @Override
    public long count() {
        throw new NotImplementedException();
    }

    @Override
    public boolean existsById(final Long aLong) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public Optional<Exercise> findById(final Long aLong) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public Iterable<Exercise> findAll() {
        throw new NotImplementedException();
    }

    @Override
    public <S extends Exercise> S save(final S entity) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public <S extends Exercise> void delete(final S entity) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public void deleteById(final Long aLong) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public void deleteAll() {
        throw new NotImplementedException();
    }
}
