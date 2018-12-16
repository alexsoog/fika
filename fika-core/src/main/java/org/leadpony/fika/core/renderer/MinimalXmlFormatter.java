/*
 * Copyright 2017-2018 the Fika authors.
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
package org.leadpony.fika.core.renderer;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @author leadpony
 */
public class MinimalXmlFormatter implements XmlFormatter {
    
    private PrintWriter writer;
    
    @Override
    public void setWriter(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    @Override
    public void doctype(String type) {
        writer.append("<!DOCTYPE ").append(type).append(">");
    }

    @Override
    public void startTag(String tagName, Map<String, String> attributes) {
        writer.append("<").append(tagName);
        if (!attributes.isEmpty()) {
            appendAttributes(attributes);
        }
        writer.append(">");
    }

    @Override
    public void endTag(String tagName) {
        writer.append("</").append(tagName).append(">");
    }

    @Override
    public void emptyTag(String tagName, Map<String, String> attributes) {
        writer.append("<").append(tagName);
        if (!attributes.isEmpty()) {
            appendAttributes(attributes);
        }
        writer.append(" />");
    }

    @Override
    public void text(String text) {
        writer.append(escape(text));
    }

    @Override
    public void preformatted(String text) {
        writer.append(escape(text));
    }
    
    @Override
    public void rawXml(String xml) {
        writer.append(xml);
    }
    
    private String escape(String text) {
        text = text.replaceAll("&", "&amp;");
        text = text.replaceAll("\"", "&quot;");
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        return text;
    }
    
    private void appendAttributes(Map<String, String> attributes) {
        for (Map.Entry<String, String> entry: attributes.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                value = "";
            } else {
                value = escape(value);
            }
            writer.append(" ")
                  .append(key).append("=\"")
                  .append(value).append("\"");
        }
    }
}
