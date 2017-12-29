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
package io.github.leadpony.fika.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leadpony
 */
public class HtmlMinifier {
    
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");

    public String minify(String html) {
        StringBuilder b = new StringBuilder();
        Matcher m = TAG_PATTERN.matcher(html);
        boolean preformatted = false;
        int last = 0;
        while (m.find()) {
            int index = m.start();
            if (last < index) {
                String content = html.substring(last, index);
                if (!preformatted) {
                    content = content.trim();
                }
                b.append(content);
            }
            String tag = m.group();
            if (tag.equals("<pre>")) {
                preformatted = true;
            } else if (tag.equals("</pre>")) {
                preformatted = false;
            }
            b.append(tag);
            last = m.end();
        }
        if (last < html.length()) {
            b.append(html.substring(last).trim());
        }
        return b.toString();
    }
}
