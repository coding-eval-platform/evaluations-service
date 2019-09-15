package ar.edu.itba.cep.evaluations_service.domain.managers;

import ar.edu.itba.cep.evaluations_service.models.ExerciseSolution;
import ar.edu.itba.cep.evaluations_service.models.TestCase;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionRepository;
import ar.edu.itba.cep.evaluations_service.repositories.ExerciseSolutionResultRepository;
import ar.edu.itba.cep.evaluations_service.repositories.TestCaseRepository;
import org.springframework.context.ApplicationEventPublisher;

/**
 * A base Test class for {@link ResultsManager}.
 */
abstract class AbstractResultsManagerTest {

    // ================================================================================================================
    // Mocks
    // ================================================================================================================

    /**
     * A {@link TestCaseRepository} that is injected to the {@link ResultsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final TestCaseRepository testCaseRepository;
    /**
     * An {@link ExerciseSolutionRepository} that is injected to the {@link ResultsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseSolutionRepository exerciseSolutionRepository;
    /**
     * An {@link ExerciseSolutionResultRepository} that is injected to the {@link ResultsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ExerciseSolutionResultRepository exerciseSolutionResultRepository;

    /**
     * An {@link ApplicationEventPublisher} that is injected to the {@link ResultsManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    /* package */ final ApplicationEventPublisher publisher;


    // ================================================================================================================
    // Results Manager
    // ================================================================================================================

    /**
     * The {@link ResultsManager} being tested.
     */
    /* package */ final ResultsManager resultsManager;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param testCaseRepository               A {@link TestCaseRepository}
     *                                         that is injected to the {@link ResultsManager}.
     * @param exerciseSolutionRepository       An {@link ExerciseSolutionRepository}
     *                                         that is injected to the {@link ResultsManager}.
     * @param exerciseSolutionResultRepository An {@link ExerciseSolutionResultRepository}
     *                                         that is injected to the {@link ResultsManager}.
     * @param publisher                        An {@link ApplicationEventPublisher}
     *                                         that is injected to the {@link ResultsManager}.
     */
    AbstractResultsManagerTest(
            final TestCaseRepository testCaseRepository,
            final ExerciseSolutionRepository exerciseSolutionRepository,
            final ExerciseSolutionResultRepository exerciseSolutionResultRepository,
            final ApplicationEventPublisher publisher) {
        this.testCaseRepository = testCaseRepository;
        this.exerciseSolutionRepository = exerciseSolutionRepository;
        this.exerciseSolutionResultRepository = exerciseSolutionResultRepository;
        this.publisher = publisher;
        this.resultsManager = new ResultsManager(
                exerciseSolutionRepository,
                testCaseRepository,
                exerciseSolutionResultRepository,
                publisher
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * A functional interface to pass actions over a {@link ResultsManager}
     * that takes an {@link ExerciseSolution} and a {@link TestCase} id.
     */
    @FunctionalInterface
            /* package */ interface ResultsManagerSolutionTestCaseAction {

        /**
         * Performs an action over the given {@code manager} with the given {@code solutionId} and {@code testCaseId}.
         *
         * @param manager    The {@link ResultsManager} over which the method will operate.
         * @param solutionId An {@link ExerciseSolution} id
         *                   to be passed to the action being performed over the {@code manager}.
         * @param testCaseId A {@link TestCase} id
         *                   to be passed to the action being performed over the {@code manager}.
         */
        void accept(final ResultsManager manager, final long solutionId, final long testCaseId);
    }
}
