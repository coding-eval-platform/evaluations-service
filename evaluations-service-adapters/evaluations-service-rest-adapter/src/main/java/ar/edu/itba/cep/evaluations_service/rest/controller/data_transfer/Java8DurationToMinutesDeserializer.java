package ar.edu.itba.cep.evaluations_service.rest.controller.data_transfer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Duration;

/**
 * {@link com.fasterxml.jackson.databind.JsonDeserializer} to transform a {@link String} into a {@link Duration},
 * reading an integer number that represents minutes.
 */
public class Java8DurationToMinutesDeserializer extends StdDeserializer<Duration> {

    /**
     * Default constructor.
     */
    protected Java8DurationToMinutesDeserializer() {
        super(Duration.class);
    }

    @Override
    public Duration deserialize(final JsonParser p, final DeserializationContext context) throws IOException {
        final var durationString = p.getText();
        try {
            return Duration.ofMinutes(Long.parseLong(durationString));
        } catch (final NumberFormatException e) {
            throw new JsonParseException(p, "Unable to deserialize a duration", e);
        }
    }
}
