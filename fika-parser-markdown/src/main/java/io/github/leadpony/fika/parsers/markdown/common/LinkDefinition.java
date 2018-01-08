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
package io.github.leadpony.fika.parsers.markdown.common;

import static io.github.leadpony.fika.parsers.markdown.common.Strings.expandReferences;
import static io.github.leadpony.fika.parsers.markdown.common.Strings.unescape;

/**
 * @author leadpony
 *
 */
public class LinkDefinition {

    private final String destination;
    private final String title;
    
    public LinkDefinition(String destination, String title) {
        this.destination = normalizeDestination(destination);
        this.title = normalizeTitle(title);
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

    private static String normalizeDestination(String s) {
        if (s == null) {
            return "";
        }
        s = unquoteDestination(s);
        s = expandReferences(s);
        s = unescape(s);
        UrlEncoder encoder = new UrlEncoder();
        return encoder.encode(s);
    }
    
    private static String unquoteDestination(String s) {
        if (s.length() >= 2) {
            if (s.charAt(0) == '<' && s.charAt(s.length() - 1) == '>') {
                s = s.substring(1, s.length() - 1);
            }
        }
        return s;
    }

    private static String normalizeTitle(String s) {
        if (s == null) {
            return null;
        }
        s = unquoteTitle(s);
        s = expandReferences(s);
        return unescape(s);
    }
    
    private static String unquoteTitle(String s) {
        if (s.length() >= 2) {
            char first = s.charAt(0);
            char last = s.charAt(s.length() - 1);
            if ((first == '"' && last == '"') ||
                (first == '\'' && last == '\'') ||
                (first == '(' && last == ')')) {
                s = s.substring(1, s.length() - 1);
            }
        }
        return s;
    }
}
