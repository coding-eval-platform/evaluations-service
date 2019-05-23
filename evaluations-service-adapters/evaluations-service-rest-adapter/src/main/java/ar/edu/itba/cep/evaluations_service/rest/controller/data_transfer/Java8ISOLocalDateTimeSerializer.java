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
