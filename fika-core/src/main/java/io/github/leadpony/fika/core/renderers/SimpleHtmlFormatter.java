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
package io.github.leadpony.fika.core.renderers;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author leadpony
 *
 */
public class SimpleHtmlFormatter implements HtmlFormatter {
    
    private final PrintWriter writer;
    
    public SimpleHtmlFormatter(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    @Override
    public void doctype(String type) {
        writer.append("<DOCTYPE ").append(type).append(">");
    }

    @Override
    public void startTag(String tagName, String... attributes) {
        writer.append("<").append(tagName);
        if (attributes.length > 0) {
            appendAttributes(attributes);
        }
        writer.append(">");
    }

    @Override
    public void endTag(String tagName) {
        writer.append("</").append(tagName).append(">");
    }

    @Override
    public void emptyTag(String tagName, String... attributes) {
        writer.append("<").append(tagName);
        if (attributes.length > 0) {
            appendAttributes(attributes);
        }
        writer.append(" />");
    }

    @Override
    public void text(String text) {
        text = text.replaceAll("&", "&amp;");
        text = text.replaceAll("\"", "&quot;");
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        writer.append(text);
    }

    @Override
    public void preformatted(String text) {
        writer.append(text);
    }
    
    private void appendAttributes(String[] attributes) {
        for (int i = 0; i + 1 < attributes.length; i += 2) {
            writer.append(" ")
                  .append(attributes[i]).append("=\"")
                  .append(attributes[i + 1]).append("\"");
        }
    }
}
