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
package org.leadpony.fika.parser.markdown.common;

import static org.leadpony.fika.parser.markdown.common.Strings.expandReferences;
import static org.leadpony.fika.parser.markdown.common.Strings.unescape;

/**
 * A link reference definition.
 * 
 * @author leadpony
 */
public class LinkDefinition {

    private final String label;
    private final String destination;
    private final String title;
    
    public LinkDefinition(String destination, String title) {
        this(null, destination, title);
    }
    
    public LinkDefinition(String label, String destination, String title) {
        this.label = label;
        this.destination = encodeDestination(destination);
        this.title = expandTitle(title);
    }

    public String label() {
        return label;
    }
    
    public String destination() {
        return destination;
    }
    
    public boolean hasTitle() {
        return title != null;
    }
    
    public String title() {
        return title;
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append('<').append(destination()).append('>');
        if (hasTitle()) {
            b.append(" \"").append(title()).append("\"");
        }
        return b.toString();
    }
    
    private String encodeDestination(String s) {
        if (s == null) {
            return "";
        }
        s = unescape(expandReferences(s));
        return new UrlEncoder().encode(s);
    }
    
    private String expandTitle(String s) {
        if (s == null) {
            return null;
        }
        return unescape(expandReferences(s));
    }
}
