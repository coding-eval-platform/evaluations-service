package ar.edu.itba.cep.evaluations_service.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for the {@link Language} enum.
 */
class LanguageTest {


    /**
     * Tests that the {@link Language#isCompiled()} method behaves as expected
     * (each language has the corresponding value).
     */
    @Test
    void testCompileProperty() {
        Assertions.assertAll(
                "The compiled property of the Language enums is not well defined.",
                () -> Assertions.assertTrue(Language.JAVA.isCompiled(), "Java is compiled"),
                () -> Assertions.assertFalse(Language.RUBY.isCompiled(), "Ruby is not compiled"),
                () -> Assertions.assertTrue(Language.C.isCompiled(), "C is compiled")
        );
    }

    /**
     * Tests the {@link Language#fromString(String)} method.
     */
    @Test
    void testFromString() {
        Assertions.assertAll(
                "The fromString method did not behave as expected",
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> Language.fromString(null),
                        "Did not throw an IllegalArgumentException when passing null"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> Language.fromString(Faker.instance().lorem().fixedString(10)),
                        "Did not throw an IllegalArgumentException when passing any value"
                ),
                () -> Assertions.assertAll(
                        "Did not return the JAVA value",
                        () -> Assertions.assertEquals(
                                Language.JAVA,
                                Language.fromString("JAVA"),
                                "When the value is all uppercase"
                        ),
                        () -> Assertions.assertEquals(
                                Language.JAVA,
                                Language.fromString("java"),
                                "When the value is all lowercase"
                        ),
                        () -> Assertions.assertEquals(
                                Language.JAVA,
                                Language.fromString("Java"),
                                "When the value is all capitalized"
                        ),
                        () -> Assertions.assertEquals(
                                Language.JAVA,
                                Language.fromString("JaVa"),
                                "When the value contains uppercase and lowercase letters."
                        )
                ),
                () -> Assertions.assertAll(
                        "Did not return the RUBY value",
                        () -> Assertions.assertEquals(
                                Language.RUBY,
                                Language.fromString("RUBY"),
                                "When the value is all uppercase"
                        ),
                        () -> Assertions.assertEquals(
                                Language.RUBY,
                                Language.fromString("ruby"),
                                "When the value is all lowercase"
                        ),
                        () -> Assertions.assertEquals(
                                Language.RUBY,
                                Language.fromString("Ruby"),
                                "When the value is all capitalized"
                        ),
                        () -> Assertions.assertEquals(
                                Language.RUBY,
                                Language.fromString("RuBy"),
                                "When the value contains uppercase and lowercase letters."
                        )
                ),
                () -> Assertions.assertAll(
                        "Did not return the JAVA value",
                        () -> Assertions.assertEquals(
                                Language.C,
                                Language.fromString("C"),
                                "When the value is all uppercase"
                        ),
                        () -> Assertions.assertEquals(
                                Language.C,
                                Language.fromString("c"),
                                "When the value is all lowercase"
                        )
                )
        );
    }
}
