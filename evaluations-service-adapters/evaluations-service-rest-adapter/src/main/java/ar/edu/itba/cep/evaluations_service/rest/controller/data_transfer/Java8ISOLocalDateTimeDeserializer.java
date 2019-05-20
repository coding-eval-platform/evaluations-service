/*
 * Copyright 2018-2019 BellotApps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ar.edu.itba.cep.evaluations_service.rest.controller.data_transfer;

import com.bellotapps.webapps_commons.data_transfer.date_time.DateTimeFormatters;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * {@link com.fasterxml.jackson.databind.JsonDeserializer} to transform a {@link String} to a {@link LocalDateTime},
 * using {@link DateTimeFormatters#ISO_LOCAL_DATE_TIME} {@link DateTimeFormatter}.
 */
public class Java8ISOLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    /**
     * Default constructor.
     */
    protected Java8ISOLocalDateTimeDeserializer() {
        super(LocalDate.class);
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
