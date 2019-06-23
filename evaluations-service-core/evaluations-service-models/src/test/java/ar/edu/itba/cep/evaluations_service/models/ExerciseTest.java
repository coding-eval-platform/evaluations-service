package ar.edu.itba.cep.evaluations_service.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * Test class for {@link Exercise}s
 */
@ExtendWith(MockitoExtension.class)
class ExerciseTest {

    /**
     * A mocked {@link Exam} that will own the created {@link Exercise}s to be tested.
     */
    private final Exam mockedExam;

    /**
     * Constructor.
     *
     * @param mockedExam A mocked {@link Exam} that will own the created {@link Exercise}s to be tested.
     */
    ExerciseTest(@Mock final Exam mockedExam) {
        this.mockedExam = mockedExam;
    }


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests that creating an {@link Exercise} with valid values can be performed without any exception being thrown.
     */
    @Test
    void testAcceptableArguments() {
        Assertions.assertDoesNotThrow(this::createExercise,
                "An exercise is not being created with acceptable arguments.");
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that updating an {@link Exercise} with valid values works as expected.
     */
    @Test
    void testValidArgumentsUpdate() {
        Assertions.assertAll("Updating with acceptable arguments is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExercise().update(validQuestion(), validLanguage(), validSolutionTemplate()),
                        "It throws an exception"
                ),
                () -> {
                    final var exercise = createExercise();
                    final var question = validQuestion();
                    final var language = validLanguage();
                    final var solutionTemplate = validSolutionTemplate();
                    exercise.update(question, language, solutionTemplate);
                    Assertions.assertAll("Is not being set (does not change the Exercise value)",
                            () -> Assertions.assertEquals(
                                    question,
                                    exercise.getQuestion(),
                                    "Question mismatch"
                            ),
                            () -> Assertions.assertEquals(
                                    language,
                                    exercise.getLanguage(),
                                    "Language mismatch"
                            ),
                            () -> Assertions.assertEquals(
                                    solutionTemplate,
                                    exercise.getSolutionTemplate(),
                                    "Solution template mismatch"
                            )
                    );
                }
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that setting a valid question to an {@link Exercise} works as expected.
     */
    @Test
    void testSetValidQuestion() {
        Assertions.assertAll("Setting a valid question is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExercise().setQuestion(validQuestion()),
                        "It throws an exception"
                ),
                () -> {
                    final var exercise = createExercise();
                    final var question = validQuestion();
                    exercise.setQuestion(question);
                    Assertions.assertEquals(
                            question,
                            exercise.getQuestion(),
                            "Is not being set (does not change the Exercise value)"
                    );
                }
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that setting a valid {@link Language} to an {@link Exercise} works as expected.
     */
    @Test
    void testSetValidLanguage() {
        Assertions.assertAll("Setting a valid language is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExercise().setLanguage(validLanguage()),
                        "It throws an exception"
                ),
                () -> {
                    final var exercise = createExercise();
                    final var language = validLanguage();
                    exercise.setLanguage(language);
                    Assertions.assertEquals(
                            language,
                            exercise.getLanguage(),
                            "Is not being set (does not change the Exercise value)"
                    );
                }
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that setting a valid solution template to an {@link Exercise} works as expected.
     */
    @Test
    void testSetValidSolutionTemplate() {
        Assertions.assertAll("Setting a valid solution template is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createExercise().setSolutionTemplate(validSolutionTemplate()),
                        "It throws an exception"
                ),
                () -> {
                    final var exercise = createExercise();
                    final var solutionTemplate = validSolutionTemplate();
                    exercise.setSolutionTemplate(solutionTemplate);
                    Assertions.assertEquals(
                            solutionTemplate,
                            exercise.getSolutionTemplate(),
                            "Is not being set (does not change the Exercise value)"
                    );
                }
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    // ================================
    // Creation
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exercise} with a null question.
     */
    @Test
    void testNullQuestionOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exercise(null, validLanguage(), validSolutionTemplate(), mockedExam),
                "Creating an exercise with a null question is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exercise} with a too short question.
     */
    @Test
    void testShortQuestionOnCreation() {
        shortQuestion().ifPresent(
                shortQuestion -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new Exercise(shortQuestion, validLanguage(), validSolutionTemplate(), mockedExam),
                        "Creating an exercise with a too short question is being allowed."
                )
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exercise} with a null {@link Language}.
     */
    @Test
    void testNullLanguageOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exercise(validQuestion(), null, validSolutionTemplate(), mockedExam),
                "Creating an exercise with a null language is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    // No test for solution template as it can be null or any string

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating an {@link Exercise} with a null {@link Exam}.
     */
    @Test
    void testNullExamOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Exercise(validQuestion(), validLanguage(), validSolutionTemplate(), null),
                "Creating an exercise with a null exam is being allowed."
        );
    }


    // ================================
    // Update
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exercise} with a null question.
     */
    @Test
    void testNullQuestionOnUpdate() {
        final var exercise = createExercise();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exercise.update(null, validLanguage(), validSolutionTemplate()),
                "Updating an exercise with a null question is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exercise} with a too short question.
     */
    @Test
    void testShortQuestionOnUpdate() {
        final var exercise = createExercise();
        shortQuestion().ifPresent(
                shortQuestion -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> exercise.update(shortQuestion, validLanguage(), validSolutionTemplate()),
                        "Updating an exercise with a too short question is being allowed."
                )
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating an {@link Exercise} with a null {@link Language}.
     */
    @Test
    void testNullLanguageOnUpdate() {
        final var exercise = createExercise();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exercise.update(validQuestion(), null, validSolutionTemplate()),
                "Updating an exercise with a null language is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    // No test for solution template as it can be null or any string


    // ================================
    // Setters
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a null question to an {@link Exercise}.
     */
    @Test
    void testSetNullQuestion() {
        final var exercise = createExercise();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exercise.setQuestion(null),
                "Setting a null question is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a too short question to an {@link Exercise}.
     */
    @Test
    void testSetShortQuestion() {
        final var exercise = createExercise();
        shortQuestion().ifPresent(
                shortQuestion -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> exercise.setQuestion(shortQuestion),
                        "Setting a short question is being allowed."
                )
        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a null {@link Language} to an {@link Exercise}.
     */
    @Test
    void testSetNullLanguage() {
        final var exercise = createExercise();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> exercise.setLanguage(null),
                "Setting a null language is being allowed."

        );
        Mockito.verifyZeroInteractions(mockedExam);
    }

    // No test for solution template as it can be null or any string


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
    private Exercise createExercise() {
        return new Exercise(
                validQuestion(),
                validLanguage(),
                validSolutionTemplate(),
                mockedExam
        );
    }


    // ========================================
    // Valid values
    // ========================================

    /**
     * @return A random question whose length is between the valid limits.
     */
    private static String validQuestion() {
        // There is no such max value for questions, but it is needed for creating a value with Faker
        final var maxLength = Short.MAX_VALUE;
        return Faker.instance()
                .lorem()
                .characters(ValidationConstants.QUESTION_MIN_LENGTH, maxLength);
    }

    /**
     * @return A random valid {@link Language}.
     */
    private static Language validLanguage() {
        final var languages = Language.values();
        final var randomIndex = (int) Faker.instance().number().numberBetween(0L, languages.length);
        return languages[randomIndex];
    }

    /**
     * @return A random valid solution template.
     */
    private static String validSolutionTemplate() {
        // Can be null or any string
        final List<String> possibleSolutionTemplates = new LinkedList<>();
        possibleSolutionTemplates.add(null);
        possibleSolutionTemplates.add(
                Faker.instance()
                        .lorem()
                        .characters(ValidationConstants.ANSWER_MIN_LENGTH, Short.MAX_VALUE)
        );

        final var index = (int) Faker.instance().number().numberBetween(0L, possibleSolutionTemplates.size());
        return possibleSolutionTemplates.get(index);
    }


    // ========================================
    // Invalid values
    // ========================================

    /**
     * @return An {@link Optional} containing a question whose length is below the valid limit
     * if there is such limit (i.e th min length is positive). Otherwise, an empty {@link Optional} is returned.
     */
    private static Optional<String> shortQuestion() {
        if (ValidationConstants.QUESTION_MIN_LENGTH > 0) {
            final var question = Faker.instance()
                    .lorem()
                    .fixedString(ValidationConstants.QUESTION_MIN_LENGTH - 1);
            return Optional.of(question);
        }
        return Optional.empty();
    }
}
