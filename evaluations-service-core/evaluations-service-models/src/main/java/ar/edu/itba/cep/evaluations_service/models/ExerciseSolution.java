package ar.edu.itba.cep.evaluations_service.models;

/**
 * Represents a solution of an exercise.
 */
public class ExerciseSolution {

    /**
     * The exercise solution's id.
     */
    private final long id;

    /**
     * The {@link Exercise} to which it belongs to.
     */
    private final Exercise belongsTo;

    /**
     * The answer to the question of the {@link Exercise} (i.e the code written by the student).
     */
    private final String answer;

    /**
     * Constructor.
     *
     * @param belongsTo The {@link Exercise} to which it belongs to.
     * @param answer    The answer to the question of the {@link Exercise} (i.e the code written by the student).
     */
    public ExerciseSolution(final Exercise belongsTo, final String answer) {
        this.id = 0;
        this.belongsTo = belongsTo;
        this.answer = answer;
    }

    /**
     * @return The exercise solution's id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The {@link Exercise} to which it belongs to.
     */
    public Exercise getBelongsTo() {
        return belongsTo;
    }

    /**
     * @return The answer to the question of the {@link Exercise} (i.e the code written by the student).
     */
    public String getAnswer() {
        return answer;
    }
}
