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
package io.github.leadpony.fika.parsers.markdown.base;

import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Characters defined in CommonMark spec.
 * 
 * @author leadpony
 */
public final class Characters {
    
    public static final char SPACE = '\u0020';
    public static final char[] ASCII_PUNCTUATIONS = {
        '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',',
        '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\',
        ']', '^', '_', '`', '{', '|', '}', '~'
    };
 
    @SuppressWarnings("serial")
    private static final BitSet ASCII_PUNCTUATION_SET = new BitSet() {{
        for (char ch: ASCII_PUNCTUATIONS) {
            set(ch);
        }
    }};
    private static final Pattern BACKSLASH_ESCAPE = Pattern.compile("\\\\(\\p{Punct})");
    
    private static final Pattern LEADING_TRAILING_WHITESPACE = Pattern.compile("^\\s+|\\s+$");
   
    public static String unescape(String input) {
        Matcher m = BACKSLASH_ESCAPE.matcher(input);
        return m.replaceAll("$1");
    }
    
    public static String trim(String input) {
        Matcher m = LEADING_TRAILING_WHITESPACE.matcher(input);
        return m.replaceAll("");
    }
    
    public static boolean isPunctuation(int ch) {
        if (ASCII_PUNCTUATION_SET.get(ch)) {
            return true;
        }
        switch (Character.getType(ch)) {
        case Character.CONNECTOR_PUNCTUATION: /* Pc */
        case Character.DASH_PUNCTUATION: /* Pd */
        case Character.END_PUNCTUATION: /* Pe */
        case Character.FINAL_QUOTE_PUNCTUATION: /* Pf */
        case Character.INITIAL_QUOTE_PUNCTUATION: /* Pi */
        case Character.OTHER_PUNCTUATION: /* Po */
        case Character.START_PUNCTUATION: /* Ps */    
            return true;
        }
        return false;
    }
    
    public static boolean isUnicodeWhitespace(int ch) {
        if (ch == '\t' || ch == '\r' || ch == '\n' || ch == '\f') {
            return true;
        }
        return (Character.getType(ch) == Character.SPACE_SEPARATOR);
    }

    private Characters() {
    }
}
