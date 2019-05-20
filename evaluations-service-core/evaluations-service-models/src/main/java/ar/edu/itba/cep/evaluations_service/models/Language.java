package ar.edu.itba.cep.evaluations_service.models;

import org.springframework.util.Assert;

/**
 * Enum containing the different programming languages supported by the evaluations service.
 */
public enum Language {
    /**
     * The Java programming language.
     */
    JAVA,
    /**
     * The Ruby programming language.
     */
    RUBY,
    /**
     * The C programming language.
     */
    C,
    ;


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
