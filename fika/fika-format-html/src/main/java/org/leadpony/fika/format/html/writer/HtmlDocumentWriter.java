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

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.leadpony.fika.core.DocumentException;
import org.leadpony.fika.core.DocumentWriter;
import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.Visitor;

/**
 * A writer for writing a HTML document.
 *
 * @author leadpony
 */
class HtmlDocumentWriter implements DocumentWriter {

    private final Writer writer;
    private final Map<String, Object> properties;
    private boolean alreadyWritten;
    private boolean alreadyClosed;
    private final XmlFormatter formatter;
    private final Visitor visitor;

    HtmlDocumentWriter(Writer writer, Map<String, Object> properties) {
        this.writer = writer;
        this.properties = properties;
        this.formatter = new SimpleXmlFormatter().withWriter(writer);
        this.visitor = buildVisitor(this.formatter);
        this.alreadyWritten = this.alreadyClosed = false;
    }

    @Override
    public void write(Node node) {
        if (alreadyWritten) {
            throw new IllegalStateException();
        }
        alreadyWritten = true;
        requireNonNull(node, "node must not be null.");
        if (hasOption(FRAGMENT_ONLY)) {
            writeDocumentFragment(node);
        } else {
            writeCompleteDocument(node);
        }
    }

    @Override
    public void close() {
        if (!alreadyClosed) {
            alreadyClosed = true;
            try {
                this.writer.close();
            } catch (IOException e) {
                throw new DocumentException("An I/O error has occurred.", e);
            }
        }
    }

    private boolean hasOption(String name) {
        if (properties.containsKey(name)) {
            Object value = properties.get(name);
            if (value instanceof Boolean) {
                return (boolean)value;
            }
        }
        return false;
    }

    private void writeCompleteDocument(Node node) {
        formatter.doctype("html");
        Map<String, String> attributes = new HashMap<>();
        if (properties.containsKey(LANGUAGE)) {
            Locale language = (Locale)properties.get(LANGUAGE);
            attributes.put("lang", language.toLanguageTag());
        }
        formatter.startTag("html", attributes);
        writeHeadPart();
        writeBodyPart(node);
        formatter.endTag("html");
    }

    private void writeDocumentFragment(Node node) {
        node.accept(visitor);
    }

    private void writeHeadPart() {
        formatter.startTag("head");

        if (properties.containsKey(CHARSET)) {
            String charset = (String)properties.get(CHARSET);
            Map<String, String> attributes = new HashMap<>();
            attributes.put("charset", charset);
            formatter.emptyTag("meta", attributes);
        }

        if (properties.containsKey(TITLE)) {
            String title = (String)properties.get(TITLE);
            formatter.startTag("title");
            formatter.text(title);
            formatter.endTag("title");
        }

        writeAllStylesheets();

        formatter.endTag("head");
    }

    private void writeBodyPart(Node node) {
        formatter.startTag("body");
        writeDocumentFragment(node);
        formatter.endTag("body");
    }

    private void writeAllStylesheets() {
        if (properties.containsKey(STYLESHEETS)) {
            @SuppressWarnings("unchecked")
            List<URI> locations = (List<URI>)properties.get(STYLESHEETS);
            for (URI location: locations) {
                Map<String, String> attributes = new HashMap<>();
                attributes.put("href", location.toString());
                attributes.put("rel", "stylesheet");
                formatter.emptyTag("link", attributes);
            }
        }
    }

    private Visitor buildVisitor(XmlFormatter formatter) {
        return new HtmlRenderingVisitor(formatter);
    }
}
