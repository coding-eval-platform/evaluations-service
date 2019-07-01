package ar.edu.itba.cep.evaluations_service.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;


/**
 * Represents an exercise.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(doNotUseGetters = true, callSuper = true)
public class Exercise {

    /**
     * The exercise's id.
     */
    private final long id;

    /**
     * The question being asked.
     */
    private String question;

    /**
     * The {@link Language} in which the answer must be written.
     */
    private Language language;

    /**
     * The solution template.
     */
    private String solutionTemplate;

    /**
     * The {@link Exam} to which this exercise belongs to.
     */
    private final Exam exam;


    /**
     * Default constructor.
     */
    /* package */ Exercise() {
        // Initialize final fields with default values.
        this.id = 0;
        this.exam = null;
    }

    /**
     * Constructor.
     *
     * @param question         The question being asked.
     * @param language         The {@link Language} in which the answer must be written.
     * @param solutionTemplate The solution template.
     * @param exam             The {@link Exam} to which this exercise belongs to.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public Exercise(final String question, final Language language, final String solutionTemplate, final Exam exam)
            throws IllegalArgumentException {
        assertQuestion(question);
        assertLanguage(language);
        assertSolutionTemplate(solutionTemplate);
        assertExam(exam);
        this.id = 0;
        this.question = question;
        this.language = language;
        this.solutionTemplate = solutionTemplate;
        this.exam = exam;
    }


    /**
     * Changes the question for this exercise.
     *
     * @param question The new question for the exercise.
     * @throws IllegalArgumentException If the given {@code question} is not valid.
     */
    public void setQuestion(final String question) throws IllegalArgumentException {
        assertQuestion(question);
        this.question = question;
    }

    /**
     * Changes the {@link Language} for this exercise.
     *
     * @param language The new {@link Language} for the exercise.
     * @throws IllegalArgumentException If the given {@code language} is not valid.
     */
    public void setLanguage(final Language language) throws IllegalArgumentException {
        assertLanguage(language);
        this.language = language;
    }

    /**
     * Changes the solution template for this exercise.
     *
     * @param solutionTemplate The new solution template for the exercise.
     * @throws IllegalArgumentException If the given {@code solutionTemplate} is not valid.
     */
    public void setSolutionTemplate(final String solutionTemplate) throws IllegalArgumentException {
        assertSolutionTemplate(solutionTemplate);
        this.solutionTemplate = solutionTemplate;
    }

    /**
     * Updates all fields of this exercise.
     *
     * @param question         The new question for the exercise.
     * @param language         The new language for the exercise.
     * @param solutionTemplate The new solution template for the exercise.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public void update(final String question, final Language language, final String solutionTemplate)
            throws IllegalArgumentException {
        assertQuestion(question);
        assertLanguage(language);
        assertSolutionTemplate(solutionTemplate);
        this.question = question;
        this.language = language;
        this.solutionTemplate = solutionTemplate;
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code question} is valid.
     *
     * @param question The question to be checked.
     * @throws IllegalArgumentException If the question is not valid.
     */
    private static void assertQuestion(final String question) throws IllegalArgumentException {
        Assert.notNull(question, "The question is missing");
        Assert.isTrue(question.length() >= ValidationConstants.QUESTION_MIN_LENGTH,
                "The question is too short");
    }

    /**
     * Asserts that the given {@code language} is valid.
     *
     * @param language The {@link Language} to be checked.
     * @throws IllegalArgumentException If the {@code language} is not valid.
     */
    private static void assertLanguage(final Language language) throws IllegalArgumentException {
        Assert.notNull(language, "The language must not be null");
    }

    /**
     * Asserts that the given {@code solutionTemplate} is valid.
     *
     * @param solutionTemplate The solution template to be checked.
     * @throws IllegalArgumentException If the solution template is not valid.
     */
    private static void assertSolutionTemplate(final String solutionTemplate) throws IllegalArgumentException {
        // There is not assertion fot the solution template. Can be null and have any length.
    }

    /**
     * Asserts that the given {@code exam} is valid.
     *
     * @param exam The {@link Exam} to be checked.
     * @throws IllegalArgumentException If the exam is not valid.
     */
    private static void assertExam(final Exam exam) throws IllegalArgumentException {
        Assert.notNull(exam, "The exam is missing");
    }
}
