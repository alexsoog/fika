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
package io.github.leadpony.fika.publication.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leadpony
 */
public class AntPathPattern {

    private static final Pattern WILD_CARDS = Pattern.compile(
            "^\\*\\*/|/\\*\\*/|\\?|\\*"
            );
    
    public static Pattern compile(String pattern) {
        StringBuilder builder = new StringBuilder();
        int last = 0;
        Matcher m = WILD_CARDS.matcher(pattern);
        while (m.find()) {
            builder.append(pattern.substring(last, m.start()));
            String matched = m.group();
            if (matched.equals("?")) {
                builder.append("[^/]?");
            } else if (matched.equals("*")) {
                builder.append("[^/]*?");
            } else {
                if (matched.length() > 3) {
                    builder.append("/");
                }
                builder.append("([^/]+/)*?");
            }
            last = m.end();
        }
        if (last < pattern.length()) {
            builder.append(pattern.substring(last));
        }
        return Pattern.compile(builder.toString());
    }
}
