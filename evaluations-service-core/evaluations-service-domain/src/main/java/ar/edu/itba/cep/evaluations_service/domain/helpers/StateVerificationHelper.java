package ar.edu.itba.cep.evaluations_service.domain.helpers;

import ar.edu.itba.cep.evaluations_service.models.ExamSolutionSubmission;
import com.bellotapps.webapps_commons.errors.IllegalEntityStateError;
import com.bellotapps.webapps_commons.exceptions.IllegalEntityStateException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Created by Juan Marcos Bellini on 2019-09-19.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class StateVerificationHelper {


    /**
     * Checks that the given {@link ExamSolutionSubmission} is already submitted.
     *
     * @param submission The {@link ExamSolutionSubmission} to be checked.
     * @throws IllegalEntityStateException If the {@link ExamSolutionSubmission} is not submitted.
     */
    public static void checkSubmitted(final ExamSolutionSubmission submission) throws IllegalEntityStateException {
        // Then check the submission's state
        if (submission.getState() != ExamSolutionSubmission.State.SUBMITTED) {
            throw new IllegalEntityStateException(SOLUTIONS_NOT_SUBMITTED);
        }
    }


    /**
     * An {@link IllegalStateException} that indicates that an {@link ExamSolutionSubmission} is not submitted yet.
     */
    private final static IllegalEntityStateError SOLUTIONS_NOT_SUBMITTED =
            new IllegalEntityStateError("Solutions not submitted yet", "state");
}
