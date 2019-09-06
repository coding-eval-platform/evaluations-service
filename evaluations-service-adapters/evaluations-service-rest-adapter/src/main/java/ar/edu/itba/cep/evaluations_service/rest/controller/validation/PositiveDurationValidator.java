package ar.edu.itba.cep.evaluations_service.rest.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

/**
 * {@link ConstraintValidator} for {@link Duration}s annotated with {@link PositiveDuration}.
 */
public class PositiveDurationValidator implements ConstraintValidator<PositiveDuration, Duration> {

    @Override
    public boolean isValid(final Duration value, final ConstraintValidatorContext context) {
        return !(value.isNegative() || value.isZero());
    }
}
