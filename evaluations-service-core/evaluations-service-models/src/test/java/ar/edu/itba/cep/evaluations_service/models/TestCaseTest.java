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
 * Test class for {@link Exercise}s
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
        Assertions.assertDoesNotThrow(
                this::createTestCase,
                "Test cases with acceptable arguments are not being created"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    @Test
    void testSetValidVisibility() {
        Assertions.assertAll("Setting a valid visibility is not being allowed",
                () -> Assertions.assertDoesNotThrow(
                        () -> createTestCase().setVisibility(TestCase.Visibility.PRIVATE),
                        "Cannot set visibility to private"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> createTestCase().setVisibility(TestCase.Visibility.PUBLIC),
                        "Cannot set visibility to public"
                ),
                () -> {
                    final var testCase = createTestCase();
                    testCase.setVisibility(TestCase.Visibility.PRIVATE);
                    Assertions.assertSame(
                            TestCase.Visibility.PRIVATE,
                            testCase.getVisibility(),
                            "Visibility is not being set to private (does not change the TestCase value)"
                    );
                },
                () -> {
                    final var testCase = createTestCase();
                    testCase.setVisibility(TestCase.Visibility.PUBLIC);
                    Assertions.assertSame(
                            TestCase.Visibility.PUBLIC,
                            testCase.getVisibility(),
                            "Visibility is not being set to public (does not change the TestCase value)"
                    );
                }
        );
    }

    @Test
    void testSetValidInputsList() {
        Assertions.assertAll("Setting a valid inputs list is not being allowed",
                () -> Assertions.assertDoesNotThrow(
                        () -> createTestCase().setInputs(validList()),
                        "Setting a valid inputs list is failing"
                ),
                () -> {
                    final var testCase = createTestCase();
                    final var inputsList = validList();
                    testCase.setInputs(inputsList);
                    Assertions.assertEquals(
                            inputsList,
                            testCase.getInputs(),
                            "The inputs lists is not being set (does not change the TestCase value)"
                    );
                }
        );
    }

    @Test
    void testSetValidExpectedOutputsList() {
        Assertions.assertDoesNotThrow(
                () -> createTestCase().setExpectedOutputs(validList()),
                "Setting a valid expected outputs list is not being allowed"
        );

        Assertions.assertAll("Setting a valid expected outputs list is not being allowed",
                () -> Assertions.assertDoesNotThrow(
                        () -> createTestCase().setExpectedOutputs(validList()),
                        "Setting a valid expected outputs list is failing"
                ),
                () -> {
                    final var testCase = createTestCase();
                    final var outputsList = validList();
                    testCase.setExpectedOutputs(outputsList);
                    Assertions.assertEquals(
                            outputsList,
                            testCase.getExpectedOutputs(),
                            "The expected outputs lists is not being set (does not change the TestCase value)"
                    );
                }
        );
    }


    // ================================================================================================================
    // Behaviour testing
    // ================================================================================================================

    /**
     * Tests that clearing the inputs list works as expected
     */
    @Test
    void testClearInputs() {
        Assertions.assertAll("Removing inputs is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createTestCase().removeAllInputs(),
                        "Cannot remove inputs to a new created test case. This should not fail"
                ),
                () -> {
                    final var testCase = createTestCase();
                    testCase.setInputs(validList());
                    testCase.removeAllInputs();
                    Assertions.assertTrue(
                            testCase.getInputs().isEmpty(),
                            "Test cases are not returning an empty inputs list after removing them"
                    );
                },
                () -> {
                    final var testCase = createTestCase();
                    testCase.setInputs(validList());
                    testCase.removeAllInputs();
                    Assertions.assertDoesNotThrow(
                            testCase::removeAllInputs,
                            "Cannot remove the inputs list more than one time. This should not fail"
                    );
                }
        );
    }

    /**
     * Tests that clearing the expected outputs list works as expected
     */
    @Test
    void testClearExpectedOutputs() {
        Assertions.assertAll("Removing expected outputs is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> createTestCase().removeAllExpectedOutputs(),
                        "Cannot remove expected outputs to a new created test case. This should not fail"
                ),
                () -> {
                    final var testCase = createTestCase();
                    testCase.setExpectedOutputs(validList());
                    testCase.removeAllExpectedOutputs();
                    Assertions.assertTrue(
                            testCase.getExpectedOutputs().isEmpty(),
                            "Test cases are not returning an empty expected outputs list after removing them"
                    );
                },
                () -> {
                    final var testCase = createTestCase();
                    testCase.setExpectedOutputs(validList());
                    testCase.removeAllExpectedOutputs();
                    Assertions.assertDoesNotThrow(
                            testCase::removeAllExpectedOutputs,
                            "Cannot remove the expected outputs list more than one time. This should not fail"
                    );
                }
        );
    }


    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    // ================================
    // Creation
    // ================================

    /**
     * Tests that a {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with a null {@link TestCase.Visibility}.
     */
    @Test
    void testNullVisibilityOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(null, mockedExercise),
                "Creating a test case with a null visibility is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }


    /**
     * Tests that a {@link IllegalArgumentException} is thrown
     * when creating a {@link TestCase} with a null {@link Exercise}.
     */
    @Test
    void testNullExamOnCreation() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new TestCase(validVisibility(), null),
                "Creating a test case with a null exercise is being allowed"
        );
    }


    // ================================
    // Setters
    // ================================

    /**
     * Tests that a {@link IllegalArgumentException} is thrown
     * when setting a null {@link TestCase.Visibility} to a {@link TestCase}.
     */
    @Test
    void testSetNullVisibility() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createTestCase().setVisibility(null),
                "Setting a null visibility is being allowed."
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown
     * when setting a null inputs {@link List} to a {@link TestCase}.
     */
    @Test
    void testSetNullInputsList() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createTestCase().setInputs(null),
                "Setting a null inputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown
     * when setting an empty inputs {@link List} to a {@link TestCase}.
     */
    @Test
    void testSetEmptyInputsList() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createTestCase().setInputs(Collections.emptyList()),
                "Setting an empty inputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown
     * when setting an inputs {@link List} containing a null element to a {@link TestCase}.
     */
    @Test
    void testSetInputsListWithNullElement() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createTestCase().setInputs(listWithNulls()),
                "Setting an input list with a null value is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown
     * when setting a null expected outputs {@link List} to a {@link TestCase}.
     */
    @Test
    void testSetNullExpectedOutputsList() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createTestCase().setExpectedOutputs(null),
                "Setting a null expected outputs list is being allowed"
        );
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown
     * when setting an empty expected outputs {@link List} to a {@link TestCase}.
     */
    @Test
    void testSetEmptyExpectedOutputsList() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createTestCase().setExpectedOutputs(Collections.emptyList()),
                "Setting an empty expected outputs list is being allowed");
        Mockito.verifyZeroInteractions(mockedExercise);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown
     * when setting an expected outputs {@link List} containing a null element to a {@link TestCase}.
     */
    @Test
    void testSetExpectedOutputsListWithNullElement() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> createTestCase().setExpectedOutputs(listWithNulls()),
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
     * Creates a valid {@link TestCase}.
     *
     * @return a {@link TestCase}.
     */
    private TestCase createTestCase() {
        return new TestCase(
                validVisibility(),
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
        final var randomIndex = Faker.instance().number().numberBetween(0, visibilities.length);
        return visibilities[randomIndex];
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
