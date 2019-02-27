package ar.edu.itba.cep.evaluations_service.models;

/**
 * Represents an exercise.
 */
public class Exercise {

    /**
     * The exercise's id.
     */
    private final long id;

    /**
     * The question being asked.
     */
    private final String question;

    /**
     * The {@link Exam} to which this exercise belongs to.
     */
    private final Exam belongsTo;


    /**
     * Constructor.
     *
     * @param question  The question being asked.
     * @param belongsTo The {@link Exam} to which this exercise belongs to.
     */
    public Exercise(final String question, final Exam belongsTo) {
        this.belongsTo = belongsTo;
        this.id = 0;
        this.question = question;
    }


    /**
     * @return The exercise's id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The question being asked.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @return The {@link Exam} to which this exercise belongs to.
     */
    public Exam getBelongsTo() {
        return belongsTo;
    }
}
