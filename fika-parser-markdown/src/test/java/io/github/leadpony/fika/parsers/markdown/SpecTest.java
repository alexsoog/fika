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
package io.github.leadpony.fika.parsers.markdown;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author leadpony
 *
 */
@RunWith(Parameterized.class)
public class SpecTest extends AbstractTest {

    @Parameters(name = "{0}: {1}")
    public static Iterable<Object[]> parameters() {
        JsonArray array = loadSpecJson("/spec.json");
        Iterator<Object[]> it = array.stream()
            .map(v->v.asJsonObject())
            .map(v->new Object[] {v.getInt("example"), v.getString("markdown"), v.getString("html")})
            .iterator();
        return ()->it;
    }
    
    public SpecTest(int example, String source, String expected) {
        super(source, expected);
    }
    
    private static JsonArray loadSpecJson(String path) {
        try (InputStream in = SpecTest.class.getResourceAsStream(path)) {
            JsonReader reader = Json.createReader(in);
            return reader.readArray();
        } catch (IOException e) {
            return null;
        }
    }
}
