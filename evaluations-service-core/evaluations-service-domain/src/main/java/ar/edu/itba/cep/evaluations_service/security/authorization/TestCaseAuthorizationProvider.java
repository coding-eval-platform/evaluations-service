package ar.edu.itba.cep.evaluations_service.security.authorization;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * A component in charge of stating whether an {@link TestCase}
 * is owned by a user with a given {@code username}.
 */
@Component(value = "testCaseAuthorizationProvider")
public class TestCaseAuthorizationProvider {

    /**
     * The {@link TestCaseRepository} used to load {@link TestCase}s by their ids.
     */
    private final TestCaseRepository testCaseRepository;


    /**
     * Constructor.
     *
     * @param testCaseRepository The {@link TestCaseRepository} used to load {@link TestCase}s by their ids.
     */
    @Autowired
    public TestCaseAuthorizationProvider(final TestCaseRepository testCaseRepository) {
        this.testCaseRepository = testCaseRepository;
    }


    /**
     * Indicates whether the {@link TestCase} with the given {@code testCaseId} belongs to an {@link Exercise}
     * that belongs to an {@link Exam} that has an owner that matches with the given {@code principal}.
     *
     * @param testCaseId The id of the {@link TestCase} being accessed.
     * @param principal  The username of the user used to check ownership.
     * @return {@code true} if the {@link TestCase} with the given {@code testCaseId} belongs to an {@link Exercise}
     * that belongs to an {@link Exam} that belongs to the user whose username is the given {@code principal}.
     */
    @Transactional(readOnly = true)
    public boolean isOwner(final long testCaseId, final String principal) {
        return testCaseRepository.findById(testCaseId)
                .map(TestCase::getExercise)
                .map(Exercise::getExam)
                .filter(exam -> AuthorizationHelper.isExamOwner(exam, principal))
                .isPresent()
                ;
    }

    /**
     * Indicates whether the {@link Exam} owning the {@link Exercise} owning the {@link TestCase}
     * with the given {@code testCaseId} has started.
     *
     * @param testCaseId The id of {@link TestCase} to be checked.
     * @return {@code true} if the {@link Exam} owning the {@link Exercise} owning the {@link TestCase}
     * with the given {@code testCaseId} has started
     * (i.e has {@link Exam.State#IN_PROGRESS} or {@link Exam.State#FINISHED} state, or {@code false} otherwise).
     */
    @Transactional(readOnly = true)
    public boolean examHasStarted(final long testCaseId) {
        return testCaseRepository.findById(testCaseId)
                .map(TestCase::getExercise)
                .map(Exercise::getExam)
                .filter(AuthorizationHelper::examHasStarted)
                .isPresent()
                ;
    }
}
