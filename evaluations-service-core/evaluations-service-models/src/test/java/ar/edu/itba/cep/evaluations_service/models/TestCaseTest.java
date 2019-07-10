package ar.edu.itba.cep.evaluations_service.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Test class for {@link TestCase}s
 */
@ExtendWith(MockitoExtension.class)
class TestCaseTest {

    /**
     * Indicates how many elements must have the created {@link String} {@link List}s used for testing
     * (i.e to be used as inputs and expected outputs).
     */
    private static final int STRING_LISTS_SIZE = 10;


    /**
     * A mocked {@link Exercise} that will own the created {@link TestCase}s to be tested.
     */
    private final Exercise mockedExercise;

    /**
     * Constructor.
     *
     * @param mockedExercise A mocked {@link Exercise} that will own the created {@link TestCase}s to be tested.
     */
    TestCaseTest(@Mock final Exercise mockedExercise) {
        this.mockedExercise = mockedExercise;
    }


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests that creating an {@link Exercise} with valid values can be performed without any exception being thrown.
     */
    @Test
    void testAcceptableArguments() {
        Assertions.assertAll("Test cases with acceptable arguments are not being created",
                () -> Assertions.assertDoesNotThrow(
                        this::createTestCaseWithTimeout,
                        "Cannot create with positive timeout"
                ),
                () -> Assertions.assertDoesNotThrow(
                        this::createTestCaseWithoutTimeout,
                        "Cannot create without timeout (null timeout)"
                )
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that updating an {@link Exercise} with valid values works as expected.
     */
    @Test
    void testValidArgumentsUpdate() {
        Assertions.assertAll("Updating with acceptable arguments is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createAnyTimeoutTestCase().update(
                                validVisibility(),
                                validTimeout(),
                                validList(),
                                validList()
                        ),
                        "It throws an exception"
                ),
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    final var visibility = validVisibility();
                    final var timeout = validTimeout();
                    final var inputs = validList();
                    final var expectedOutputs = validList();
                    testCase.update(visibility, timeout, inputs, expectedOutputs);
                    Assertions.assertAll("Is not being set (does not change the Exercise value)",
                            () -> Assertions.assertEquals(
                                    visibility,
                                    testCase.getVisibility(),
                                    "Visibility mismatch"
                            ),
                            () -> Assertions.assertEquals(
                                    timeout,
                                    testCase.getTimeout(),
                                    "Timeout mismatch"
                            ),
                            () -> Assertions.assertEquals(
                                    inputs,
                                    testCase.getInputs(),
                                    "Inputs mismatch"
                            ),
                            () -> Assertions.assertEquals(
                                    expectedOutputs,
                                    testCase.getExpectedOutputs(),
                                    "Expected outputs mismatch"
                            )
                    );
                }
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }


    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    // ================================
    // Creation
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with a null {@link TestCase.Visibility}.
     */
    @Test
    void testNullVisibilityOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(null, validTimeout(), validList(), validList(), mockedExercise),
                "Creating a test case with a null visibility is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with a non positive timeout.
     */
    @Test
    void testNonPositiveTimeoutOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(validVisibility(), nonPositiveTimeout(), validList(), validList(), mockedExercise),
                "Creating a test case with a null visibility is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with a null inputs {@link List}.
     */
    @Test
    void testNullInputsListOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(validVisibility(), validTimeout(), null, validList(), mockedExercise),
                "Creating a test case with a null inputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with an empty inputs {@link List}.
     */
    @Test
    void testEmptyInputsListOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(validVisibility(), validTimeout(), Collections.emptyList(), validList(), mockedExercise),
                "Creating a test case with an empty inputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with an inputs {@link List} containing a null element.
     */
    @Test
    void testInputsListWithNullElementOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(validVisibility(), validTimeout(), listWithNulls(), validList(), mockedExercise),
                "Creating a test case with an inputs list with null elements is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with a null expected outputs {@link List}.
     */
    @Test
    void testNullExpectedOutputsListOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(validVisibility(), validTimeout(), validList(), null, mockedExercise),
                "Creating a test case with a null expected outputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with an empty expected outputs {@link List}.
     */
    @Test
    void testEmptyExpectedOutputsListOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(validVisibility(), validTimeout(), validList(), Collections.emptyList(), mockedExercise),
                "Creating a test case with an empty expected outputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with an expected outputs {@link List} containing a null element.
     */
    @Test
    void testExpectedOutputsListWithNullElementOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(validVisibility(), validTimeout(), validList(), listWithNulls(), mockedExercise),
                "Creating a test case with an expected outputs list with null elements is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with a null {@link Exercise}.
     */
    @Test
    void testNullExerciseOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(validVisibility(), validTimeout(), validList(), validList(), null),
                "Creating a test case with a null exercise is being allowed"
        );
    }


    // ================================
    // Updates
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating a {@link TestCase} with a null {@link TestCase.Visibility}.
     */
    @Test
    void testNullVisibilityOnUpdate() {
        final var testCase = createAnyTimeoutTestCase();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testCase.update(null, validTimeout(), validList(), validList()),
                "Updating a test case with a null visibility is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating a {@link TestCase} with a non positive timeout.
     */
    @Test
    void testNonPositiveTimeoutOnUpdate() {
        final var testCase = createAnyTimeoutTestCase();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testCase.update(validVisibility(), nonPositiveTimeout(), validList(), validList()),
                "Updating a test case with a null visibility is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating a {@link TestCase} with a null inputs {@link List}.
     */
    @Test
    void testNullInputsListOnUpdate() {
        final var testCase = createAnyTimeoutTestCase();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testCase.update(validVisibility(), validTimeout(), null, validList()),
                "Updating a test case with a null inputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating a {@link TestCase} with an empty inputs {@link List}.
     */
    @Test
    void testEmptyInputsListOnUpdate() {
        final var testCase = createAnyTimeoutTestCase();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testCase.update(validVisibility(), validTimeout(), Collections.emptyList(), validList()),
                "Updating a test case with an empty inputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating a {@link TestCase} with an inputs {@link List} containing a null element.
     */
    @Test
    void testInputsListWithNullElementOnUpdate() {
        final var testCase = createAnyTimeoutTestCase();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testCase.update(validVisibility(), validTimeout(), listWithNulls(), validList()),
                "Updating a test case with an inputs list with null elements is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating a {@link TestCase} with a null inputs {@link List}.
     */
    @Test
    void testNullExpectedOutputsListOnUpdate() {
        final var testCase = createAnyTimeoutTestCase();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testCase.update(validVisibility(), validTimeout(), validList(), null),
                "Updating a test case with a null expected outputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating a {@link TestCase} with an empty expected outputs {@link List}.
     */
    @Test
    void testEmptyExpectedOutputsListOnUpdate() {
        final var testCase = createAnyTimeoutTestCase();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testCase.update(validVisibility(), validTimeout(), validList(), Collections.emptyList()),
                "Updating a test case with an empty expected outputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when updating a {@link TestCase} with an expected outputs {@link List} containing a null element.
     */
    @Test
    void testExpectedOutputsListWithNullElementOnUpdate() {
        final var testCase = createAnyTimeoutTestCase();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testCase.update(validVisibility(), validTimeout(), validList(), listWithNulls()),
                "Updating a test case with an expected outputs list with null elements is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Creation of objects
    // ========================================

    /**
     * Creates a valid {@link TestCase} with a timeout set.
     *
     * @return A {@link TestCase}, with the timeout set.
     */
    private TestCase createTestCaseWithTimeout() {
        return new TestCase(
                validVisibility(),
                positiveTimeout(),
                validList(),
                validList(),
                mockedExercise
        );
    }

    /**
     * Creates a valid {@link TestCase} without a timeout set.
     *
     * @return A {@link TestCase}, without the timeout set.
     */
    private TestCase createTestCaseWithoutTimeout() {
        return new TestCase(
                validVisibility(),
                null,
                validList(),
                validList(),
                mockedExercise
        );
    }

    /**
     * Createa a valid {@link TestCase} with or without a timeout set.
     *
     * @return A {@link TestCase}.
     */
    private TestCase createAnyTimeoutTestCase() {
        return new TestCase(
                validVisibility(),
                validTimeout(),
                validList(),
                validList(),
                mockedExercise
        );
    }


    // ========================================
    // Valid values
    // ========================================

    /**
     * @return A random {@link TestCase.Visibility}.
     */
    private static TestCase.Visibility validVisibility() {
        final var visibilities = TestCase.Visibility.values();
        final var randomIndex = (int) Faker.instance().number().numberBetween(0L, visibilities.length);
        return visibilities[randomIndex];
    }

    /**
     * @return A random valid timeout.
     */
    private static Long validTimeout() {
        return Faker.instance().bool().bool() ? positiveTimeout() : null;
    }

    /**
     * @return A random positive long that represents a timeout.
     */
    private static long positiveTimeout() {
        return Faker.instance().number().numberBetween(1, Long.MAX_VALUE);
    }

    /**
     * Creates a valid {@link List} of {@link String} to be used as inputs or expected outputs.
     *
     * @return A valid {@link List}.
     */
    private static List<String> validList() {
        return Faker.instance()
                .lorem()
                .words(STRING_LISTS_SIZE);
    }


    // ========================================
    // Invalid values
    // ========================================

    /**
     * @return A random non positive long that represents a timeout.
     */
    private static long nonPositiveTimeout() {
        return Faker.instance().number().numberBetween(Long.MIN_VALUE, 1);
    }

    /**
     * Creates a {@link List} containing a null value.
     *
     * @return A {@link List} with a null value.
     */
    private static List<String> listWithNulls() {
        final var result = new LinkedList<>(Faker.instance().lorem().words(STRING_LISTS_SIZE - 1));
        result.add(null);
        Collections.shuffle(result); // Perform shuffling to be sure that check is performed in all the list
        return result;
    }
}
