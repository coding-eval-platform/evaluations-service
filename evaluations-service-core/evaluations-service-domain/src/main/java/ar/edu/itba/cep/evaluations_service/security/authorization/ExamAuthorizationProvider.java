package ar.edu.itba.cep.evaluations_service.security.authorization;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * A component in charge of stating whether an {@link Exam}
 * is owned by a user with a given {@code username}.
 */
@Component(value = "examAuthorizationProvider")
public class ExamAuthorizationProvider {

    /**
     * The {@link ExamRepository} used to load {@link Exam}s by their ids.
     */
    private final ExamRepository examRepository;


    /**
     * Constructor.
     *
     * @param examRepository The {@link ExamRepository} used to load {@link Exam}s by their ids.
     */
    @Autowired
    public ExamAuthorizationProvider(final ExamRepository examRepository) {
        this.examRepository = examRepository;
    }


    /**
     * Indicates whether the {@link Exam} with the given {@code examId} has an owner that matches
     * with the given {@code principal}.
     *
     * @param examId    The id of the {@link Exam} being accessed.
     * @param principal The username of the user used to check ownership.
     * @return {@code true} if the {@link Exam} with the given {@code examId} belongs to the user
     * whose username is the given {@code principal}.
     */
    @Transactional(readOnly = true)
    public boolean isOwner(final long examId, final String principal) {
        return examRepository.findById(examId)
                .filter(exam -> AuthorizationHelper.isExamOwner(exam, principal))
                .isPresent()
                ;
    }

    /**
     * Indicates whether the {@link Exam} with the given {@code examId} has started.
     *
     * @param examId The id of {@link Exam} to be checked.
     * @return {@code true} if the {@link Exam} with the given {@code examId} has started
     * (i.e has {@link Exam.State#IN_PROGRESS} or {@link Exam.State#FINISHED} state, or {@code false} otherwise).
     */
    @Transactional(readOnly = true)
    public boolean hasStarted(final long examId) {
        return examRepository.findById(examId)
                .filter(AuthorizationHelper::examHasStarted)
                .isPresent()
                ;
    }
}
