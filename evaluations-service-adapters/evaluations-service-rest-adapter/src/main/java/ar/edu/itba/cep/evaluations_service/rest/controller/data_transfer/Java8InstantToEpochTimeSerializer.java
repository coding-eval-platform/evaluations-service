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
