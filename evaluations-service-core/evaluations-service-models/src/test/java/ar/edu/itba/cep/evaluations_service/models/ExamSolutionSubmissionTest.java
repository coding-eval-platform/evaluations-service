package ar.edu.itba.cep.evaluations_service.models;

import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;

import static ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission.State.SUBMITTED;
import static ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission.State.UNPLACED;

/**
 * Test class for {@link ExamSolutionSubmission}s.
 */
@ExtendWith(MockitoExtension.class)
class ExamSolutionSubmissionTest {

    /**
     * A mocked {@link Exam} that will own the created {@link ExamSolutionSubmission}s to be tested.
     */
    private final Exam mockedExam;

    ExamSolutionSubmissionTest(@Mock(name = "exam") final Exam mockedExam) {
        this.mockedExam = mockedExam;
    }

    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests that creating an {@link ExamSolutionSubmission}
     * with valid values can be performed without any exception being thrown.
     */
    @Test
    void testAcceptableArguments() {
        Assertions.assertDoesNotThrow(this::createSubmission,
                "A submission is not being created with acceptable arguments.");
        Mockito.verifyZeroInteractions(mockedExam);
    }

    // ================================================================================================================
    // Behaviour testing
    // ================================================================================================================

    /**
     * Tests that an {@link ExamSolutionSubmission} is created with the {@link ExamSolutionSubmission.State#UNPLACED}
     * state.
     */
    @Test
    void testSubmissionStateIsUnplacedWhenCreated() {
        final var submission = createSubmission();
        Assertions.assertEquals(
                UNPLACED,
                submission.getState(),
                "The state of the submission must be " + UNPLACED + " when created");
    }

    /**
     * Tests that an {@link ExamSolutionSubmission}'s state is {@link ExamSolutionSubmission.State#SUBMITTED}
     * after submitting it.
     */
    @Test
    void testSubmissionStateIsChangedAfterSubmittingIt() {
        final var submission = createSubmission();
        submission.submit();
        Assertions.assertEquals(
                SUBMITTED,
                submission.getState(),
                "The state of the submission must be " + SUBMITTED + " after submitting it");
    }

    /**
     * Tests that the submit action cannot be executed more than once.
     */
    @Test
    void testSubmissionCannotBeDoneTwice() {
        final var submission = createSubmission();
        submission.submit();
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                submission::submit,
                "Submitting a submission is being allowed to be done more than once"
        );
    }

    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    // ================================
    // Creation
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExamSolutionSubmission} with a null exam.
     */
    @Test
    void testNullExamOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExamSolutionSubmission(null, validOwner()),
                "Creating a submission with a null exam is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExamSolutionSubmission} with a null submitter.
     */
    @Test
    void testNullSubmitterOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExamSolutionSubmission(mockedExam, null),
                "Creating a submission with a null submitter is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExamSolutionSubmission} with an empty {@link String} submitter.
     */
    @Test
    void testEmptyStringSubmitterOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExamSolutionSubmission(mockedExam, ""),
                "Creating a submission with an empty string submitter is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link ExamSolutionSubmission} with a blank {@link String} submitter.
     */
    @Test
    void testBlankStringSubmitterOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ExamSolutionSubmission(mockedExam, " \t\n"),
                "Creating a submission with a blank string submitter is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Creation of objects
    // ========================================

    /**
     * Creates a valid {@link Exercise}.
     *
     * @return An {@link Exercise}.
     */
    private ExamSolutionSubmission createSubmission() {
        return new ExamSolutionSubmission(
                mockedExam,
                validOwner()
        );
    }


    // ========================================
    // Valid values
    // ========================================

    /**
     * @return A random username.
     */
    private static String validOwner() {
        return Faker.instance().name().username();
    }


    // ========================================
    // Invalid values
    // ========================================

    /**
     * @return An invalid owner.
     */
    private static String invalidOwner() {
        final List<String> invalidValues = new LinkedList<>();
        invalidValues.add(null);
        invalidValues.add("");
        invalidValues.add(" \t\n");
        final var index = (int) Faker.instance().number().numberBetween(0L, invalidValues.size());
        return invalidValues.get(index);
    }
}
