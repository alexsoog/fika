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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leadpony
 *
 */
public class LinkDefinitionParser {

    private static final String LINK_LABEL =
            "\\[(?<label>(\\\\\\]|[^\\]])+)\\]:";

    private static final String LINK_DESTINATION =
            "(?<destination><(\\[<>]|[^\u0020\n<>])*>|(\\\\[()]|[^\u0020\\p{Cntrl}])+)";
 
    private static final String LINK_TITLE =
            "(?<title>" +
            "(\"(\\\\\"|[^\"])*\")|" +
            "('(\\\\'|[^'])*')|" +
            "(\\((\\\\\\)|[^\\)])*\\))" +
            ")";
 
    public static final String LINK_DEFINITION =
            "^\\p{Blank}*" + LINK_LABEL +
            "\\s*" + LINK_DESTINATION +
            "(\\s+" + LINK_TITLE + ")?" +
            "\\s*($|\\n)";
    
    private static final String INLINE_LINK =
            "\\(\\s*" +
            LINK_DESTINATION + "?" +
           "(\\s+" + LINK_TITLE + ")?" +        
            "\\s*\\)";

    private static final Pattern LINK_DEFINITION_PATTERN = Pattern.compile(LINK_DEFINITION);
    private static final Pattern INLINE_LINK_PATTERN = Pattern.compile(INLINE_LINK);
    
    private int length;
    private String label;
    
    public LinkDefinition parseLinkDefinition(CharSequence input, int offset) {
        Matcher m = LINK_DEFINITION_PATTERN.matcher(input);
        m.region(offset, input.length());
        if (m.lookingAt()) {
            String destination = m.group("destination");
            String title = m.group("title");
            this.length = m.group().length();
            this.label = m.group("label");
            return createLinkDestination(destination, title);
        } else {
            this.length = 0;
            this.label = null;
            return null;
        }
    }

    public LinkDefinition parseInlineLink(CharSequence input, int offset) {
        Matcher m = INLINE_LINK_PATTERN.matcher(input);
        m.region(offset, input.length());
        if (m.lookingAt()) {
            String destination = m.group("destination");
            String title = m.group("title");
            this.length = m.group().length();
            return createLinkDestination(destination, title);
        } else {
            this.length = 0;
            return null;
        }
    }
    
    public String label() {
        return label;
    }
    
    public int length() {
        return length;
    }
    
    private LinkDefinition createLinkDestination(String destination, String title) {
        return new LinkDefinition(
                unquoteDestination(destination), 
                unquoteTitle(title));
    }

    private static String unquoteDestination(String s) {
        if (s == null) {
            return "";
        }
        if (s.length() >= 2) {
            if (s.charAt(0) == '<' && s.charAt(s.length() - 1) == '>') {
                s = s.substring(1, s.length() - 1);
            }
        }
        s = expandReferences(s);
        s = unescape(s);
        UrlEncoder encoder = new UrlEncoder();
        return encoder.encode(s);
    }

    private static String unquoteTitle(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() >= 2) {
            char first = s.charAt(0);
            char last = s.charAt(s.length() - 1);
            if ((first == '"' && last == '"') ||
                (first == '\'' && last == '\'') ||
                (first == '(' && last == ')')) {
                s = s.substring(1, s.length() - 1);
            }
        }
        s = expandReferences(s);
        return unescape(s);
    }
}
