package ar.edu.itba.cep.evaluations_service.spring_data.jpa.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;
import java.util.Optional;

/**
 * {@link AttributeConverter} for mapping a {@link Duration} into {@link Long}, in nanoseconds.
 */
@Converter
public class DurationConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(final Duration attribute) {
        return Optional.ofNullable(attribute).map(Duration::toNanos).orElse(null);
    }

    @Override
    public Duration convertToEntityAttribute(final Long dbData) {
        return Optional.ofNullable(dbData).map(Duration::ofNanos).orElse(null);
    }
}
