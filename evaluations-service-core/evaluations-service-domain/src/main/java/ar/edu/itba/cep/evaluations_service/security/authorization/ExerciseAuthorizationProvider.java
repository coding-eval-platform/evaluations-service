package ar.edu.itba.cep.evaluations_service.security.authorization;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * A component in charge of stating whether an {@link Exercise}
 * is owned by a user with a given {@code username}.
 */
@Component(value = "exerciseAuthorizationProvider")
public class ExerciseAuthorizationProvider {

    /**
     * The {@link ExerciseRepository} used to load {@link Exercise}s by their ids.
     */
    private final ExerciseRepository exerciseRepository;


    /**
     * Constructor.
     *
     * @param exerciseRepository The {@link ExerciseRepository} used to load {@link Exercise}s by their ids.
     */
    @Autowired
    public ExerciseAuthorizationProvider(final ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }


    /**
     * Indicates whether the {@link Exercise} with the given {@code exerciseId} belongs to an {@link Exam} that
     * has an owner that matches with the given {@code principal}.
     *
     * @param exerciseId The id of the {@link Exercise} being accessed.
     * @param principal  The username of the user used to check ownership.
     * @return {@code true} if the {@link Exercise} with the given {@code exerciseId} belongs to an {@link Exam} that
     * belongs to the user whose username is the given {@code principal}.
     */
    @Transactional(readOnly = true)
    public boolean isOwner(final long exerciseId, final String principal) {
        return exerciseRepository.findById(exerciseId)
                .map(Exercise::getExam)
                .filter(exam -> AuthorizationHelper.isExamOwner(exam, principal))
                .isPresent()
                ;
    }
}
