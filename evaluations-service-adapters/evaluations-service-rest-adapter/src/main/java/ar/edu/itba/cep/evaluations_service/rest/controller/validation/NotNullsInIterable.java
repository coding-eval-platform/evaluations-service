package ar.edu.itba.cep.evaluations_service.rest.controller.validation;

import ar.edu.itba.cep.evaluations_service.rest.controller.validation.NotNullsInIterable.List;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated {@link Iterable} must not contain {@code null} values.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)

@Documented
@Constraint(validatedBy = {
        NotNullsInIterableValidator.class
})
@Repeatable(List.class)
public @interface NotNullsInIterable {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @NotNullsInIterable} constraints on the same element.
     *
     * @see NotNullsInIterable
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        NotNullsInIterable[] value();
    }
}
