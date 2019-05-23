package ar.edu.itba.cep.evaluations_service.rest.controller.data_transfer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;

/**
 * {@link com.fasterxml.jackson.databind.JsonSerializer} to transform an {@link Instant} into a number representing
 * epoch time.
 */
public class Java8InstantToEpochTimeSerializer extends StdSerializer<Instant> {

    /**
     * Default constructor.
     */
    public Java8InstantToEpochTimeSerializer() {
        super(Instant.class);
    }

    @Override
    public void serialize(final Instant value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeNumber(value.toEpochMilli());
    }
}
