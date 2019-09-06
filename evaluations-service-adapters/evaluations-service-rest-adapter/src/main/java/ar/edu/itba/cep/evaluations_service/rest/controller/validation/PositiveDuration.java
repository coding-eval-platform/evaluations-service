package ar.edu.itba.cep.evaluations_service.rest.controller.validation;

import ar.edu.itba.cep.evaluations_service.rest.controller.validation.PositiveDuration.List;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.time.Duration;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated {@link java.time.Duration} must be positive
 * (i.e both {@link Duration#isNegative()} and {@link Duration#isZero()}must return {@code false})
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {
        PositiveDurationValidator.class
})
@Repeatable(List.class)
public @interface PositiveDuration {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @PositiveDuration} constraints on the same element.
     *
     * @see PositiveDuration
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        PositiveDuration[] value();
    }
}
