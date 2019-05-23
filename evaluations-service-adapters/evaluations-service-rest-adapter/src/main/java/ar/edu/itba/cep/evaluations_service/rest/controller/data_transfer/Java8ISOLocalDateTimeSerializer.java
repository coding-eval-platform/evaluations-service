package ar.edu.itba.cep.evaluations_service.rest.controller.data_transfer;

import com.bellotapps.webapps_commons.data_transfer.date_time.DateTimeFormatters;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link com.fasterxml.jackson.databind.JsonSerializer} to transform a {@link LocalDateTime} into a {@link String},
 * using {@link DateTimeFormatters#ISO_LOCAL_DATE_TIME} {@link DateTimeFormatter}.
 */
public class Java8ISOLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

    /**
     * Default constructor.
     */
    public Java8ISOLocalDateTimeSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(final LocalDateTime value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeString(DateTimeFormatters.ISO_LOCAL_DATE_TIME.getFormatter().format(value));

    }
}
