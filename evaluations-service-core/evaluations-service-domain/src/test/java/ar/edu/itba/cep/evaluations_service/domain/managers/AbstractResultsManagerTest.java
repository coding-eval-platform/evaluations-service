package ar.edu.itba.cep.evaluations_service.domain.managers;

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
}
