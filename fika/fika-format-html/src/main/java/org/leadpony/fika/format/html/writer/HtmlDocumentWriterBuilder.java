/*
 * Copyright 2017-2019 the Fika authors.
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

package org.leadpony.fika.format.html.writer;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.leadpony.fika.core.DocumentWriter;
import org.leadpony.fika.core.DocumentWriterBuilder;

/**
 * @author leadpony
 */
public class HtmlDocumentWriterBuilder implements DocumentWriterBuilder {

    private final Writer writer;
    private final Map<String, Object> properties = new HashMap<>();

    public HtmlDocumentWriterBuilder(Writer writer) {
        this.writer = writer;
    }

    @Override
    public DocumentWriterBuilder withProperty(String name, Object value) {
        properties.put(name, value);
        return this;
    }

    @Override
    public DocumentWriter build() {
        return new HtmlDocumentWriter(writer, properties);
    }
}