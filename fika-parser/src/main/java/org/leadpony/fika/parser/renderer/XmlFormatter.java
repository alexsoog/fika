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
 * @author leadpony
 */
public interface XmlFormatter {
    
    void setWriter(Writer writer);
    
    void doctype(String type);
    
    default void startTag(String tagName) {
        startTag(tagName, Collections.emptyMap());
    }

    void startTag(String tagName, Map<String, String> attributes);

    void endTag(String tagName);

    default void emptyTag(String tagName) {
        emptyTag(tagName, Collections.emptyMap());
    }

    void emptyTag(String tagName, Map<String, String> attributes);

    void text(String text);
    
    void preformatted(String text);
    
    void rawXml(String xml);
}
