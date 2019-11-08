package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.domain.events.ExamFinishedEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExamScoredEvent;
import ar.edu.itba.cep.evaluations_service.domain.events.ExamSolutionSubmittedEvent;
import ar.edu.itba.cep.evaluations_service.domain.helpers.TestHelper;
import ar.edu.itba.cep.evaluations_service.models.*;
import ar.edu.itba.cep.evaluations_service.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission.State.UNPLACED;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link SolutionsManager}, containing tests for the happy paths
 * (i.e how the manager behaves when operating with valid values, entity states, etc.).
 */
@ExtendWith(MockitoExtension.class)
class SolutionsManagerHappyPathsTest extends AbstractSolutionsManagerTest {

    /**
     * Constructor.
     *
     * @param examRepository       An {@link ExamRepository} that is injected to the {@link SolutionsManager}.
     * @param exerciseRepository   An {@link ExerciseRepository} that is injected to the {@link SolutionsManager}.
     * @param submissionRepository An {@link ExamSolutionSubmissionRepository} that is injected to the {@link SolutionsManager}.
     * @param solutionRepository   An {@link ExerciseSolutionRepository} that is injected to the {@link SolutionsManager}.
     * @param publisher            An {@link ApplicationEventPublisher} that is injected to the {@link SolutionsManager}.
     */
    SolutionsManagerHappyPathsTest(
            @Mock(name = "examRepository") final ExamRepository examRepository,
            @Mock(name = "exerciseRepository") final ExerciseRepository exerciseRepository,
            @Mock(name = "submissionRepository") final ExamSolutionSubmissionRepository submissionRepository,
            @Mock(name = "solutionRepository") final ExerciseSolutionRepository solutionRepository,
            @Mock(name = "resultRepository") final ExerciseSolutionResultRepository resultRepository,
            @Mock(name = "eventPublisher") final ApplicationEventPublisher publisher) {
        super(examRepository, exerciseRepository, submissionRepository, solutionRepository, resultRepository, publisher);
    }


    // ================================================================================================================
    // Exam Solution Submission
    // ================================================================================================================

    /**
     * Tests that searching for an {@link ExamSolutionSubmission} that exists
     * returns the expected {@link ExamSolutionSubmission}.
     *
     * @param submission A mocked {@link ExamSolutionSubmission}
     *                   (which is returned by {@link SolutionsManager#getSubmission(long)}).
     */
    @Test
    void testSearchForSubmissionThatExists(@Mock(name = "submission") final ExamSolutionSubmission submission) {
        final var submissionsId = TestHelper.validExerciseSolutionId();
        when(submission.getId()).thenReturn(submissionsId);
        when(submissionRepository.findById(submissionsId)).thenReturn(Optional.of(submission));
        final var submissionOptional = solutionsManager.getSubmission(submissionsId);
        Assertions.assertAll("Searching for a submission that exists is not working as expected",
                () -> Assertions.assertTrue(
                        submissionOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        submissionsId,
                        submissionOptional.map(ExamSolutionSubmission::getId).get().longValue(),
                        "The returned submission id's is not the same as the requested"
                )
        );
        verifyOnlySubmissionSearch(submissionsId);
    }

    /**
     * Tests that an {@link ExamSolutionSubmission} is created (i.e is saved) when arguments are valid,
     * creating also all the corresponding {@link ExerciseSolution}s.
     *
     * @param exam            A mocked {@link Exam} (i.e the one to which the submission belongs).
     * @param exercise1       A mocked {@link Exercise} (i.e owned by the {@code exam}).
     * @param exercise2       Another mocked {@link Exercise} (i.e also owned by the {@code exam}).
     * @param authentication  A mocked {@link Authentication} that will hold a mocked principal.
     * @param securityContext A mocked {@link SecurityContext} to be retrieved from the {@link SecurityContextHolder}.
     */
    @Test
    void testSubmissionIsCreatedUsingValidArguments(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise1") final Exercise exercise1,
            @Mock(name = "exercise1") final Exercise exercise2,
            @Mock(name = "authentication") final Authentication authentication,
            @Mock(name = "securityContext") final SecurityContext securityContext) {
        final var examId = TestHelper.validExamId();
        final var submitter = TestHelper.validOwner();
        final var exercises = List.of(exercise1, exercise2);
        when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        when(exercise1.getExam()).thenReturn(exam);
        when(exercise2.getExam()).thenReturn(exam);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(submissionRepository.save(any(ExamSolutionSubmission.class))).then(i -> i.getArgument(0));
        when(submissionRepository.existsSubmissionFor(exam, submitter)).thenReturn(false);
        when(exerciseRepository.getExamExercises(exam)).thenReturn(exercises);
        when(solutionRepository.save(any(ExerciseSolution.class))).then(i -> i.getArgument(0));

        TestHelper.setupSecurityContext(submitter, authentication, securityContext);

        final var submission = solutionsManager.createExamSolutionSubmission(examId);
        Assertions.assertAll("ExamSolutionSubmission properties are not the expected",
                () -> Assertions.assertEquals(
                        exam,
                        submission.getExam(),
                        "There is a mismatch in the exam"
                ),
                () -> Assertions.assertEquals(
                        submitter,
                        submission.getSubmitter(),
                        "There is a mismatch in the submitter"
                )
        );
        verify(examRepository, only()).findById(examId);
        verify(exerciseRepository, only()).getExamExercises(exam);
        verify(submissionRepository, times(1)).existsSubmissionFor(exam, submitter);
        verify(submissionRepository, times(1)).save(any(ExamSolutionSubmission.class));
        verifyNoMoreInteractions(submissionRepository);
        exercises.forEach(e -> verify(solutionRepository, times(1))
                .save(argThat(inner -> inner.getExercise().equals(e)))
        );
        verifyNoMoreInteractions(solutionRepository);
        verifyZeroInteractions(resultRepository);
        verifyZeroInteractions(publisher);

        TestHelper.clearSecurityContext();
    }

    /**
     * Tests that solutions are submitted, sending the corresponding {@link ExamSolutionSubmittedEvent}.
     *
     * @param submission A mocked {@link ExamSolutionSubmission} (i.e the one being submitted)
     * @param exam       A mocked {@link Exam} (i.e the one to which the submission belongs).
     */
    @Test
    void testExamSolutionSubmission(
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "exam") final Exam exam) {
        final var submissionsId = TestHelper.validExerciseSolutionId();
        when(submission.getExam()).thenReturn(exam);
        doNothing().when(submission).submit();
        when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        when(submissionRepository.findById(submissionsId)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(submission)).thenReturn(submission);
        doNothing().when(publisher).publishEvent(any(ExamSolutionSubmittedEvent.class));
        Assertions.assertDoesNotThrow(
                () -> solutionsManager.submitSolutions(submissionsId),
                "An unexpected exception is thrown when submitting an exam solution"
        );
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(solutionRepository);
        verify(submissionRepository, times(1)).findById(submissionsId);
        verify(submissionRepository, times(1)).save(submission);
        verifyNoMoreInteractions(submissionRepository);
        verifyZeroInteractions(solutionRepository);
        verifyZeroInteractions(resultRepository);
        verify(publisher, only()).publishEvent(argThat(eventContainsSubmission(submission)));
    }

    /**
     * Tests that re-scoring an {@link ExamSolutionSubmission} does not do anything.
     *
     * @param submission A mocked {@link ExamSolutionSubmission} (the one being re-scored).
     */
    @Test
    void testReScoringDoesNotDoAnything(@Mock(name = "submission") final ExamSolutionSubmission submission) {
        final var submissionId = TestHelper.validExamSolutionSubmissionId();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(submission.getScore()).thenReturn(TestHelper.validScore());

        solutionsManager.scoreSubmission(submissionId);

        verify(submission, only()).getScore();
        verifyOnlySubmissionSearch(submissionId);
    }

    /**
     * Tests that scoring an {@link ExamSolutionSubmission}
     * that contains approved and not-approved {@link ExerciseSolution}s works as expected.
     *
     * @param submission A mocked {@link ExamSolutionSubmission} (the one being scored).
     * @param solution1  A mocked {@link ExerciseSolution} (represents a solution belonging to the {@code submission}).
     * @param solution2  A mocked {@link ExerciseSolution} (represents a solution belonging to the {@code submission}).
     * @param result1a   A mocked {@link ExerciseSolutionResult} (represents a result of the {@code solution1}).
     * @param result1b   A mocked {@link ExerciseSolutionResult} (represents a result of the {@code solution1}).
     * @param result2a   A mocked {@link ExerciseSolutionResult} (represents a result of the {@code solution2}).
     * @param result2b   A mocked {@link ExerciseSolutionResult} (represents a result of the {@code solution2}).
     */
    @Test
    void testScoring(
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "exercise1") final Exercise exercise1,
            @Mock(name = "solution1") final ExerciseSolution solution1,
            @Mock(name = "solution2") final ExerciseSolution solution2,
            @Mock(name = "result1a") final ExerciseSolutionResult result1a,
            @Mock(name = "result1b") final ExerciseSolutionResult result1b,
            @Mock(name = "result2a") final ExerciseSolutionResult result2a,
            @Mock(name = "result2b") final ExerciseSolutionResult result2b) {
        final var submissionId = TestHelper.validExamSolutionSubmissionId();
        final var exercise1AwardedScore = TestHelper.validAwardedScore();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(submission.getScore()).thenReturn(null);
        when(submission.getState()).thenReturn(ExamSolutionSubmission.State.SUBMITTED);
        doNothing().when(submission).score(exercise1AwardedScore);
        when(solution1.getExercise()).thenReturn(exercise1);
        when(exercise1.getAwardedScore()).thenReturn(exercise1AwardedScore);
        // We assume that all but one are approved
        when(result1a.isMarked()).thenReturn(true);
        when(result1b.isMarked()).thenReturn(true);
        when(result2a.isMarked()).thenReturn(true);
        when(result2b.isMarked()).thenReturn(true);
        when(result1a.getResult()).thenReturn(ExerciseSolutionResult.Result.APPROVED);
        when(result1b.getResult()).thenReturn(ExerciseSolutionResult.Result.APPROVED);
        when(result2a.getResult()).thenReturn(ExerciseSolutionResult.Result.APPROVED);
        when(result2b.getResult()).thenReturn(TestHelper.notApprovedResult());
        when(solutionRepository.getExerciseSolutions(submission)).thenReturn(List.of(solution1, solution2));
        when(resultRepository.find(solution1)).thenReturn(List.of(result1a, result1b));
        when(resultRepository.find(solution2)).thenReturn(List.of(result2a, result2b));

        solutionsManager.scoreSubmission(submissionId);

        verify(submission, times(1)).getState();
        verify(submission, times(1)).getScore();
        verify(submission, times(1)).score(exercise1AwardedScore);
        verifyNoMoreInteractions(submission);
        verify(solution1, only()).getExercise();
        verifyZeroInteractions(solution2); // The failed solution's exercise is not retrieved.
        verify(result1a, atMost(1)).isMarked();
        verify(result1a, atMost(1)).getResult();
        verifyNoMoreInteractions(result1a);
        verify(result1b, atMost(1)).isMarked();
        verify(result1b, atMost(1)).getResult();
        verifyNoMoreInteractions(result1b);
        verify(result2a, atMost(1)).isMarked();
        verify(result2a, atMost(1)).getResult();
        verifyNoMoreInteractions(result2a);
        verify(result2b, atMost(1)).isMarked();
        verify(result2b, atMost(1)).getResult();
        verifyNoMoreInteractions(result2b);
        verify(exercise1, only()).getAwardedScore();
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verify(submissionRepository, times(1)).findById(submissionId);
        verify(submissionRepository, times(1)).save(submission);
        verify(solutionRepository, only()).getExerciseSolutions(submission);
        verify(resultRepository, atMost(1)).find(solution1);
        verify(resultRepository, atMost(1)).find(solution2);
        verifyNoMoreInteractions(resultRepository);
        verify(publisher, only())
                .publishEvent(argThat(eventContainsSubmissionAndScore(submission, exercise1AwardedScore)));
    }


    // ================================================================================================================
    // Exercises Solutions
    // ================================================================================================================

    /**
     * Tests that the {@link List} of {@link ExerciseSolution}s belonging to a given {@link ExamSolutionSubmission}
     * is returned as expected.
     *
     * @param submission A mocked {@link ExamSolutionSubmission} (the owner of the {@link ExerciseSolution}s).
     * @param solutions  A mocked {@link List} of {@link ExerciseSolution}s owned by the {@link ExamSolutionSubmission}.
     */
    @Test
    void testGetExerciseSolutions(
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "solutions") final List<ExerciseSolution> solutions) {
        final var submissionId = TestHelper.validExamSolutionSubmissionId();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(solutionRepository.getExerciseSolutions(submission)).thenReturn(solutions);
        Assertions.assertEquals(
                solutions,
                solutionsManager.getSolutionsForSubmission(submissionId),
                "The returned solutions list is not the one returned by the repository"
        );
        verify(submissionRepository, only()).findById(submissionId);
        verify(solutionRepository, only()).getExerciseSolutions(submission);
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(resultRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests that searching for an {@link ExerciseSolution} that exists returns the expected {@link ExerciseSolution}.
     *
     * @param solution A mocked {@link ExerciseSolution}
     *                 (which is returned by {@link SolutionsManager#getSolution(long)}).
     */
    @Test
    void testSearchForExerciseSolutionThatExists(@Mock(name = "solution") final ExerciseSolution solution) {
        final var solutionId = TestHelper.validExerciseSolutionId();
        when(solution.getId()).thenReturn(solutionId);
        when(solutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        final var solutionOptional = solutionsManager.getSolution(solutionId);
        Assertions.assertAll("Searching for an exercise solution that exists is not working as expected",
                () -> Assertions.assertTrue(
                        solutionOptional.isPresent(),
                        "The returned Optional is empty"
                ),
                () -> Assertions.assertEquals(
                        solutionId,
                        solutionOptional.map(ExerciseSolution::getId).get().longValue(),
                        "The returned ExerciseSolution id's is not the same as the requested"
                )
        );
        verifyOnlySolutionSearch(solutionId);
    }

    /**
     * Tests that modifying an {@link ExerciseSolution} belonging to an {@link Exercise} of an in progress {@link Exam},
     * and an {@link ExamSolutionSubmission} that is not submitted is performed as expected.
     *
     * @param exam     A mocked {@link Exam} (the owner of the exercise).
     * @param exercise A mocked {@link Exercise} (the one being modified).
     */
    @Test
    void testModifyExerciseSolutionWithValidArgumentsForUpcomingExam(
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "exercise") final Exercise exercise,
            @Mock(name = "submission") final ExamSolutionSubmission submission,
            @Mock(name = "solution") final ExerciseSolution solution) {

        final var solutionId = TestHelper.validExerciseSolutionId();
        final var answer = TestHelper.validExerciseSolutionAnswer();
        final var compilerFlags = TestHelper.validExerciseSolutionAnswer();
        final var mainFileName = TestHelper.validMainFileName();
        when(exercise.getExam()).thenReturn(exam);
        when(solution.getExercise()).thenReturn(exercise);
        when(solution.getSubmission()).thenReturn(submission);
        when(exam.getState()).thenReturn(Exam.State.IN_PROGRESS);
        when(submission.getState()).thenReturn(UNPLACED);
        doNothing().when(solution).setAnswer(answer);
        doNothing().when(solution).setCompilerFlags(compilerFlags);
        doNothing().when(solution).setMainFileName(mainFileName);
        when(solutionRepository.findById(solutionId)).thenReturn(Optional.of(solution));
        when(solutionRepository.save(solution)).thenReturn(solution);
        Assertions.assertDoesNotThrow(
                () -> solutionsManager.modifySolution(solutionId, answer, compilerFlags, mainFileName),
                "An unexpected exception was thrown"
        );
        verify(exam, only()).getState();
        verify(exercise, only()).getExam();
        verify(submission, only()).getState();
        verify(solution, times(1)).getExercise();
        verify(solution, times(1)).setAnswer(answer);
        verify(solution, times(1)).setCompilerFlags(compilerFlags);
        verify(solution, times(1)).setMainFileName(mainFileName);
        verifyNoMoreInteractions(solution);
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(exerciseRepository);
        verifyZeroInteractions(submissionRepository);
        verify(solutionRepository, times(1)).findById(solutionId);
        verify(solutionRepository, times(1)).save(solution);
        verifyNoMoreInteractions(solutionRepository);
        verifyZeroInteractions(resultRepository);
        verifyZeroInteractions(publisher);
    }


    // ================================================================================================================
    // Event Listeners
    // ================================================================================================================

    /**
     * Tests the the reception of an {@link ExamFinishedEvent} is handled as expected
     * (retrieves {@link ExamSolutionSubmission}s that are not placed, then they are submitted, and then the
     * corresponding event is streamed).
     *
     * @param event               An {@link ExamFinishedEvent} mock (the one being received).
     * @param exam                An {@link Exam} mock (the one that has finished).
     * @param placedSubmission    An {@link ExamSolutionSubmission} mock (one that has already been placed).
     * @param unplacedSubmission1 An {@link ExamSolutionSubmission} mock (one that has not been placed).
     * @param unplacedSubmission2 An {@link ExamSolutionSubmission} mock (another one that has not been placed).
     */
    @Test
    void testExamFinishedEventReception(
            @Mock(name = "event") final ExamFinishedEvent event,
            @Mock(name = "exam") final Exam exam,
            @Mock(name = "placedSubmission") final ExamSolutionSubmission placedSubmission,
            @Mock(name = "unplacedSubmission1") final ExamSolutionSubmission unplacedSubmission1,
            @Mock(name = "unplacedSubmission2") final ExamSolutionSubmission unplacedSubmission2) {
        when(event.getExam()).thenReturn(exam);
        when(submissionRepository.getByExamAndState(exam, UNPLACED))
                .thenReturn(List.of(unplacedSubmission1, unplacedSubmission2));
        doNothing().when(unplacedSubmission1).submit();
        when(submissionRepository.save(unplacedSubmission1)).thenReturn(unplacedSubmission1);
        when(submissionRepository.save(unplacedSubmission2)).thenReturn(unplacedSubmission2);
        doNothing().when(publisher).publishEvent(any(ExamSolutionSubmittedEvent.class));

        solutionsManager.examFinished(event);

        verifyZeroInteractions(exam);
        verifyZeroInteractions(placedSubmission);
        verify(unplacedSubmission1, only()).submit();
        verify(unplacedSubmission2, only()).submit();
        verifyZeroInteractions(examRepository);
        verifyZeroInteractions(solutionRepository);
        verify(submissionRepository, times(1)).getByExamAndState(exam, UNPLACED);
        verify(submissionRepository, times(1)).save(unplacedSubmission1);
        verify(submissionRepository, times(1)).save(unplacedSubmission2);
        verifyNoMoreInteractions(submissionRepository);
        verifyZeroInteractions(solutionRepository);
        verifyZeroInteractions(resultRepository);
        verify(publisher, times(1)).publishEvent(argThat(eventContainsSubmission(unplacedSubmission1)));
        verify(publisher, times(1)).publishEvent(argThat(eventContainsSubmission(unplacedSubmission2)));
        verifyNoMoreInteractions(publisher);
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Creates an {@link ArgumentMatcher} of {@link ExamSolutionSubmittedEvent} to check if the said event
     * contains the given {@code submission}.
     *
     * @param submission The {@link ExamSolutionSubmission} to be checked.
     * @return The {@link ArgumentMatcher}.
     */
    private static ArgumentMatcher<ExamSolutionSubmittedEvent> eventContainsSubmission(
            final ExamSolutionSubmission submission) {
        return event -> event.getSubmission().equals(submission);
    }

    /**
     * Creates an {@link ArgumentMatcher} of {@link ExamScoredEvent} to check if the said event
     * contains the given {@code submission} and {@code score}.
     *
     * @param submission The {@link ExamSolutionSubmission} to be checked.
     * @param score      The score to be checked.
     * @return The {@link ArgumentMatcher}.
     */
    private static ArgumentMatcher<ExamScoredEvent> eventContainsSubmissionAndScore(
            final ExamSolutionSubmission submission, final int score) {
        return event -> event.getSubmission().equals(submission);
    }
}
