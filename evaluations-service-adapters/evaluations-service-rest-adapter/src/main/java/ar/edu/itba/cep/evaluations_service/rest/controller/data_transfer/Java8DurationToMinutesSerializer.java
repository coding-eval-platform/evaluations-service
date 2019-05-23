package ar.edu.itba.cep.evaluations_service.rest.controller.data_transfer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

/**
 * {@link com.fasterxml.jackson.databind.JsonSerializer} to transform a {@link Duration} into an integer number
 * representing minutes.
 */
public class Java8DurationToMinutesSerializer extends StdSerializer<Duration> {

    /**
     * Default constructor.
     */
    public Java8DurationToMinutesSerializer() {
        super(Duration.class);
    }

    @Override
    public void serialize(final Duration value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeNumber(value.toMinutes());
    }
}
