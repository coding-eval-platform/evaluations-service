package ar.edu.itba.cep.evaluations_service.rest.controller.data_transfer;

import com.bellotapps.webapps_commons.data_transfer.date_time.DateTimeFormatters;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * {@link com.fasterxml.jackson.databind.JsonDeserializer} to transform a {@link String} into a {@link LocalDateTime},
 * using {@link DateTimeFormatters#ISO_LOCAL_DATE_TIME} {@link DateTimeFormatter}.
 */
public class Java8ISOLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    /**
     * Default constructor.
     */
    protected Java8ISOLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(final JsonParser p, final DeserializationContext context) throws IOException {
        final var dateString = p.getText();
        try {
            return LocalDateTime.from(DateTimeFormatters.ISO_LOCAL_DATE_TIME.getFormatter().parse(dateString));
        } catch (final DateTimeParseException e) {
            throw new JsonParseException(p, "Unable to deserialize the date-time", e);
        }
    }
}
