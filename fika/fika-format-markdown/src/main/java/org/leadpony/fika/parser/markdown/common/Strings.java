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

import static org.leadpony.fika.parser.markdown.common.Characters.isPunctuation;
import static org.leadpony.fika.parser.markdown.common.Characters.isWhitespace;
import static org.leadpony.fika.parser.markdown.common.Characters.sanitize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leadpony
 */
public final class Strings {
    
    public static String unescape(String s) {
        StringBuilder b = null;
        int last = 0;
        for (int i = 0; i < s.length() - 1; ) {
            if (s.charAt(i++) == '\\') {
                char next = s.charAt(i);
                if (isPunctuation(next)) {
                    if (b == null) {
                        b = new StringBuilder();
                    }
                    b.append(s.substring(last, i - 1));
                    b.append(next);
                    last = ++i;
                }
            }
        }
        if (last == 0) {
            return s;
        } else if (last < s.length()) {
            b.append(s.substring(last));
        }
        return b.toString();
    }
    
    public static String trimWhitespace(String s) {
        int leading = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (isWhitespace(s.charAt(i))) {
                ++leading;
            } else {
                break;
            }
        }
        int trailing = 0;
        for (int i = s.length() - 1; i > leading; --i) {
            if (isWhitespace(s.charAt(i))) {
                ++trailing;
            } else {
                break;
            }
        }
        if (leading > 0 || trailing > 0) {
            return s.substring(leading, s.length() - trailing);
        }
        return s;
    }
    
    public static String trimLeadingWhitespace(String s) {
        int leading = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (isWhitespace(s.charAt(i))) {
                ++leading;
            } else {
                break;
            }
        }
        return (leading > 0) ? s.substring(leading) : s;
    }
  
    private static final Pattern CONSECUTIVE_WHITESPACE = Pattern.compile("\\s+");
    
    public static String normalizeString(String s) {
        s = trimWhitespace(s);
        return CONSECUTIVE_WHITESPACE.matcher(s).replaceAll("\u0020");
    }
    
    public static final Pattern CHARACTER_REFERENCE_PATTERN = Pattern.compile(
            "&((#x([0-9a-f]{1,8}))|(#(\\d{1,8}))|(\\w+));",
            Pattern.CASE_INSENSITIVE);

    /**
     * Expands entity and numeric character references in specified string.
     * 
     * @param s the string to expand.
     * @return expanded string.
     */
    public static String expandReferences(String s) {
        Matcher m = CHARACTER_REFERENCE_PATTERN.matcher(s);
        if (!m.find()) {
            return s;
        }
        StringBuilder b = new StringBuilder();
        EntityResolver resolver = EntityResolver.get();
        int last = 0;
        do {
            b.append(s.substring(last, m.start()));
            if (m.group(3) != null) {
                // Hexadecimal numeric character
                int c = Integer.parseInt(m.group(3), 16);
                b.appendCodePoint(sanitize(c));
            } else if (m.group(5) != null) {
                // Decimal numeric character
                int c = Integer.parseInt(m.group(5));
                b.appendCodePoint(sanitize(c));
            } else {
                // Entity reference
                String resolved = resolver.resolve(m.group(1));
                if (resolved != null) {
                    b.append(resolved);
                } else {
                    b.append(m.group(0));
                }
            }
            last = m.end();
        } while (m.find());
        
        if (last < s.length()) {
            b.append(s.substring(last));
        }
        return b.toString();
    }
    
    public static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    private Strings() {
    }
}
