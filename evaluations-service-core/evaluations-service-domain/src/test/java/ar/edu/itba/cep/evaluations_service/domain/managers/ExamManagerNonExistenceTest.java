package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.when;

/**
 * Test class for {@link ExamManager},
 * containing tests for the non-existence condition
 * (i.e how the manager behaves when trying to operate over entities that do not exist).
 */
@ExtendWith(MockitoExtension.class)
class ExamManagerNonExistenceTest extends AbstractExamManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository     A mocked {@link ExamRepository} passed to super class.
     * @param exerciseRepository A mocked {@link ExerciseRepository} passed to super class.
     * @param testCaseRepository A mocked {@link TestCaseRepository} passed to super class.
     * @param publisher          A mocked {@link ApplicationEventPublisher} passed to super class.
     */
    ExamManagerNonExistenceTest(
            @Mock(name = "examRepository") final ExamRepository examRepository,
            @Mock(name = "exerciseRepository") final ExerciseRepository exerciseRepository,
            @Mock(name = "testCaseRepository") final TestCaseRepository testCaseRepository,
            @Mock(name = "publisher") final ApplicationEventPublisher publisher) {
        super(examRepository, exerciseRepository, testCaseRepository, publisher);
    }


    // ================================================================================================================
    // Exams
    // ================================================================================================================

    /**
     * Tests that searching for an {@link Exam} that does not exist does not fail,
     * and returns an empty {@link Optional}.
     */
    @Test
    void testSearchForExamThatDoesNotExist() {
        final var examId = TestHelper.validExamId();
        when(examRepository.findById(examId)).thenReturn(Optional.empty());
        Assertions.assertTrue(
                examManager.getExam(examId).isEmpty(),
                "Searching for an exam that does not exist does not return an empty optional."
        );
        verifyOnlyExamSearch(examId);
    }

    /**
     * Tests that trying to modify an {@link Exam} that does not exists throws a {@link NoSuchEntityException}.
     */
    @Test
    void testModifyNonExistenceExam() {
        testMissingExamThrowsNoSuchEntityException(
                (manager, id) -> manager.modifyExam(
                        id,
                        TestHelper.validExamDescription(),
                        TestHelper.validExamStartingMoment(),
                        TestHelper.validExamDuration()),
                "Trying to modify an exam that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to start an {@link Exam} that does not exists throws a {@link NoSuchEntityException}.
     */
    @Test
    void testStartNonExistenceExam() {
        testMissingExamThrowsNoSuchEntityException(
                ExamManager::startExam,
                "Trying to start an exam that does not exist does not throw a NoSuchEntityException"
        );

    }

    /**
     * Tests that trying to finish an {@link Exam} that does not exists throws a {@link NoSuchEntityException}.
     */
    @Test
    void testFinishNonExistenceExam() {
        testMissingExamThrowsNoSuchEntityException(
                ExamManager::finishExam,
                "Trying to finish an exam that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to add an owner to an {@link Exam} that does not exists throws a {@link NoSuchEntityException}.
     */
    @Test
    void testAddOwnerForNonExistenceExam() {
        testMissingExamThrowsNoSuchEntityException(
                (em, id) -> em.addOwnerToExam(id, TestHelper.validOwner()),
                "Trying to add an owner to an exam that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to remove an owner from an {@link Exam}
     * that does not exists throws a {@link NoSuchEntityException}.
     */
    @Test
    void testRemoveOwnerForNonExistenceExam() {
        testMissingExamThrowsNoSuchEntityException(
                (em, id) -> em.removeOwnerFromExam(id, TestHelper.validOwner()),
                "Trying to remove an owner from an exam that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to delete an {@link Exam} that does not not exists
     * does not throws a {@link NoSuchEntityException}.
     */
    @Test
    void testDeleteNonExistenceExam() {
        testMissingExam(
                ExamManagerNonExistenceTest::assertDoesNotThrowsNoSuchEntityException,
                ExamManager::deleteExam,
                "Trying to delete an exam that does not exist throws an exception"
        );
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    /**
     * Tests that trying to get {@link Exercise}s belonging to an {@link Exam} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testGetExercisesOfNonExistenceExam() {
        testMissingExamThrowsNoSuchEntityException(
                ExamManager::getExercises,
                "Trying to get exercises" +
                        " belonging to an exam that does not exist does not throw a NoSuchEntityException"
        );

    }

    /**
     * Tests that trying to clear {@link Exercise}s belonging to an {@link Exam} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testClearExercisesOfNonExistenceExam() {
        testMissingExamThrowsNoSuchEntityException(
                ExamManager::clearExercises,
                "Trying to clear exercises" +
                        " belonging to an exam that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to create an {@link Exercise} for an {@link Exam} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testCreateExerciseForNonExistenceExam() {
        testMissingExamThrowsNoSuchEntityException(
                (manager, id) -> manager.createExercise(
                        id,
                        TestHelper.validExerciseQuestion(),
                        TestHelper.validLanguage(),
                        TestHelper.validSolutionTemplate(),
                        TestHelper.validAwardedScore()
                ),
                "Trying to create an exercise" +
                        " belonging to an exam that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that searching for an {@link Exercise} that does not exist does not fail,
     * and returns an empty {@link Optional}.
     */
    @Test
    void testSearchForExerciseThatDoesNotExist() {
        final var exerciseId = TestHelper.validExerciseId();
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());
        Assertions.assertTrue(
                examManager.getExercise(exerciseId).isEmpty(),
                "Searching for an exercise that does not exist does not return an empty optional."
        );
        verifyOnlyExerciseSearch(exerciseId);
    }

    /**
     * Tests that trying to modify an {@link Exercise} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testModifyNonExistenceExercise() {
        testMissingExerciseThrowsNoSuchEntityException(
                (manager, id) -> manager.modifyExercise(
                        id,
                        TestHelper.validExerciseQuestion(),
                        TestHelper.validLanguage(),
                        TestHelper.validSolutionTemplate(),
                        TestHelper.validAwardedScore()
                ),
                "Trying to modify an exercise that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to delete an {@link Exercise} that does not not exists
     * does not throws a {@link NoSuchEntityException}.
     */
    @Test
    void testDeleteNonExistenceExercise() {
        testMissingExercise(
                ExamManagerNonExistenceTest::assertDoesNotThrowsNoSuchEntityException,
                ExamManager::deleteExercise,
                "Trying to delete an exercise that does not exist throws an exception"
        );
    }


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    /**
     * Tests that trying to list the public {@link TestCase}s belonging to an {@link Exercise} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testListOfPublicTestCasesForNonExistenceExercise() {
        testMissingExerciseThrowsNoSuchEntityException(
                ExamManager::getPublicTestCases,
                "Trying to list the public test cases of an exercise that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to list the private {@link TestCase}s belonging to an {@link Exercise} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testListOfPrivateTestCasesForNonExistenceExercise() {
        testMissingExerciseThrowsNoSuchEntityException(
                ExamManager::getPrivateTestCases,
                "Trying to list the private test cases of an exercise that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to create an {@link Exercise} for an {@link Exam} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testCreateTestCaseForNonExistenceExercise() {
        testMissingExerciseThrowsNoSuchEntityException(
                (manager, id) ->
                        manager.createTestCase(
                                id,
                                TestHelper.validTestCaseVisibility(),
                                TestHelper.validTestCaseTimeout(),
                                TestHelper.validTestCaseList(),
                                TestHelper.validTestCaseList()
                        ),
                "Trying to create a test case" +
                        " belonging to an exercise that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that searching for a {@link TestCase} that does not exist does not fail,
     * and returns an empty {@link Optional}.
     */
    @Test
    void testSearchForTestCaseThatDoesNotExist() {
        final var testCaseId = TestHelper.validTestCaseId();
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());
        Assertions.assertTrue(
                examManager.getTestCase(testCaseId).isEmpty(),
                "Searching for a test case that does not exist does not return an empty optional."
        );
        verifyOnlyTestCaseSearch(testCaseId);
    }

    /**
     * Tests that trying to change the visibility of a {@link TestCase} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testModifyNonExistenceTestCase() {
        testMissingTestCaseThrowsNoSuchEntityException(
                (manager, id) -> manager.modifyTestCase(
                        id,
                        TestHelper.validTestCaseVisibility(),
                        TestHelper.validTestCaseTimeout(),
                        TestHelper.validTestCaseList(),
                        TestHelper.validTestCaseList()
                ),
                "Trying to modify a test case that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to delete a {@link TestCase} that does not not exists
     * does not throws a {@link NoSuchEntityException}.
     */
    @Test
    void testDeleteNonExistenceTestCase() {
        testMissingTestCase(
                ExamManagerNonExistenceTest::assertDoesNotThrowsNoSuchEntityException,
                ExamManager::deleteTestCase,
                "Trying to delete a test case that does not exist throws an exception"
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Abstract tests
    // ========================================

    /**
     * Tests that performing the given {@code examManagerAction} throws a {@link NoSuchEntityException}
     * because the action implies accessing an {@link Exam} that does not exists.
     *
     * @param examManagerAction The action being tested.
     * @param message           The message to be displayed in case of assertion failure.
     */
    private void testMissingExamThrowsNoSuchEntityException(
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        testMissingExam(
                ExamManagerNonExistenceTest::assertNoSuchEntityExceptionIsThrown,
                examManagerAction,
                message
        );
    }

    /**
     * Tests that performing the given {@code examManagerAction} throws a {@link NoSuchEntityException}
     * because the action implies accessing an {@link Exercise} that does not exists.
     *
     * @param examManagerAction The action being tested.
     * @param message           The message to be displayed in case of assertion failure.
     */
    private void testMissingExerciseThrowsNoSuchEntityException(
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        testMissingExercise(
                ExamManagerNonExistenceTest::assertNoSuchEntityExceptionIsThrown,
                examManagerAction,
                message
        );
    }

    /**
     * Tests that performing the given {@code examManagerAction} throws a {@link NoSuchEntityException}
     * because the action implies accessing a {@link TestCase} that does not exists.
     *
     * @param examManagerAction The action being tested.
     * @param message           The message to be displayed in case of assertion failure.
     */
    private void testMissingTestCaseThrowsNoSuchEntityException(
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        testMissingTestCase(
                ExamManagerNonExistenceTest::assertNoSuchEntityExceptionIsThrown,
                examManagerAction,
                message
        );
    }

    /**
     * Performs a "missing exam" test.
     *
     * @param missingEntityAssertion The {@link MissingEntityAssertion} to be used to assert a given condition
     *                               when executing the {@code examManagerAction}.
     * @param examManagerAction      The action being tested.
     * @param message                The message to be displayed in case of assertion failure.
     */
    private void testMissingExam(
            final MissingEntityAssertion missingEntityAssertion,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        final var examId = TestHelper.validExamId();
        when(examRepository.findById(examId)).thenReturn(Optional.empty());
        missingEntityAssertion.assertion(examManager, examId, examManagerAction, message);
        verifyOnlyExamSearch(examId);
    }

    /**
     * Performs a "missing exercise" test.
     *
     * @param missingEntityAssertion The {@link MissingEntityAssertion} to be used to assert a given condition
     *                               when executing the {@code examManagerAction}.
     * @param examManagerAction      The action being tested.
     * @param message                The message to be displayed in case of assertion failure.
     */
    private void testMissingExercise(
            final MissingEntityAssertion missingEntityAssertion,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        final var exerciseId = TestHelper.validExerciseId();
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());
        missingEntityAssertion.assertion(examManager, exerciseId, examManagerAction, message);
        verifyOnlyExerciseSearch(exerciseId);
    }

    /**
     * Performs a "missing test case" test.
     *
     * @param missingEntityAssertion The {@link MissingEntityAssertion} to be used to assert a given condition
     *                               when executing the {@code examManagerAction}.
     * @param examManagerAction      The action being tested.
     * @param message                The message to be displayed in case of assertion failure.
     */
    private void testMissingTestCase(
            final MissingEntityAssertion missingEntityAssertion,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        final var testCaseId = TestHelper.validExerciseId();
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());
        missingEntityAssertion.assertion(examManager, testCaseId, examManagerAction, message);
        verifyOnlyTestCaseSearch(testCaseId);
    }


    // ========================================
    // Custom Assertions
    // ========================================

    /**
     * Asserts that a {@link NoSuchEntityException} is thrown.
     *
     * @param examManager       The {@link ExamManager} to be asserted.
     * @param id                The hypothetical id of the non existence entity.
     * @param examManagerAction The action being tested.
     * @param message           The message to be displayed in case of assertion failure.
     */
    private static void assertNoSuchEntityExceptionIsThrown(
            final ExamManager examManager,
            final long id,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> examManagerAction.accept(examManager, id),
                message
        );
    }

    /**
     * Asserts that a {@link NoSuchEntityException} is not thrown.
     *
     * @param examManager       The {@link ExamManager} to be asserted.
     * @param id                The hypothetical id of the non existence entity.
     * @param examManagerAction The action being tested.
     * @param message           The message to be displayed in case of assertion failure.
     */
    private static void assertDoesNotThrowsNoSuchEntityException(
            final ExamManager examManager,
            final long id,
            final BiConsumer<ExamManager, Long> examManagerAction,
            final String message) {
        Assertions.assertDoesNotThrow(
                () -> examManagerAction.accept(examManager, id),
                message
        );
    }


    // ========================================
    // Inner classes, interfaces and enums
    // ========================================

    @FunctionalInterface
    private interface MissingEntityAssertion {

        /**
         * The assertion to be performed.
         *
         * @param examManager       The {@link ExamManager} to be asserted.
         * @param id                The hypothetical id of the non existence entity.
         * @param examManagerAction The action being tested.
         * @param message           The message to be displayed in case of assertion failure.
         */
        void assertion(
                final ExamManager examManager,
                final long id,
                final BiConsumer<ExamManager, Long> examManagerAction,
                final String message
        );
    }
}
