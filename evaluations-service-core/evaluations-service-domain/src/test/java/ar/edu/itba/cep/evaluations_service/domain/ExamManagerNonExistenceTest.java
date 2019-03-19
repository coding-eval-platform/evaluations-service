package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.persistence.repository_utils.PagingRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.BiConsumer;

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
     * @param examRepository             A mocked {@link ExamRepository} passed to super class.
     * @param exerciseRepository         A mocked {@link ExerciseRepository} passed to super class.
     * @param testCaseRepository         A mocked {@link TestCaseRepository} passed to super class.
     * @param exerciseSolutionRepository A mocked {@link ExamRepository} passed to super class.
     * @param exerciseSolResultRep       A mocked {@link ExerciseSolutionResultRepository} passed to super class.
     */
    ExamManagerNonExistenceTest(
            @Mock(name = "examRep") final ExamRepository examRepository,
            @Mock(name = "exerciseRep") final ExerciseRepository exerciseRepository,
            @Mock(name = "testCaseRep") final TestCaseRepository testCaseRepository,
            @Mock(name = "exerciseSolutionRep") final ExerciseSolutionRepository exerciseSolutionRepository,
            @Mock(name = "exerciseSolutionResultRep") final ExerciseSolutionResultRepository exerciseSolResultRep) {
        super(examRepository,
                exerciseRepository,
                testCaseRepository,
                exerciseSolutionRepository,
                exerciseSolResultRep);
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
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.empty());
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


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    /**
     * Tests that trying to create an {@link Exercise} for an {@link Exam} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testCreateExerciseForNonExistenceExam() {
        testMissingExamThrowsNoSuchEntityException(
                (manager, id) -> manager.createExercise(id, TestHelper.validExerciseQuestion()),
                "Trying to create an exercise" +
                        " belonging to an exam that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to change the question of an {@link Exercise} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testChangeExerciseQuestionForNonExistenceExercise() {
        testMissingExerciseThrowsNoSuchEntityException(
                (manager, id) -> manager.changeExerciseQuestion(id, TestHelper.validExerciseQuestion()),
                "Trying to change the question of an exercise that does not exist" +
                        " does not throw a NoSuchEntityException"
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
     * Tests that trying to list the {@link ExerciseSolution}s belonging to an {@link Exercise} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testListOfExerciseSolutionsForNonExistenceExercise() {
        testMissingExerciseThrowsNoSuchEntityException(
                (manager, id) -> manager.listSolutions(id, Mockito.mock(PagingRequest.class)),
                "Trying to list the exercise solutions of an exercise that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
    }


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

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
                                TestHelper.validTestCaseList(),
                                TestHelper.validTestCaseList()
                        ),
                "Trying to create a test case" +
                        " belonging to an exercise that does not exist does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to change the visibility of a {@link TestCase} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testChangeVisibilityForNonExistenceTestCase() {
        testMissingTestCaseThrowsNoSuchEntityException(
                (manager, id) -> manager.changeVisibility(id, TestHelper.validTestCaseVisibility()),
                "Trying to change the visibility of a test case that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to change the inputs of a {@link TestCase} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testChangeInputsForNonExistenceTestCase() {
        testMissingTestCaseThrowsNoSuchEntityException(
                (manager, id) -> manager.changeInputs(id, TestHelper.validTestCaseList()),
                "Trying to change the inputs of a test case that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to change the expected outputs of a {@link TestCase} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testChangeExpectedOutputsForNonExistenceTestCase() {
        testMissingTestCaseThrowsNoSuchEntityException(
                (manager, id) -> manager.changeExpectedOutputs(id, TestHelper.validTestCaseList()),
                "Trying to change the expected outputs of a test case that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to clear the inputs of a {@link TestCase} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testClearInputsForNonExistenceTestCase() {
        testMissingTestCaseThrowsNoSuchEntityException(
                ExamManager::clearInputs,
                "Trying to clear the inputs of a test case that does not exist" +
                        " does not throw a NoSuchEntityException"
        );
    }

    /**
     * Tests that trying to clear the expected inputs of a {@link TestCase} that does not exists
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testClearExpectedOutputsForNonExistenceTestCase() {
        testMissingTestCaseThrowsNoSuchEntityException(
                ExamManager::clearOutputs,
                "Trying to clear the expected outputs of a test case that does not exist" +
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
    // Solutions
    // ================================================================================================================

    /**
     * Tests that trying to create an {@link ExerciseSolution} for an {@link Exercise} that does not not exists
     * does not throws a {@link NoSuchEntityException}.
     */
    @Test
    void testCreateExerciseSolutionNonExistenceExercise() {
        testMissingExerciseThrowsNoSuchEntityException(
                (manager, id) -> manager.createExerciseSolution(id, TestHelper.validExerciseSolutionAnswer()),
                "Trying to create a solution" +
                        " for an exercise that does not exist does not throw a NoSuchEntityException"
        );
    }

    // ================================================================================================================
    // Solution Results
    // ================================================================================================================

    /**
     * Tests that trying to process an execution for an {@link ExerciseSolution} that does not not exists
     * does not throws a {@link NoSuchEntityException}.
     *
     * @param testCase A mocked {@link TestCase} (the one from which the inputs were taken for the execution).
     */
    @Test
    void testProcessExecutionForNonExistenceSolution(@Mock(name = "testCase") final TestCase testCase) {
        final var solutionId = TestHelper.validExerciseId();
        Mockito
                .when(testCaseRepository.findById(solutionId))
                .thenReturn(Optional.of(testCase));
        Mockito
                .when(exerciseSolutionRepository.findById(solutionId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchEntityException.class,
                () ->
                        examManager.processExecution(
                                solutionId,
                                TestHelper.validTestCaseId(),
                                TestHelper.validExerciseSolutionExitCode(),
                                TestHelper.validExerciseSolutionResultList(),
                                TestHelper.validExerciseSolutionResultList()
                        ),
                "Trying to process an execution for a solution that does not exist" +
                        " does not throw a NoSuchEntityException"
        );

        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.atMost(1)).findById(Mockito.anyLong());
        Mockito.verify(exerciseSolutionRepository, Mockito.atMost(1)).findById(solutionId);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
        Mockito.verifyNoMoreInteractions(testCaseRepository, exerciseSolutionRepository);
    }

    /**
     * Tests that trying to process an execution for a {@link TestCase} that does not not exists
     * does not throws a {@link NoSuchEntityException}.
     *
     * @param solution A mocked {@link ExerciseSolution} (the one from which the executed code was taken).
     */
    @Test
    void testProcessExecutionForNonExistenceTestCase(@Mock(name = "solution") final ExerciseSolution solution) {
        final var testCaseId = TestHelper.validExerciseId();
        Mockito
                .when(exerciseSolutionRepository.findById(testCaseId))
                .thenReturn(Optional.of(solution));
        Mockito
                .when(testCaseRepository.findById(testCaseId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchEntityException.class,
                () ->
                        examManager.processExecution(
                                TestHelper.validExerciseSolutionId(),
                                testCaseId,
                                TestHelper.validExerciseSolutionExitCode(),
                                TestHelper.validExerciseSolutionResultList(),
                                TestHelper.validExerciseSolutionResultList()
                        ),
                "Trying to process an execution for a test case that does not exist" +
                        " does not throw a NoSuchEntityException"
        );

        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.atMost(1)).findById(testCaseId);
        Mockito.verify(exerciseSolutionRepository, Mockito.atMost(1)).findById(Mockito.anyLong());
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
        Mockito.verifyNoMoreInteractions(testCaseRepository, exerciseSolutionRepository);
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
        final var examId = TestHelper.validExerciseId();
        Mockito.when(exerciseRepository.findById(examId)).thenReturn(Optional.empty());
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
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());
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
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());
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
