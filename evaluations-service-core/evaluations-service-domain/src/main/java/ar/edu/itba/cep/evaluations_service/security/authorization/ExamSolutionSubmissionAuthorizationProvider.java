package ar.edu.itba.cep.evaluations_service.security.authorization;

import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import ar.edu.itba.cep.evaluations_service.repositories.ExamSolutionSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * A component in charge of stating whether an {@link ExamSolutionSubmission}
 * is owned by a user with a given {@code username}.
 */
@Component(value = "examSolutionSubmissionAuthorizationProvider")
public class ExamSolutionSubmissionAuthorizationProvider {

    /**
     * The {@link ExamSolutionSubmissionRepository} used to load {@link ExamSolutionSubmission}s by their ids.
     */
    private final ExamSolutionSubmissionRepository examSolutionSubmissionRepository;


    /**
     * Constructor.
     *
     * @param examSolutionSubmissionRepository The {@link ExamSolutionSubmissionRepository}
     *                                         used to load {@link ExamSolutionSubmission}s by their ids.
     */
    @Autowired
    public ExamSolutionSubmissionAuthorizationProvider(
            final ExamSolutionSubmissionRepository examSolutionSubmissionRepository) {
        this.examSolutionSubmissionRepository = examSolutionSubmissionRepository;
    }


    /**
     * Indicates whether the {@link ExamSolutionSubmission}'s owner matches with the given {@code principal}.
     *
     * @param submissionId The id of the {@link ExamSolutionSubmission} being accessed.
     * @param principal    The username of the user used to check ownership.
     * @return {@code true} if the {@link ExamSolutionSubmission} with the given {@code submissionId}
     * belongs to the user whose username is the given {@code principal}.
     */
    @Transactional(readOnly = true)
    public boolean isOwner(final long submissionId, final String principal) {
        return examSolutionSubmissionRepository.findById(submissionId)
                .map(ExamSolutionSubmission::getSubmitter)
                .filter(submitter -> submitter.equals(principal))
                .isPresent()
                ;
    }

    /**
     * Indicates whether the {@link ExamSolutionSubmission}'s {@link ar.edu.itba.cep.evaluations_service.models.Exam}'s
     * owner matches with the given {@code principal}.
     *
     * @param submissionId The id of the {@link ExamSolutionSubmission} being accessed.
     * @param principal    The username of the user used to check ownership.
     * @return {@code true} if the {@link ExamSolutionSubmission} with the given {@code submissionId}'s
     * {@link ar.edu.itba.cep.evaluations_service.models.Exam}'s
     * belongs to the user whose username is the given {@code principal}.
     */
    @Transactional(readOnly = true)
    public boolean isExamOwner(final long submissionId, final String principal) {
        return examSolutionSubmissionRepository.findById(submissionId)
                .map(ExamSolutionSubmission::getExam)
                .filter(exam -> AuthorizationHelper.isExamOwner(exam, principal))
                .isPresent()
                ;
    }
}
