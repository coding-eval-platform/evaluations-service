package ar.edu.itba.cep.evaluations_service.domain;

import ar.edu.itba.cep.evaluations_service.models.*;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import com.bellotapps.webapps_commons.persistence.repository_utils.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.PagingRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * Test class for {@link ExamManager}, containing tests for the happy paths
 * (i.e how the manager behaves when operating with valid values, entity states, etc.).
 */
@ExtendWith(MockitoExtension.class)
class ExamManagerHappyPathsTest extends AbstractExamManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository             A mocked {@link ExamRepository} passed to super class.
     * @param exerciseRepository         A mocked {@link ExerciseRepository} passed to super class.
     * @param testCaseRepository         A mocked {@link TestCaseRepository} passed to super class.
     * @param exerciseSolutionRepository A mocked {@link ExamRepository} passed to super class.
     * @param exerciseSolResultRep       A mocked {@link ExerciseSolutionResultRepository} passed to super class.
     */
    ExamManagerHappyPathsTest(
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
     * Tests that searching for an {@link Exam} that exists returns the expected {@link Exam}.
     *
     * @param exam A mocked {@link Exam} (which is returned by {@link ExamManager#getExam(long)}).
     */
    @Test
    void testSearchForExamThatExists(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        Mockito.when(exam.getId()).thenReturn(examId);
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        final var examOptional = examManager.getExam(examId);
        Assertions.assertAll("Searching for an exam that exists is not working as expected",
                () -> Assertions.assertTrue(
                        examOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        examId,
                        examOptional.map(Exam::getId).get().longValue(),
                        "The returned Exam id's is not the same as the requested"
                )
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    /**
     * Tests that an {@link Exam} is created (i.e is saved) when arguments are valid.
     */
    @Test
    void testExamIsCreatedUsingValidArguments() {
        final var description = TestHelper.validExamDescription();
        final var startingAt = TestHelper.validExamStartingMoment();
        final var duration = TestHelper.validExamDuration();
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
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
        Mockito.verify(examRepository, Mockito.only()).save(Mockito.any(Exam.class));
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
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
        setExamStateField(exam, Exam.State.UPCOMING);
        Mockito.doNothing().when(exam).update(newDescription, newStartingAt, newDuration);
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.modifyExam(examId, newDescription, newStartingAt, newDuration),
                "An unexpected exception was thrown"
        );
        Mockito.verify(exam, Mockito.only()).update(newDescription, newStartingAt, newDuration);
        Mockito.verify(examRepository, Mockito.times(1)).findById(examId);
        Mockito.verify(examRepository, Mockito.times(1)).save(Mockito.any(Exam.class));
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that starting an upcoming {@link Exam} works as expected
     * (changes the state and then saves the exam instance).
     *
     * @param exam A mocked {@link Exam} (the one being started).
     */
    @Test
    void testExamIsStartedWhenIsUpcoming(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        Mockito.doNothing().when(exam).startExam();
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.startExam(examId),
                "An unexpected exception was thrown"
        );
        Mockito.verify(exam, Mockito.only()).startExam();
        Mockito.verify(examRepository, Mockito.times(1)).findById(examId);
        Mockito.verify(examRepository, Mockito.times(1)).save(exam);
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
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
        Mockito.doNothing().when(exam).finishExam();
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Mockito.when(examRepository.save(Mockito.any(Exam.class))).then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.finishExam(examId),
                "An unexpected exception was thrown"
        );
        Mockito.verify(exam, Mockito.only()).finishExam();
        Mockito.verify(examRepository, Mockito.times(1)).findById(examId);
        Mockito.verify(examRepository, Mockito.times(1)).save(exam);
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that deleting an upcoming exam is performed as expected.
     *
     * @param exam A mocked {@link Exam} (the one being deleted).
     */
    @Test
    void testDeleteOfUpcomingExam(@Mock(name = "exam") final Exam exam) {
        final var id = TestHelper.validExamId();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.doNothing().when(examRepository).delete(exam);
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Assertions.assertDoesNotThrow(
                () -> examManager.deleteExam(id),
                "Deleting an exam throws an exception"
        );
        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verify(examRepository, Mockito.times(1)).delete(exam);
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verify(exerciseRepository, Mockito.only()).deleteExamExercises(exam);
        Mockito.verify(testCaseRepository, Mockito.only()).deleteExamTestCases(exam);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

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
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exerciseRepository.getExamExercises(exam)).thenReturn(mockedExercises);
        final var exercises = examManager.getExercises(id);
        Assertions.assertEquals(
                mockedExercises,
                exercises,
                "The returned exercises list is not the one returned by the repository"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(id);
        Mockito.verify(exerciseRepository, Mockito.only()).getExamExercises(exam);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that clearing an upcoming exam exercises is performed as expected.
     *
     * @param exam A mocked {@link Exam} (the owner of the {@link Exercise}s).
     */
    @Test
    void testClearExercisesOfUpcomingExam(@Mock(name = "exam") final Exam exam) {
        final var examId = TestHelper.validExamId();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Assertions.assertDoesNotThrow(
                () -> examManager.clearExercises(examId),
                "Clearing exam's exercises throws an exception"
        );
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verify(exerciseRepository, Mockito.only()).deleteExamExercises(exam);
        Mockito.verify(testCaseRepository, Mockito.only()).deleteExamTestCases(exam);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================================================================================================
    // Exercises
    // ================================================================================================================

    /**
     * Tests that creating an exercise for an upcoming exam is performed as expected.
     *
     * @param exam A mocked {@link Exam} (the future owner of the {@link Exercise}).
     */
    @Test
    void testCreateExerciseWithValidArgumentsForUpcomingExam(@Mock(name = "exam") final Exam exam) {
        final var question = TestHelper.validExerciseQuestion();
        final var examId = TestHelper.validExamId();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        Mockito
                .when(exerciseRepository.save(Mockito.any(Exercise.class)))
                .then(invocation -> invocation.getArgument(0));
        final var exercise = examManager.createExercise(examId, question);
        Assertions.assertAll("Exercise properties are not the expected",
                () -> Assertions.assertEquals(
                        question,
                        exercise.getQuestion(),
                        "There is a mismatch in the question"
                ),
                () -> Assertions.assertEquals(
                        exam,
                        exercise.belongsToExam(),
                        "There is a mismatch in the owner"
                )
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(examRepository, Mockito.only()).findById(examId);
        Mockito.verify(exerciseRepository, Mockito.only()).save(Mockito.any(Exercise.class));
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that changing the question of an exercise belonging to an upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one whose question is being changed).
     */
    @Test
    void testChangeExerciseQuestionWithValidQuestionForUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var exerciseId = TestHelper.validExerciseId();
        final var newQuestion = TestHelper.validExerciseQuestion();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.doNothing().when(exercise).setQuestion(newQuestion);
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Mockito
                .when(exerciseRepository.save(Mockito.any(Exercise.class)))
                .then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.changeExerciseQuestion(exerciseId, newQuestion),
                "An unexpected exception was thrown"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.times(1)).belongsToExam();
        Mockito.verify(exercise, Mockito.times(1)).setQuestion(newQuestion);
        Mockito.verifyNoMoreInteractions(exercise);
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verify(exerciseRepository, Mockito.times(1)).findById(exerciseId);
        Mockito.verify(exerciseRepository, Mockito.times(1)).save(exercise);
        Mockito.verifyNoMoreInteractions(exerciseRepository);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
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
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Mockito.doNothing().when(exerciseRepository).delete(exercise);
        Assertions.assertDoesNotThrow(
                () -> examManager.deleteExercise(exerciseId),
                "Deleting an exercise throws an exception"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).belongsToExam();
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verify(exerciseRepository, Mockito.times(1)).findById(exerciseId);
        Mockito.verify(exerciseRepository, Mockito.times(1)).delete(exercise);
        Mockito.verifyNoMoreInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.only()).deleteExerciseTestCases(exercise);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

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
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Mockito.when(testCaseRepository.getExercisePrivateTestCases(exercise)).thenReturn(mockedTestCases);
        final var testCases = examManager.getPrivateTestCases(exerciseId);
        Assertions.assertEquals(
                mockedTestCases,
                testCases,
                "The returned test cases list is not the one returned by the repository"
        );
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verify(exerciseRepository, Mockito.only()).findById(exerciseId);
        Mockito.verify(testCaseRepository, Mockito.only()).getExercisePrivateTestCases(exercise);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
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
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Mockito.when(testCaseRepository.getExercisePublicTestCases(exercise)).thenReturn(mockedTestCases);
        final var testCases = examManager.getPublicTestCases(exerciseId);
        Assertions.assertEquals(
                mockedTestCases,
                testCases,
                "The returned test cases list is not the one returned by the repository"
        );
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verify(exerciseRepository, Mockito.only()).findById(exerciseId);
        Mockito.verify(testCaseRepository, Mockito.only()).getExercisePublicTestCases(exercise);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    /**
     * Tests that listing an exercise's solutions is performed as expected.
     *
     * @param exercise           A mocked {@link Exercise} (the owner of the {@link TestCase}s).
     * @param pagingRequest      A mocked {@link PagingRequest} to be passed to the {@link ExerciseSolutionRepository}.
     * @param mockedExeSolutions A mocked {@link Page} of {@link ExerciseSolution}s belonging to the {@link Exercise}.
     */
    @Test
    void testListExerciseSolutions(
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "pagingRequest") final PagingRequest pagingRequest,
            @Mock(name = "mockedSolutions") final Page<ExerciseSolution> mockedExeSolutions) {
        final var exerciseId = TestHelper.validExerciseId();
        Mockito
                .when(exerciseRepository.findById(exerciseId))
                .thenReturn(Optional.of(exercise));
        Mockito
                .when(exerciseSolutionRepository.getExerciseSolutions(exercise, pagingRequest))
                .thenReturn(mockedExeSolutions);
        final var solutions = examManager.listSolutions(exerciseId, pagingRequest);
        Assertions.assertEquals(
                mockedExeSolutions,
                solutions,
                "The returned solutions is not the one returned by the repository"
        );
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verify(exerciseRepository, Mockito.only()).findById(exerciseId);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verify(exerciseSolutionRepository, Mockito.only()).getExerciseSolutions(exercise, pagingRequest);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================================================================================================
    // Test Cases
    // ================================================================================================================

    /**
     * Tests that creating an exercise for an upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the {@link Exercise}s).
     * @param exercise A mocked {@link Exercise} (the future owner of the {@link TestCase}).
     */
    @Test
    void testCreateTestCaseWithValidArgumentsForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var visibility = TestHelper.validTestCaseVisibility();
        final var inputs = TestHelper.validTestCaseList();
        final var expectedOutputs = TestHelper.validTestCaseList();
        final var exerciseId = TestHelper.validExerciseId();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Mockito
                .when(testCaseRepository.save(Mockito.any(TestCase.class)))
                .then(invocation -> invocation.getArgument(0));
        final var testCase = examManager.createTestCase(exerciseId, visibility, inputs, expectedOutputs);
        Assertions.assertAll("TestCase properties are not the expected",
                () -> Assertions.assertEquals(
                        visibility,
                        testCase.getVisibility(),
                        "There is a mismatch in the visibility"
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
                        testCase.belongsToExercise(),
                        "There is a mismatch in the owner"
                )
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).belongsToExam();
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verify(exerciseRepository, Mockito.only()).findById(exerciseId);
        Mockito.verify(testCaseRepository, Mockito.only()).save(Mockito.any(TestCase.class));
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that changing the visibility of a test case belonging to an exercise
     * of upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one whose visibility is being changed).
     */
    @Test
    void testChangeVisibilityWithValidValueForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        final var newVisibility = TestHelper.validTestCaseVisibility();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.when(testCase.belongsToExercise()).thenReturn(exercise);
        Mockito.doNothing().when(testCase).setVisibility(newVisibility);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Mockito
                .when(testCaseRepository.save(Mockito.any(TestCase.class)))
                .then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.changeVisibility(testCaseId, newVisibility),
                "An unexpected exception was thrown"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).belongsToExam();
        Mockito.verify(testCase, Mockito.times(1)).belongsToExercise();
        Mockito.verify(testCase, Mockito.times(1)).setVisibility(newVisibility);
        Mockito.verifyNoMoreInteractions(testCase);
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.times(1)).findById(testCaseId);
        Mockito.verify(testCaseRepository, Mockito.times(1)).save(testCase);
        Mockito.verifyNoMoreInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that changing the inputs of a test case belonging to an exercise
     * of upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one whose inputs are being changed).
     */
    @Test
    void testChangeInputsWithValidValueForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        final var newInputs = TestHelper.validTestCaseList();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.when(testCase.belongsToExercise()).thenReturn(exercise);
        Mockito.doNothing().when(testCase).setInputs(newInputs);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Mockito
                .when(testCaseRepository.save(Mockito.any(TestCase.class)))
                .then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.changeInputs(testCaseId, newInputs),
                "An unexpected exception was thrown"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).belongsToExam();
        Mockito.verify(testCase, Mockito.times(1)).belongsToExercise();
        Mockito.verify(testCase, Mockito.times(1)).setInputs(newInputs);
        Mockito.verifyNoMoreInteractions(testCase);
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.times(1)).findById(testCaseId);
        Mockito.verify(testCaseRepository, Mockito.times(1)).save(testCase);
        Mockito.verifyNoMoreInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that changing the expected outputs of a test case belonging to an exercise
     * of upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one whose expected outputs are being changed).
     */
    @Test
    void testChangeExpectedOutputsWithValidValueForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        final var newExpectedOutputs = TestHelper.validTestCaseList();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.when(testCase.belongsToExercise()).thenReturn(exercise);
        Mockito.doNothing().when(testCase).setExpectedOutputs(newExpectedOutputs);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Mockito
                .when(testCaseRepository.save(Mockito.any(TestCase.class)))
                .then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.changeExpectedOutputs(testCaseId, newExpectedOutputs),
                "An unexpected exception was thrown"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).belongsToExam();
        Mockito.verify(testCase, Mockito.times(1)).belongsToExercise();
        Mockito.verify(testCase, Mockito.times(1)).setExpectedOutputs(newExpectedOutputs);
        Mockito.verifyNoMoreInteractions(testCase);
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.times(1)).findById(testCaseId);
        Mockito.verify(testCaseRepository, Mockito.times(1)).save(testCase);
        Mockito.verifyNoMoreInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that clearing the inputs of a test case belonging to an exercise
     * of upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one whose inputs are being cleared).
     */
    @Test
    void testClearInputsWithValidValueForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.when(testCase.belongsToExercise()).thenReturn(exercise);
        Mockito.doNothing().when(testCase).removeAllInputs();
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Mockito
                .when(testCaseRepository.save(Mockito.any(TestCase.class)))
                .then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.clearInputs(testCaseId),
                "An unexpected exception was thrown"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).belongsToExam();
        Mockito.verify(testCase, Mockito.times(1)).belongsToExercise();
        Mockito.verify(testCase, Mockito.times(1)).removeAllInputs();
        Mockito.verifyNoMoreInteractions(testCase);
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.times(1)).findById(testCaseId);
        Mockito.verify(testCaseRepository, Mockito.times(1)).save(testCase);
        Mockito.verifyNoMoreInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }

    /**
     * Tests that clearing the expected outputs of a test case belonging to an exercise
     * of upcoming exam is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the owner of the test case)
     * @param testCase A mocked {@link TestCase} (the one whose expected outputs are being cleared).
     */
    @Test
    void testClearExpectedOutputsWithValidValueForExerciseOfUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "testCase") final TestCase testCase) {
        final var testCaseId = TestHelper.validTestCaseId();
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.when(testCase.belongsToExercise()).thenReturn(exercise);
        Mockito.doNothing().when(testCase).removeAllExpectedOutputs();
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Mockito
                .when(testCaseRepository.save(Mockito.any(TestCase.class)))
                .then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.clearOutputs(testCaseId),
                "An unexpected exception was thrown"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).belongsToExam();
        Mockito.verify(testCase, Mockito.times(1)).belongsToExercise();
        Mockito.verify(testCase, Mockito.times(1)).removeAllExpectedOutputs();
        Mockito.verifyNoMoreInteractions(testCase);
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.times(1)).findById(testCaseId);
        Mockito.verify(testCaseRepository, Mockito.times(1)).save(testCase);
        Mockito.verifyNoMoreInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
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
        Mockito.when(exam.getState()).thenReturn(Exam.State.UPCOMING);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.when(testCase.belongsToExercise()).thenReturn(exercise);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Mockito.doNothing().when(testCaseRepository).delete(testCase);
        Assertions.assertDoesNotThrow(
                () -> examManager.deleteTestCase(testCaseId),
                "Deleting a test case throws an exception"
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).belongsToExam();
        Mockito.verify(testCase, Mockito.only()).belongsToExercise();
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.times(1)).findById(testCaseId);
        Mockito.verify(testCaseRepository, Mockito.times(1)).delete(testCase);
        Mockito.verifyNoMoreInteractions(testCaseRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionRepository);
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================================================================================================
    // Solutions
    // ================================================================================================================

    /**
     * Tests that creating a solution for an exercise belonging to an in progress exam is performed as expected.
     *
     * @param exam     A mocked {@link Exercise} (the owner of the {@link Exercise}s).
     * @param exercise A mocked {@link Exercise} (the future owner of the {@link ExerciseSolution}).
     */
    @Test
    void testCreateSolutionForExerciseBelongingToInProgressExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise) {
        final var answer = TestHelper.validExerciseSolutionAnswer();
        final var exerciseId = TestHelper.validExerciseId();
        Mockito.when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        Mockito.when(exercise.belongsToExam()).thenReturn(exam);
        Mockito.when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        Mockito
                .when(exerciseSolutionRepository.save(Mockito.any(ExerciseSolution.class)))
                .then(invocation -> invocation.getArgument(0));
        final var solution = examManager.createExerciseSolution(exerciseId, answer);
        Assertions.assertAll("ExerciseSolution properties are not the expected",
                () -> Assertions.assertEquals(
                        answer,
                        solution.getAnswer(),
                        "There is a mismatch in the answer"
                ),
                () -> Assertions.assertEquals(
                        exercise,
                        solution.getBelongsTo(),
                        "There is a mismatch in the owner"
                )
        );
        Mockito.verify(exam, Mockito.only()).getState();
        Mockito.verify(exercise, Mockito.only()).belongsToExam();
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verify(exerciseRepository, Mockito.only()).findById(exerciseId);
        Mockito.verifyZeroInteractions(testCaseRepository);
        Mockito.verify(exerciseSolutionRepository, Mockito.only()).save(Mockito.any(ExerciseSolution.class));
        Mockito.verifyZeroInteractions(exerciseSolutionResultRepository);
    }


    // ================================================================================================================
    // Solution Results
    // ================================================================================================================

    /**
     * Tests the processing of an execution result when the exit code is zero and there is no standard error output.
     *
     * @param stderr           A mocked {@link List} of {@link String} to be used as standard error output
     *                         (i.e used to verify that the stderr {@link List} is accessed
     *                         only for checking if it is empty).
     * @param testCase         The {@link TestCase} used in the execution.
     * @param exerciseSolution The {@link ExerciseSolution} being executed.
     */
    @Test
    void testProcessExecutionWithZeroExitCodeAndEmptyStdErr(
            @Mock(name = "stderr") List<String> stderr,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "solution") final ExerciseSolution exerciseSolution) {
        final var outputs = TestHelper.validExerciseSolutionResultList();
        final var testCaseId = TestHelper.validTestCaseId();
        final var solutionId = TestHelper.validExerciseSolutionId();
        Mockito.when(stderr.isEmpty()).thenReturn(true);
        Mockito.when(testCase.getExpectedOutputs()).thenReturn(outputs);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Mockito.when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(exerciseSolution));
        Mockito
                .when(exerciseSolutionResultRepository.save(Mockito.any(ExerciseSolutionResult.class)))
                .then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.processExecution(solutionId, testCaseId, 0, outputs, stderr),
                "An exception was thrown when processing an execution result"
        );
        Mockito.verify(stderr, Mockito.only()).isEmpty();
        Mockito.verifyZeroInteractions(exerciseSolution);
        Mockito.verify(testCase, Mockito.only()).getExpectedOutputs();
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.only()).findById(testCaseId);
        Mockito.verify(exerciseSolutionRepository, Mockito.only()).findById(solutionId);
        Mockito.verify(exerciseSolutionResultRepository, Mockito.only()).save(Mockito.any(ExerciseSolutionResult.class));
    }

    /**
     * Tests the processing of an execution result when the exit code is zero but there is standard error output.
     *
     * @param stderr           A mocked {@link List} of {@link String} to be used as standard error output
     *                         (i.e used to verify that the stderr {@link List} is accessed
     *                         only for checking if it is empty).
     * @param testCase         The {@link TestCase} used in the execution.
     * @param exerciseSolution The {@link ExerciseSolution} being executed.
     */
    @Test
    void testProcessExecutionWithZeroExitCodeAndNonEmptyStdErr(
            @Mock(name = "stderr") List<String> stderr,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "solution") final ExerciseSolution exerciseSolution) {
        final var outputs = TestHelper.validExerciseSolutionResultList();
        final var testCaseId = TestHelper.validTestCaseId();
        final var solutionId = TestHelper.validExerciseSolutionId();
        Mockito.when(stderr.isEmpty()).thenReturn(false);
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Mockito.when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(exerciseSolution));
        Mockito
                .when(exerciseSolutionResultRepository.save(Mockito.any(ExerciseSolutionResult.class)))
                .then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.processExecution(solutionId, testCaseId, 0, outputs, stderr),
                "An exception was thrown when processing an execution result"
        );
        Mockito.verify(stderr, Mockito.only()).isEmpty();
        Mockito.verifyZeroInteractions(exerciseSolution);
        Mockito.verifyZeroInteractions(testCase);
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.only()).findById(testCaseId);
        Mockito.verify(exerciseSolutionRepository, Mockito.only()).findById(solutionId);
        Mockito.verify(exerciseSolutionResultRepository, Mockito.only()).save(Mockito.any(ExerciseSolutionResult.class));
    }

    /**
     * Tests the processing of an execution result when the exit code is not zero.
     *
     * @param stderr           A mocked {@link List} of {@link String} to be used as standard error output
     *                         (i.e used to verify that the stderr {@link List} is not accessed
     *                         when the exit code is not zero).
     * @param testCase         The {@link TestCase} used in the execution.
     * @param exerciseSolution The {@link ExerciseSolution} being executed.
     */
    @Test
    void testProcessExecutionWithNonZeroExitCode(
            @Mock(name = "stderr") List<String> stderr,
            @Mock(name = "testCase") final TestCase testCase,
            @Mock(name = "solution") final ExerciseSolution exerciseSolution) {
        final var exitCode = TestHelper.validExerciseSolutionExitCode();
        final var outputs = TestHelper.validExerciseSolutionResultList();
        final var testCaseId = TestHelper.validTestCaseId();
        final var solutionId = TestHelper.validExerciseSolutionId();
        Mockito.when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        Mockito.when(exerciseSolutionRepository.findById(solutionId)).thenReturn(Optional.of(exerciseSolution));
        Mockito
                .when(exerciseSolutionResultRepository.save(Mockito.any(ExerciseSolutionResult.class)))
                .then(invocation -> invocation.getArgument(0));
        Assertions.assertDoesNotThrow(
                () -> examManager.processExecution(solutionId, testCaseId, exitCode, outputs, stderr),
                "An exception was thrown when processing an execution result"
        );
        Mockito.verifyZeroInteractions(stderr);
        Mockito.verifyZeroInteractions(exerciseSolution);
        Mockito.verifyZeroInteractions(testCase);
        Mockito.verifyZeroInteractions(examRepository);
        Mockito.verifyZeroInteractions(exerciseRepository);
        Mockito.verify(testCaseRepository, Mockito.only()).findById(testCaseId);
        Mockito.verify(exerciseSolutionRepository, Mockito.only()).findById(solutionId);
        Mockito.verify(exerciseSolutionResultRepository, Mockito.only()).save(Mockito.any(ExerciseSolutionResult.class));
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Mock helpers
    // ========================================

    /**
     * Sets the given {@code state} in the given {@code exam}.
     *
     * @param exam  The {@link Exam} being altered.
     * @param state The {@link Exam.State} being set to the {@link Exam}.
     */
    private static void setExamStateField(final Exam exam, final Exam.State state) {
        try {
            FieldSetter.setField(exam, Exam.class.getDeclaredField("state"), state);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException("Cannot find field \"state\" in Exam class", e);
        }
    }
}
