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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Characters defined in CommonMark spec.
 * 
 * @author leadpony
 */
public final class Characters {
    
    public static final char SPACE = '\u0020';
    private static final Pattern BACKSLASH_CAPE = Pattern.compile("\\\\(\\p{Punct})");
    private static final Pattern LEADING_TRAILING_WHITESPACE = Pattern.compile("^\\s+|\\s+$");
 
    public static String unescape(String input) {
        Matcher m = BACKSLASH_CAPE.matcher(input);
        return m.replaceAll("$1");
    }
    
    public static String trim(String input) {
        Matcher m = LEADING_TRAILING_WHITESPACE.matcher(input);
        return m.replaceAll("");
    }

    private Characters() {
    }
}
