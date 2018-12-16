/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.leadpony.fika.parser.markdown;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * Test fixture.
 * 
 * @author leadpony
 */
public class Fixture {
    
    private final int index;
    private final String source;
    private final String expected;
    
    Fixture(String source, String expected) {
        this.index = 0;
        this.source = source;
        this.expected = expected;
    }
    
    private Fixture(int index, String source, String expected) {
        this.index = index;
        this.source = source;
        this.expected = expected;
    }

    int index() {
        return index;
    }
    
    String source() {
        return source;
    }
    
    String expected() {
        return expected;
    }
    
    @Override
    public String toString() {
        return source();
    }
    
    public static Stream<Fixture> fromJson(String name) {
        try {
            return new FixtureLoader().loadFromJson(name);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private static class FixtureLoader {
        
        public Stream<Fixture> loadFromJson(String name) throws IOException {
            return readJsonArray(name).stream()
                    .map(JsonValue::asJsonObject)
                    .map(this::mapToFixture);
        }
        
        private JsonArray readJsonArray(String name) throws IOException {
            InputStream resource = getClass().getResourceAsStream(name);
            if (resource == null) {
                throw new IOException(name + " not found");
            }
            try (InputStream in = resource; JsonReader reader = Json.createReader(in)) {
                return reader.readArray();
            } catch (IOException e) {
                throw e;
            }
        }
        
        private Fixture mapToFixture(JsonObject object) {
            return new Fixture(
                    object.getInt("example"),
                    object.getString("markdown"), 
                    object.getString("html"));
        }
    }
}
