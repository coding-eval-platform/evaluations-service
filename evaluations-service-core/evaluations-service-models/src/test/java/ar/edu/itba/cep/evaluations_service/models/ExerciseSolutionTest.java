package ar.edu.itba.cep.evaluations_service.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;


/**
 * Test class for {@link ExerciseSolution}s
 */
@ExtendWith(MockitoExtension.class)
class ExerciseSolutionTest {

    /**
     * A mocked {@link Exam} that will be the owner of the {@link #mockedExercise}} and the {@link #mockedSubmission}.
     */
    private final Exam mockedExam;
    /**
     * A mocked {@link Exercise} that will own the created {@link ExerciseSolution}s to be tested.
     */
    private final Exercise mockedExercise;
    /**
     * A mocked {@link ExamSolutionSubmission} that will own the created {@link ExerciseSolution}s to be tested.
     */
    private final ExamSolutionSubmission mockedSubmission;

    /**
     * Constructor.
     *
     * @param mockedExam       A mocked {@link Exam}
     *                         that will be the owner of the {@code mockedExercise}} and the {@code mockedSubmission}.
     * @param mockedExercise   A mocked {@link Exercise}
     *                         that will own the created {@link ExerciseSolution}s to be tested.
     * @param mockedSubmission A mocked {@link ExamSolutionSubmission}
     *                         that will own the created {@link ExerciseSolution}s to be tested.
     */
    ExerciseSolutionTest(
            @Mock(name = "exam") final Exam mockedExam,
            @Mock(name = "exercise") final Exercise mockedExercise,
            @Mock(name = "submission") final ExamSolutionSubmission mockedSubmission) {
        this.mockedExam = mockedExam;
        this.mockedExercise = mockedExercise;
        this.mockedSubmission = mockedSubmission;
    }


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests that creating an {@link ExerciseSolution}
     * with valid values can be performed without any exception being thrown.
     */
    @Test
    void testAcceptableArguments() {
        final var solutionTemplate = Faker.instance().lorem().characters();
        when(mockedExercise.getExam()).thenReturn(mockedExam);
        when(mockedSubmission.getExam()).thenReturn(mockedExam);
        when(mockedExercise.getSolutionTemplate()).thenReturn(solutionTemplate);
        Assertions.assertDoesNotThrow(
                this::createExerciseSolution,
                "Exercise solutions with acceptable arguments are not being created"
        );
        verify(mockedExercise, times(1)).getExam();
        verify(mockedExercise, times(1)).getSolutionTemplate();
        verifyNoMoreInteractions(mockedExercise);
        verify(mockedSubmission, only()).getExam();
    }

    /**
     * Tests that an {@link ExerciseSolution} has a {@code null} answer when it is created.
     */
    @Test
    void testAnswerHasTheExerciseSolutionTemplateWhenCreated() {
        final var solutionTemplate = Faker.instance().lorem().characters();
        when(mockedExercise.getExam()).thenReturn(mockedExam);
        when(mockedSubmission.getExam()).thenReturn(mockedExam);
        when(mockedExercise.getSolutionTemplate()).thenReturn(solutionTemplate);
        final var solution = createExerciseSolution();
        Assertions.assertEquals(
                solutionTemplate,
                solution.getAnswer(),
                "The solution's answer must be the Exercise's solution template when created.");
        verify(mockedExercise, times(1)).getExam();
        verify(mockedExercise, times(1)).getSolutionTemplate();
        verifyNoMoreInteractions(mockedExercise);
    }

    /**
     * Tests that setting a {@code null} value for an answer of an {@link ExerciseSolution} is allowed.
     */
    @Test
    void testNullAnswerIsSet() {
        final var solutionTemplate = Faker.instance().lorem().characters();
        when(mockedExercise.getExam()).thenReturn(mockedExam);
        when(mockedSubmission.getExam()).thenReturn(mockedExam);
        when(mockedExercise.getSolutionTemplate()).thenReturn(solutionTemplate);
        final var solution = createExerciseSolution();
        solution.setAnswer(null);
        Assertions.assertNull(solution.getAnswer(), "The expected answer is null");
        verify(mockedExercise, times(1)).getExam();
        verify(mockedExercise, times(1)).getSolutionTemplate();
        verifyNoMoreInteractions(mockedExercise);
    }

    /**
     * Tests that setting an empty {@link String} as an answer of an {@link ExerciseSolution} is allowed.
     */
    @Test
    void testEmptyAnswerIsSet() {
        final var solutionTemplate = Faker.instance().lorem().characters();
        when(mockedExercise.getExam()).thenReturn(mockedExam);
        when(mockedSubmission.getExam()).thenReturn(mockedExam);
        when(mockedExercise.getSolutionTemplate()).thenReturn(solutionTemplate);
        final var solution = createExerciseSolution();
        solution.setAnswer("");
        Assertions.assertTrue(solution.getAnswer().isEmpty(), "The expected answer is an empty String");
        verify(mockedExercise, times(1)).getExam();
        verify(mockedExercise, times(1)).getSolutionTemplate();
        verifyNoMoreInteractions(mockedExercise);
    }

    /**
     * Tests that setting a random {@link String} as an answer of an {@link ExerciseSolution} is allowed.
     */
    @Test
    void testRandomAnswerIsSet() {
        final var solutionTemplate = Faker.instance().lorem().characters();
        when(mockedExercise.getExam()).thenReturn(mockedExam);
        when(mockedSubmission.getExam()).thenReturn(mockedExam);
        when(mockedExercise.getSolutionTemplate()).thenReturn(solutionTemplate);
        final var solution = createExerciseSolution();
        final var answer = Faker.instance().lorem().characters();
        solution.setAnswer(answer);
        Assertions.assertEquals(answer, solution.getAnswer(), "The returned answer is not the expected");
        verify(mockedExercise, times(1)).getExam();
        verify(mockedExercise, times(1)).getSolutionTemplate();
        verifyNoMoreInteractions(mockedExercise);
    }

    /**
     * Tests that setting a random {@link String} as compiler flags of an {@link ExerciseSolution} is allowed.
     */
    @Test
    void testRandomCompilerFlagsAreSet() {
        final var solutionTemplate = Faker.instance().lorem().characters();
        when(mockedExercise.getExam()).thenReturn(mockedExam);
        when(mockedSubmission.getExam()).thenReturn(mockedExam);
        when(mockedExercise.getSolutionTemplate()).thenReturn(solutionTemplate);
        final var solution = createExerciseSolution();
        final var compilerFlags = Faker.instance().lorem().characters();
        solution.setCompilerFlags(compilerFlags);
        Assertions.assertEquals(
                compilerFlags,
                solution.getCompilerFlags(),
                "The returned compiler flags are not the expected"
        );
        verify(mockedExercise, times(1)).getExam();
        verify(mockedExercise, times(1)).getSolutionTemplate();
        verifyNoMoreInteractions(mockedExercise);
    }

    /**
     * Tests that setting a random {@link String} as main file name of an {@link ExerciseSolution} is allowed.
     */
    @Test
    void testRandomFileNameIsSet() {
        final var solutionTemplate = Faker.instance().lorem().characters();
        when(mockedExercise.getExam()).thenReturn(mockedExam);
        when(mockedSubmission.getExam()).thenReturn(mockedExam);
        when(mockedExercise.getSolutionTemplate()).thenReturn(solutionTemplate);
        final var solution = createExerciseSolution();
        final var mainFileName = Faker.instance().file().fileName();
        solution.setMainFileName(mainFileName);
        Assertions.assertEquals(
                mainFileName,
                solution.getMainFileName(),
                "The returned main file name is not the expected"
        );
        verify(mockedExercise, times(1)).getExam();
        verify(mockedExercise, times(1)).getSolutionTemplate();
        verifyNoMoreInteractions(mockedExercise);
    }

    /**
     * Tests that {@code null} can be used in all setters (as they are all optionals).
     */
    @Test
    void testNullForOptionals() {
        final var solutionTemplate = Faker.instance().lorem().characters();
        when(mockedExercise.getExam()).thenReturn(mockedExam);
        when(mockedSubmission.getExam()).thenReturn(mockedExam);
        when(mockedExercise.getSolutionTemplate()).thenReturn(solutionTemplate);
        final var solution = createExerciseSolution();
        solution.setAnswer(null);
        solution.setCompilerFlags(null);
        solution.setMainFileName(null);
        verify(mockedExercise, times(1)).getExam();
        verify(mockedExercise, times(1)).getSolutionTemplate();
        verifyNoMoreInteractions(mockedExercise);
    }


    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    // ================================
    // Creation
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExerciseSolution} with a null {@link Exercise}.
     */
    @Test
    void testNullSubmissionOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExerciseSolution(null, mockedExercise),
                "Creating an exercise solution with a null exercise is being allowed"
        );
        verifyZeroInteractions(mockedExercise);
        verifyZeroInteractions(mockedSubmission);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExerciseSolution} with a null {@link Exercise}.
     */
    @Test
    void testNullExerciseOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExerciseSolution(mockedSubmission, null),
                "Creating an exercise solution with a null exercise is being allowed"
        );
        verifyZeroInteractions(mockedExercise);
        verifyZeroInteractions(mockedSubmission);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExerciseSolution} with an {@link Exercise} and an {@link ExamSolutionSubmission}
     * both belonging to different {@link Exam}s.
     */
    @Test
    void testDifferentExams() {
        final var exam1 = mock(Exam.class);
        final var exam2 = mock(Exam.class);
        when(mockedExercise.getExam()).thenReturn(exam1);
        when(mockedSubmission.getExam()).thenReturn(exam2);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExerciseSolution(mockedSubmission, mockedExercise),
                "Creating an exercise solution with an exercise and a submission with different exams" +
                        " is being allowed"
        );
        verify(mockedExercise, only()).getExam();
        verify(mockedSubmission, only()).getExam();
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Creation of objects
    // ========================================

    /**
     * Creates a valid {@link ExerciseSolution}.
     *
     * @return An {@link ExerciseSolution}.
     */
    private ExerciseSolution createExerciseSolution() {
        return new ExerciseSolution(mockedSubmission, mockedExercise);
    }
}
