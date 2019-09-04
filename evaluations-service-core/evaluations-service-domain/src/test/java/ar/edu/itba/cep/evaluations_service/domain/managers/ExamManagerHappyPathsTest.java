package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.Exam;
import ar.edu.itba.cep.evaluations_service.models.Exercise;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExamRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import ar.edu.itba.cep.evaluations_service.services.ExamWithOwners;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Test class for {@link ExamManager}, containing tests for the happy paths
 * (i.e how the manager behaves when operating with valid values, entity states, etc.).
 */
@ExtendWith(MockitoExtension.class)
class ExamManagerHappyPathsTest extends AbstractExamManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository     A mocked {@link ExamRepository} passed to super class.
     * @param exerciseRepository A mocked {@link ExerciseRepository} passed to super class.
     * @param testCaseRepository A mocked {@link TestCaseRepository} passed to super class.
     */
    ExamManagerHappyPathsTest(
            @Mock(name = "examRepository") final ExamRepository examRepository,
            @Mock(name = "exerciseRepository") final ExerciseRepository exerciseRepository,
            @Mock(name = "testCaseRepository") final TestCaseRepository testCaseRepository) {
        super(examRepository, exerciseRepository, testCaseRepository);
    }


    // ================================================================================================================
    // Exams
    // ================================================================================================================

    /**
     * Tests that searching for an {@link Exam} that exists returns the expected {@link Exam}.
     *
     * @param exam A mocked {@link Exam} (which is returned by {@link ExamManager#getExam(long)}).
     */
    @Test
    void testSearchForExamThatExists(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        when(exam.getId()).thenReturn(examId);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        final var examOptional = examManager.getExam(examId);
        Assertions.assertAll("Searching for an exam that exists is not working as expected",
                () -> Assertions.assertTrue(
                        examOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        examId,
                        examOptional.map(ExamWithOwners::getId).get().longValue(),
                        "The returned Exam id's is not the same as the requested"
                )
        );
        verifyOnlyExamSearch(examId);
    }

    /**
     * Tests that an {@link Exam} is created (i.e is saved) when arguments are valid.
     *
     * @param authentication  A mocked {@link Authentication} that will hold a mocked principal.
     * @param securityContext A mocked {@link SecurityContext} to be retrieved from the {@link SecurityContextHolder}.
     */
    @Test
    void testExamIsCreatedUsingValidArguments(
            @Mock(name = "authentication") final Authentication authentication,
            @Mock(name = "securityContext") final SecurityContext securityContext) {
        final var description = TestHelper.validExamDescription();
        final var startingAt = TestHelper.validExamStartingMoment();
        final var duration = TestHelper.validExamDuration();
        when(examRepository.save(any(Exam.class))).then(invocation -> invocation.getArgument(0));
        // Set the security context
        when(authentication.getPrincipal()).thenReturn(TestHelper.validOwner());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        final var exam = examManager.createExam(description, startingAt, duration);
        Assertions.assertAll("Exam properties are not the expected",
                () -> Assertions.assertEquals(
                        description,
                        exam.getDescription(),
                        "There is a mismatch in the description"
                ),
                () -> Assertions.assertEquals(
                        startingAt,
                        exam.getStartingAt(),
                        "There is a mismatch in the starting moment"
                ),
                () -> Assertions.assertEquals(
                        duration,
                        exam.getDuration(),
                        "There is a mismatch in the duration"
                )
        );
        verify(examRepository, only()).save(any(Exam.class));
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(testCaseRepository);

        // Clear the security context
        SecurityContextHolder.clearContext();
    }


    /**
     * Tests that an {@link Exam} is updated (i.e is saved) when arguments are valid.
     *
     * @param exam A mocked {@link Exam} (the one being updated).
     */
    @Test
    void testExamIsModifiedWithValidArgumentsForUpcomingExam(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        final var newDescription = TestHelper.validExamDescription();
        final var newStartingAt = TestHelper.validExamStartingMoment();
        final var newDuration = TestHelper.validExamDuration();
        doNothing().when(exam).update(newDescription, newStartingAt, newDuration);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(examRepository.save(any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.modifyExam(examId, newDescription, newStartingAt, newDuration),
                "An unexpected exception was thrown"
        );
        verify(exam, only()).update(newDescription, newStartingAt, newDuration);
        verify(examRepository, times(1)).findById(examId);
        verify(examRepository, times(1)).save(any(Exam.class));
        verifyNoMoreInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(testCaseRepository);
    }

    /**
     * Tests that starting an upcoming {@link Exam} works as expected
     * (changes the state and then saves the exam instance).
     *
     * @param exam     A mocked {@link Exam} (the one being started).
     * @param exercise A mocked {@link Exercise} (owned by the {@code exam}).
     * @param testCase A mocked {@link TestCase} (owned by the {@code exercise}).
     */
    @Test
    void testExamIsStartedWhenIsUpcomingAndHasExercisesWithPrivateTestCases(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {

        final var examId = TestHelper.validExamId();
        doNothing().when(exam).startExam();
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(exerciseRepository.getExamExercises(exam)).thenReturn(List.of(exercise));
        when(testCaseRepository.getExercisePrivateTestCases(exercise)).thenReturn(List.of(testCase));
        when(examRepository.save(any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.startExam(examId),
                "An unexpected exception was thrown"
        );
        verify(exam, only()).startExam();
        verify(examRepository, times(1)).findById(examId);
        verify(examRepository, times(1)).save(exam);
        verifyNoMoreInteractions(examRepository);
        verify(exerciseRepository, only()).getExamExercises(exam);
        verify(testCaseRepository, only()).getExercisePrivateTestCases(exercise);
    }

    /**
     * Tests that finishing an in progress {@link Exam} works as expected
     * (changes the state and then saves the exam instance).
     *
     * @param exam A mocked {@link Exam} (the one being finished).
     */
    @Test
    void testExamIsFinishedWhenIsInProgress(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        doNothing().when(exam).finishExam();
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(examRepository.save(any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.finishExam(examId),
                "An unexpected exception was thrown"
        );
        verify(exam, only()).finishExam();
        verify(examRepository, times(1)).findById(examId);
        verify(examRepository, times(1)).save(exam);
        verifyNoMoreInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(testCaseRepository);
    }

    /**
     * Tests that adding an owner to an {@link Exam} works as expected.
     *
     * @param exam A mocked {@link Exam} (the one to which an owner is being set).
     */
    @Test
    void testOwnerIsAdded(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        final var owner = TestHelper.validOwner();
        doNothing().when(exam).addOwner(owner);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(examRepository.save(any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.addOwnerToExam(examId, owner),
                "An unexpected exception was thrown"
        );
        verify(exam, only()).addOwner(owner);
        verify(examRepository, times(1)).findById(examId);
        verify(examRepository, times(1)).save(exam);
        verifyNoMoreInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(testCaseRepository);
    }

    /**
     * Tests that removing an owner from an {@link Exam} works as expected.
     *
     * @param exam A mocked {@link Exam} (the one from which an owner is being removed).
     */
    @Test
    void testOwnerIsRemoved(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        final var owner = TestHelper.validOwner();
        doNothing().when(exam).removeOwner(owner);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(examRepository.save(any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.removeOwnerFromExam(examId, owner),
                "An unexpected exception was thrown"
        );
        verify(exam, only()).removeOwner(owner);
        verify(examRepository, times(1)).findById(examId);
        verify(examRepository, times(1)).save(exam);
        verifyNoMoreInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(testCaseRepository);
    }

    /**
     * Tests that deleting an upcoming exam is performed as expected.
     *
     * @param exam A mocked {@link Exam} (the one being deleted).
     */
    @Test
    void testDeleteOfUpcomingExam(@Mock(name = "exam") final Exam exam) {
        final var id = TestHelper.validExamId();
        when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        doNothing().when(examRepository).delete(exam);
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Assertions.assertDoesNotThrow(
                () -> examManager.deleteExam(id),
                "Deleting an exam throws an exception"
        );
        verify(examRepository, times(1)).findById(id);
        verify(examRepository, times(1)).delete(exam);
        verifyNoMoreInteractions(examRepository);
        verify(exerciseRepository, only()).deleteExamExercises(exam);
        verify(testCaseRepository, only()).deleteExamTestCases(exam);
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    /**
     * Tests that the {@link List} of {@link Exercise}s belonging to a given {@link Exam} is returned as expected.
     *
     * @param exam            A mocked {@link Exam} (the owner of the {@link Exercise}s).
     * @param mockedExercises A mocked {@link List} of {@link Exercise}s owned by the {@link Exam}.
     */
    @Test
    void testGetExamExercises(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "mockedExercises") final List<Exercise> mockedExercises) {
        final var id = TestHelper.validExamId();
        when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        when(exerciseRepository.getExamExercises(exam)).thenReturn(mockedExercises);
        final var exercises = examManager.getExercises(id);
        Assertions.assertEquals(
                mockedExercises,
                exercises,
                "The returned exercises list is not the one returned by the repository"
        );
        verify(examRepository, only()).findById(id);
        verify(exerciseRepository, only()).getExamExercises(exam);
        verifyZeroInteractions(testCaseRepository);
    }

    /**
     * Tests that clearing an upcoming exam exercises is performed as expected.
     *
     * @param exam A mocked {@link Exam} (the owner of the {@link Exercise}s).
     */
    @Test
    void testClearExercisesOfUpcomingExam(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertDoesNotThrow(
                () -> examManager.clearExercises(examId),
                "Clearing exam's exercises throws an exception"
        );
        verify(examRepository, only()).findById(examId);
        verify(exerciseRepository, only()).deleteExamExercises(exam);
        verify(testCaseRepository, only()).deleteExamTestCases(exam);
    }

    /**
     * Tests that creating an exercise for an upcoming exam is performed as expected.
     *
     * @param exam A mocked {@link Exam} (the future owner of the {@link Exercise}).
     */
    @Test
    void testCreateExerciseWithValidArgumentsForUpcomingExam(@Mock(name = "exam") final Exam exam) {
        final var question = TestHelper.validExerciseQuestion();
        final var language = TestHelper.validLanguage();
        final var solutionTemplate = TestHelper.validSolutionTemplate();
        final var awardedScore = TestHelper.validAwardedScore();
        final var examId = TestHelper.validExamId();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(exerciseRepository.save(any(Exercise.class))).then(invocation -> invocation.getArgument(0));
        final var exercise = examManager.createExercise(examId, question, language, solutionTemplate, awardedScore);
        Assertions.assertAll("Exercise properties are not the expected",
                () -> Assertions.assertEquals(
                        question,
                        exercise.getQuestion(),
                        "There is a mismatch in the question"
                ),
                () -> Assertions.assertEquals(
                        language,
                        exercise.getLanguage(),
                        "There is a mismatch in the language"
                ),
                () -> Assertions.assertEquals(
                        solutionTemplate,
                        exercise.getSolutionTemplate(),
                        "There is a mismatch in the solution template"
                ),
                () -> Assertions.assertEquals(
                        awardedScore,
                        exercise.getAwardedScore(),
                        "There is a mismatch in the awarded score"
                ),
                () -> Assertions.assertEquals(
                        exam,
                        exercise.getExam(),
                        "There is a mismatch in the owner"
                )
        );
        verify(exam, only()).getState();
        verify(examRepository, only()).findById(examId);
        verify(exerciseRepository, only()).save(any(Exercise.class));
        verifyZeroInteractions(testCaseRepository);
    }

    /**
     * Tests that searching for an {@link Exercise} that exists returns the expected {@link Exercise}.
     *
     * @param exercise A mocked {@link Exercise} (which is returned by {@link ExamManager#getExercise(long)}).
     */
    @Test
    void testSearchForExerciseThatExists(@Mock(name = "exercise") final Exercise exercise) {
        final var exerciseId = TestHelper.validExerciseId();
        when(exercise.getId()).thenReturn(exerciseId);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        final var exerciseOptional = examManager.getExercise(exerciseId);
        Assertions.assertAll("Searching for an exercise that exists is not working as expected",
                () -> Assertions.assertTrue(
                        exerciseOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        exerciseId,
                        exerciseOptional.map(Exercise::getId).get().longValue(),
                        "The returned Exercise id's is not the same as the requested"
                )
        );
        verifyOnlyExerciseSearch(exerciseId);
    }

    /**
     * Tests that modifying an exercise belonging to an upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one being modified).
     */
    @Test
    void testModifyExerciseWithValidArgumentsForUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var exerciseId = TestHelper.validExerciseId();
        final var newQuestion = TestHelper.validExerciseQuestion();
        final var newLanguage = TestHelper.validLanguage();
        final var newSolutionTemplate = TestHelper.validSolutionTemplate();
        final var awardedScore = TestHelper.validAwardedScore();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(exercise.getExam()).thenReturn(exam);
        doNothing().when(exercise).update(newQuestion, newLanguage, newSolutionTemplate, awardedScore);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(exerciseRepository.save(any(Exercise.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.modifyExercise(exerciseId, newQuestion, newLanguage, newSolutionTemplate, awardedScore),
                "An unexpected exception was thrown"
        );
        verify(exam, only()).getState();
        verify(exercise, times(1)).getExam();
        verify(exercise, times(1)).update(newQuestion, newLanguage, newSolutionTemplate, awardedScore);
        verifyNoMoreInteractions(exercise);
        verifyZeroInteractions(examRepository);
        verify(exerciseRepository, times(1)).findById(exerciseId);
        verify(exerciseRepository, times(1)).save(exercise);
        verifyNoMoreInteractions(exerciseRepository);
        verifyZeroInteractions(testCaseRepository);
    }

    /**
     * Tests that deleting an exercise belonging to an upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one being deleted).
     */
    @Test
    void testDeleteExerciseBelongingToUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var exerciseId = TestHelper.validExerciseId();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(exercise.getExam()).thenReturn(exam);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        doNothing().when(exerciseRepository).delete(exercise);
        Assertions.assertDoesNotThrow(
                () -> examManager.deleteExercise(exerciseId),
                "Deleting an exercise throws an exception"
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verifyZeroInteractions(examRepository);
        verify(exerciseRepository, times(1)).findById(exerciseId);
        verify(exerciseRepository, times(1)).delete(exercise);
        verifyNoMoreInteractions(exerciseRepository);
        verify(testCaseRepository, only()).deleteExerciseTestCases(exercise);
    }


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    /**
     * Tests that listing an exercise's private test cases is performed as expected.
     *
     * @param exercise        A mocked {@link Exercise} (the owner of the {@link TestCase}s).
     * @param mockedTestCases A mocked {@link List} of {@link TestCase}s owned by the {@link Exercise}.
     */
    @Test
    void testListExercisePrivateTestCases(
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "mockedPrivateTestCases") final List<TestCase> mockedTestCases) {
        final var exerciseId = TestHelper.validExerciseId();
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(testCaseRepository.getExercisePrivateTestCases(exercise)).thenReturn(mockedTestCases);
        final var testCases = examManager.getPrivateTestCases(exerciseId);
        Assertions.assertEquals(
                mockedTestCases,
                testCases,
                "The returned test cases list is not the one returned by the repository"
        );
        verifyZeroInteractions(examRepository);
        verify(exerciseRepository, only()).findById(exerciseId);
        verify(testCaseRepository, only()).getExercisePrivateTestCases(exercise);
    }

    /**
     * Tests that listing an exercise's public test cases is performed as expected.
     *
     * @param exercise        A mocked {@link Exercise} (the owner of the {@link TestCase}s).
     * @param mockedTestCases A mocked {@link List} of {@link TestCase}s owned by the {@link Exercise}.
     */
    @Test
    void testListExercisePublicTestCases(
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "mockedPublicTestCases") final List<TestCase> mockedTestCases) {
        final var exerciseId = TestHelper.validExerciseId();
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(testCaseRepository.getExercisePublicTestCases(exercise)).thenReturn(mockedTestCases);
        final var testCases = examManager.getPublicTestCases(exerciseId);
        Assertions.assertEquals(
                mockedTestCases,
                testCases,
                "The returned test cases list is not the one returned by the repository"
        );
        verifyZeroInteractions(examRepository);
        verify(exerciseRepository, only()).findById(exerciseId);
        verify(testCaseRepository, only()).getExercisePublicTestCases(exercise);
    }

    /**
     * Tests that creating a test case for an exercise of an upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the {@link Exercise}s).
     * @param exercise A mocked {@link Exercise} (the future owner of the {@link TestCase}).
     */
    @Test
    void testCreateTestCaseWithValidArgumentsForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var visibility = TestHelper.validTestCaseVisibility();
        final var timeout = TestHelper.validTestCaseTimeout();
        final var inputs = TestHelper.validTestCaseList();
        final var expectedOutputs = TestHelper.validTestCaseList();
        final var exerciseId = TestHelper.validExerciseId();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(exercise.getExam()).thenReturn(exam);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(testCaseRepository.save(any(TestCase.class))).then(invocation -> invocation.getArgument(0));
        final var testCase = examManager.createTestCase(exerciseId, visibility, timeout, inputs, expectedOutputs);
        Assertions.assertAll("TestCase properties are not the expected",
                () -> Assertions.assertEquals(
                        visibility,
                        testCase.getVisibility(),
                        "There is a mismatch in the visibility"
                ),
                () -> Assertions.assertEquals(
                        timeout,
                        testCase.getTimeout(),
                        "There is a mismatch in the timeout"
                ),
                () -> Assertions.assertEquals(
                        inputs,
                        testCase.getInputs(),
                        "There is a mismatch in the inputs"
                ),
                () -> Assertions.assertEquals(
                        expectedOutputs,
                        testCase.getExpectedOutputs(),
                        "There is a mismatch in the expected outputs"
                ),
                () -> Assertions.assertEquals(
                        exercise,
                        testCase.getExercise(),
                        "There is a mismatch in the owner"
                )
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verifyZeroInteractions(examRepository);
        verify(exerciseRepository, only()).findById(exerciseId);
        verify(testCaseRepository, only()).save(any(TestCase.class));
    }

    /**
     * Tests that searching for a {@link TestCase} that exists returns the expected {@link TestCase}.
     *
     * @param testCase A mocked {@link TestCase} (which is returned by {@link ExamManager#getTestCase(long)}).
     */
    @Test
    void testSearchForTestCaseThatExists(@Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        when(testCase.getId()).thenReturn(testCaseId);
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        final var testCaseOptional = examManager.getTestCase(testCaseId);
        Assertions.assertAll("Searching for a test case that exists is not working as expected",
                () -> Assertions.assertTrue(
                        testCaseOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        testCaseId,
                        testCaseOptional.map(TestCase::getId).get().longValue(),
                        "The returned TestCase id's is not the same as the requested"
                )
        );
        verifyOnlyTestCaseSearch(testCaseId);
    }

    /**
     * Tests that modifying a test case belonging to an exercise of an upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one being modified).
     */
    @Test
    void testModifyTestCaseWithValidValuesForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        final var newVisibility = TestHelper.validTestCaseVisibility();
        final var newTimeout = TestHelper.validTestCaseTimeout();
        final var newInputs = TestHelper.validTestCaseList();
        final var newExpectedOutputs = TestHelper.validTestCaseList();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(exercise.getExam()).thenReturn(exam);
        when(testCase.getExercise()).thenReturn(exercise);
        doNothing().when(testCase).update(newVisibility, newTimeout, newInputs, newExpectedOutputs);
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(testCaseRepository.save(any(TestCase.class))).then(inv -> inv.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.modifyTestCase(testCaseId, newVisibility, newTimeout, newInputs, newExpectedOutputs),
                "An unexpected exception was thrown"
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verify(testCase, times(1)).getExercise();
        verify(testCase, times(1)).update(newVisibility, newTimeout, newInputs, newExpectedOutputs);
        verifyNoMoreInteractions(testCase);
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(testCaseRepository, times(1)).save(testCase);
        verifyNoMoreInteractions(testCaseRepository);
    }

    /**
     * Tests that deleting a test case of an exercise belonging to an upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one being deleted).
     */
    @Test
    void testDeleteTestCaseOfExerciseBelongingToUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        when(exercise.getExam()).thenReturn(exam);
        when(testCase.getExercise()).thenReturn(exercise);
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        doNothing().when(testCaseRepository).delete(testCase);
        Assertions.assertDoesNotThrow(
                () -> examManager.deleteTestCase(testCaseId),
                "Deleting a test case throws an exception"
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verify(testCase, only()).getExercise();
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(testCaseRepository, times(1)).delete(testCase);
        verifyNoMoreInteractions(testCaseRepository);
    }
}
