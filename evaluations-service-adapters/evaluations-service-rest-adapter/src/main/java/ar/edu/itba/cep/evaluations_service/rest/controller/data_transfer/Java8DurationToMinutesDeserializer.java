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
