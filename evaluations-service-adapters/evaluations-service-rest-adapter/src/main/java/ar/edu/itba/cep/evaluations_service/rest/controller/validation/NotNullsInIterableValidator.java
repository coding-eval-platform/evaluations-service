package ar.edu.itba.cep.evaluations_service.rest.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * {@link ConstraintValidator} for {@link Iterable}s annotated with {@link NotNullsInIterable}.
 */
public class NotNullsInIterableValidator implements ConstraintValidator<NotNullsInIterable, Iterable<?>> {

    @Override
    public boolean isValid(final Iterable<?> value, final ConstraintValidatorContext context) {
        if (value == null) {
            // If a null value is used, then return true (this validator is not intended to validate nulls).
            return true;
        }
        return StreamSupport.stream(value.spliterator(), false).noneMatch(Objects::isNull);
    }
}
