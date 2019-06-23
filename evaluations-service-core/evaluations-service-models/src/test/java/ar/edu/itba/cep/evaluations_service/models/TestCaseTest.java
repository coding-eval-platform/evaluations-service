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

    /**
     * Tests that setting a valid {@link TestCase.Visibility} to a {@link TestCase} works as expected.
     */
    @Test
    void testSetValidVisibility() {
        Assertions.assertAll("Setting a valid visibility is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createAnyTimeoutTestCase().setVisibility(TestCase.Visibility.PRIVATE),
                        "It throws an exception when it is private"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> createAnyTimeoutTestCase().setVisibility(TestCase.Visibility.PUBLIC),
                        "It throws an exception when it is public"
                ),
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    testCase.setVisibility(TestCase.Visibility.PRIVATE);
                    Assertions.assertSame(
                            TestCase.Visibility.PRIVATE,
                            testCase.getVisibility(),
                            "Is not being set to private (does not change the TestCase value)"
                    );
                },
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    testCase.setVisibility(TestCase.Visibility.PUBLIC);
                    Assertions.assertSame(
                            TestCase.Visibility.PUBLIC,
                            testCase.getVisibility(),
                            "Is not being set to public (does not change the TestCase value)"
                    );
                }
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that setting a valid timeout to a {@link TestCase} works as expected.
     */
    @Test
    void testSetValidTimeout() {
        Assertions.assertAll("Setting a valid timeout is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createAnyTimeoutTestCase().setTimeout(positiveTimeout()),
                        "It throws an exception when setting a positive value"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> createAnyTimeoutTestCase().setTimeout(null),
                        "It throws an exception when setting a null value"
                ),
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    final var timeout = positiveTimeout();
                    testCase.setTimeout(timeout);
                    Assertions.assertEquals(
                            Long.valueOf(timeout),
                            testCase.getTimeout(),
                            "Is not being set (does not change the TestCase value)"
                    );
                },
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    testCase.setTimeout(null);
                    Assertions.assertNull(
                            testCase.getTimeout(),
                            "Is not being set (does not change the TestCase value)");
                }
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that setting a valid input {@link List} to a {@link TestCase} works as expected.
     */
    @Test
    void testSetValidInputsList() {
        Assertions.assertAll("Setting a valid inputs list is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createAnyTimeoutTestCase().setInputs(validList()),
                        "It throws an exception"
                ),
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    final var inputsList = validList();
                    testCase.setInputs(inputsList);
                    Assertions.assertEquals(
                            inputsList,
                            testCase.getInputs(),
                            "Is not being set (does not change the TestCase value)"
                    );
                }
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that setting a valid expected output {@link List} to a {@link TestCase} works as expected.
     */
    @Test
    void testSetValidExpectedOutputsList() {
        Assertions.assertAll("Setting a valid expected outputs list is not being allowed",
                () -> Assertions.assertDoesNotThrow(
                        () -> createAnyTimeoutTestCase().setExpectedOutputs(validList()),
                        "It throws an exception"
                ),
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    final var outputsList = validList();
                    testCase.setExpectedOutputs(outputsList);
                    Assertions.assertEquals(
                            outputsList,
                            testCase.getExpectedOutputs(),
                            "Is not being set (does not change the TestCase value)"
                    );
                }
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that clearing the inputs list works as expected
     */
    @Test
    void testClearInputs() {
        Assertions.assertAll("Removing inputs is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createAnyTimeoutTestCase().removeAllInputs(),
                        "Cannot remove inputs to a new created test case. This should not fail"
                ),
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    testCase.setInputs(validList());
                    testCase.removeAllInputs();
                    Assertions.assertTrue(
                            testCase.getInputs().isEmpty(),
                            "Test cases are not returning an empty inputs list after removing them"
                    );
                },
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    testCase.setInputs(validList());
                    testCase.removeAllInputs();
                    Assertions.assertDoesNotThrow(
                            testCase::removeAllInputs,
                            "Cannot remove the inputs list more than one time. This should not fail"
                    );
                }
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that clearing the expected outputs list works as expected
     */
    @Test
    void testClearExpectedOutputs() {
        Assertions.assertAll("Removing expected outputs is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createAnyTimeoutTestCase().removeAllExpectedOutputs(),
                        "Cannot remove expected outputs to a new created test case. This should not fail"
                ),
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    testCase.setExpectedOutputs(validList());
                    testCase.removeAllExpectedOutputs();
                    Assertions.assertTrue(
                            testCase.getExpectedOutputs().isEmpty(),
                            "Test cases are not returning an empty expected outputs list after removing them"
                    );
                },
                () -> {
                    final var testCase = createAnyTimeoutTestCase();
                    testCase.setExpectedOutputs(validList());
                    testCase.removeAllExpectedOutputs();
                    Assertions.assertDoesNotThrow(
                            testCase::removeAllExpectedOutputs,
                            "Cannot remove the expected outputs list more than one time. This should not fail"
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


    // ================================
    // Setters
    // ================================

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a null {@link TestCase.Visibility} to a {@link TestCase}.
     */
    @Test
    void testSetNullVisibility() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createAnyTimeoutTestCase().setVisibility(null),
                "Setting a null visibility is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a non positive timeout to a {@link TestCase}.
     */
    @Test
    void testSetNonPositiveTimeout() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createAnyTimeoutTestCase().setTimeout(nonPositiveTimeout()),
                "Setting a non positive timeout is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a null inputs {@link List} to a {@link TestCase}.
     */
    @Test
    void testSetNullInputsList() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createAnyTimeoutTestCase().setInputs(null),
                "Setting a null inputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting an empty inputs {@link List} to a {@link TestCase}.
     */
    @Test
    void testSetEmptyInputsList() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createAnyTimeoutTestCase().setInputs(Collections.emptyList()),
                "Setting an empty inputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting an inputs {@link List} containing a null element to a {@link TestCase}.
     */
    @Test
    void testSetInputsListWithNullElement() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createAnyTimeoutTestCase().setInputs(listWithNulls()),
                "Setting an input list with a null value is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting a null expected outputs {@link List} to a {@link TestCase}.
     */
    @Test
    void testSetNullExpectedOutputsList() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createAnyTimeoutTestCase().setExpectedOutputs(null),
                "Setting a null expected outputs list is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting an empty expected outputs {@link List} to a {@link TestCase}.
     */
    @Test
    void testSetEmptyExpectedOutputsList() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createAnyTimeoutTestCase().setExpectedOutputs(Collections.emptyList()),
                "Setting an empty expected outputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown
     * when setting an expected outputs {@link List} containing a null element to a {@link TestCase}.
     */
    @Test
    void testSetExpectedOutputsListWithNullElement() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createAnyTimeoutTestCase().setExpectedOutputs(listWithNulls()),
                "Setting an expected output list with a null value is being allowed"
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
        final var randomIndex = (int)  Faker.instance().number().numberBetween(0L, visibilities.length);
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
