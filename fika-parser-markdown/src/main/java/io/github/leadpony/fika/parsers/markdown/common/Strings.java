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

import static io.github.leadpony.fika.parsers.markdown.common.Characters.isPunctuation;
import static io.github.leadpony.fika.parsers.markdown.common.Characters.isWhitespace;

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
        for (int i = s.length() - 1; i >= 0; --i) {
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
    
    public static String unquote(String s) {
        if (s.length() < 2) {
            return s;
        }
        char first = s.charAt(0);
        if (first == '"' || first == '\'') {
            char last = s.charAt(s.length() - 1);
            if (first == last) {
                return s.substring(1, s.length() - 1); 
            }
        }
        return s;
    }
    
    private Strings() {
    }
}
