package ar.edu.itba.cep.evaluations_service.security.authorization;


import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * A component in charge of stating whether an {@link ExerciseSolution}
 * is owned by a user with a given {@code username}.
 */
@Component(value = "exerciseSolutionAuthorizationProvider")
public class ExerciseSolutionAuthorizationProvider {

    /**
     * The {@link ExerciseSolutionRepository} used to load {@link ExerciseSolution}s by their ids.
     */
    private final ExerciseSolutionRepository exerciseSolutionRepository;


    /**
     * Constructor.
     *
     * @param exerciseSolutionRepository The {@link ExerciseSolutionRepository}
     *                                   used to load {@link ExerciseSolution}s by their ids.
     */
    @Autowired
    public ExerciseSolutionAuthorizationProvider(
            final ExerciseSolutionRepository exerciseSolutionRepository) {
        this.exerciseSolutionRepository = exerciseSolutionRepository;
    }


    /**
     * Indicates whether the {@link ExerciseSolution}'s owner matches with the given {@code principal}.
     *
     * @param solutionId The id of the {@link ExerciseSolution} being accessed.
     * @param principal  The username of the user used to check ownership.
     * @return {@code true} if the {@link ExerciseSolution} with the given {@code solutionId}
     * belongs to the user whose username is the given {@code principal}.
     */
    @Transactional(readOnly = true)
    public boolean isOwner(final long solutionId, final String principal) {
        return exerciseSolutionRepository.findById(solutionId)
                .map(ExerciseSolution::getSubmission)
                .map(ExamSolutionSubmission::getSubmitter)
                .filter(submitter -> submitter.equals(principal))
                .isPresent()
                ;
    }

    /**
     * Indicates whether the {@link ExerciseSolution}'s {@link ExamSolutionSubmission}'s
     * {@link ar.edu.itba.cep.evaluations_service.models.Exam}'s owner matches with the given {@code principal}.
     *
     * @param solutionId The id of the {@link ExamSolutionSubmission} being accessed.
     * @param principal  The username of the user used to check ownership.
     * @return {@code true} if the {@link ExerciseSolution} with the given {@code solutionId}'s
     * {@link ExamSolutionSubmission}'s {@link ar.edu.itba.cep.evaluations_service.models.Exam}'s
     * belongs to the user whose username is the given {@code principal}.
     */
    @Transactional(readOnly = true)
    public boolean isExamOwner(final long solutionId, final String principal) {
        return exerciseSolutionRepository.findById(solutionId)
                .map(ExerciseSolution::getSubmission)
                .map(ExamSolutionSubmission::getExam)
                .filter(exam -> AuthorizationHelper.isExamOwner(exam, principal))
                .isPresent()
                ;
    }
}
