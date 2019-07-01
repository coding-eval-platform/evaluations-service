package ar.edu.itba.cep.evaluations_service.models;

import lombok.Getter;
import org.springframework.util.Assert;

/**
 * Enum containing the different programming languages supported by the evaluations service.
 */
@Getter
public enum Language {
    /**
     * The Java programming language.
     */
    JAVA(true),
    /**
     * The Ruby programming language.
     */
    RUBY(false),
    /**
     * The C programming language.
     */
    C(true),
    ;


    /**
     * Indicates whether this language is compiled.
     */
    private final boolean compiled;


    /**
     * Constructor.
     *
     * @param compiled Indicates whether this language is compiled.
     */
    Language(final boolean compiled) {
        this.compiled = compiled;
    }


    /**
     * Returns the {@link Language} instance from the given {@code value}.
     *
     * @param value The {@link String} representing the {@link Language} to be returned.
     * @return The {@link Language} of the specified {@code value}.
     * @throws IllegalArgumentException If the given {@code value} is {@code null}, or there is no {@link Language}
     *                                  matching the given {@code value}.
     */
    public static Language fromString(final String value) throws IllegalArgumentException {
        Assert.notNull(value, "The value must not be null");
        return valueOf(value.toUpperCase());
    }
}
