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
package org.leadpony.fika.parser.renderer;

import java.io.Writer;
import java.util.Collections;
import java.util.Map;

/**
 * A formatter interface which will format the rendered XML document before presenting.
 *
 * @author leadpony
 */
public interface XmlFormatter {

    /**
     * Assigns the writer with which the formatted XML document will be written.
     *
     * @param writer the object with which this formatter will write the XML
     *               document.
     * @return this formatter.
     * @throws NullPointerException if the specified {@code writer} is {@code null}.
     */
    XmlFormatter withWriter(Writer writer);

    /**
     * Outputs a document type declaration.
     *
     * @param type the type of the document, such as "html".
     */
    void doctype(String type);

    /**
     * Outputs a start tag without any attributes.
     *
     * @param name the name of the tag.
     */
    default void startTag(String name) {
        startTag(name, Collections.emptyMap());
    }

    /**
     * Outputs a start tag with element attributes.
     *
     * @param name       the name of the tag.
     * @param attributes the attributes of the tag.
     */
    void startTag(String name, Map<String, String> attributes);

    /**
     * Outputs an end tag.
     *
     * @param name the name of the tag.
     */
    void endTag(String name);

    /**
     * Outputs an empty tag without any attributes.
     *
     * @param name the name of the tag.
     */
    default void emptyTag(String name) {
        emptyTag(name, Collections.emptyMap());
    }

    /**
     * Outputs an empty tag with element attributes.
     *
     * @param name       the name of the tag.
     * @param attributes the attributes of the tag.
     */
    void emptyTag(String name, Map<String, String> attributes);

    /**
     * Outputs text content.
     *
     * @param text the text content to output.
     */
    void text(String text);

    /**
     * Outputs a preformatted text.
     *
     * @param text the preformatted text to output.
     */
    void preformattedText(String text);

    /**
     * Outputs raw XML content.
     *
     * @param xml the raw XML content to output.
     */
    void rawXml(String xml);
}
