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

import java.util.BitSet;

/**
 * Characters defined in CommonMark specification.
 * 
 * @author leadpony
 */
public final class Characters {
    
    public static final char SPACE = '\u0020';
    public static final int REPLACEMENT_CHARACTER = '\uFFFD';
    
    private static final char[] ASCII_PUNCTUATIONS = {
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
    
    public static boolean isWhitespace(int c) {
        return c == '\u0020' || 
               c == '\t' || 
               c == '\r' || 
               c == '\n' || 
               c == '\f' ||
               c == '\u000b';
    }
    
    public static boolean isUnicodeWhitespace(int c) {
        if (c == '\t' || c == '\r' || c == '\n' || c == '\f') {
            return true;
        }
        return (Character.getType(c) == Character.SPACE_SEPARATOR);
    }

    public static boolean isPunctuation(int c) {
        return ASCII_PUNCTUATION_SET.get(c);
    }
    
    public static boolean isUnicodePunctuation(int c) {
        if (isPunctuation(c)) {
            return true;
        }
        switch (Character.getType(c)) {
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
    
    public static int sanitize(int c) {
        if (c == 0 || !Character.isValidCodePoint(c)) {
            return REPLACEMENT_CHARACTER;
        }
        return c;
    }

    private Characters() {
    }
}
