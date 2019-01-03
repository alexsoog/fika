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
package org.leadpony.fika.parser.markdown;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.assertj.core.api.AbstractAssert;

/**
 * Custom assertion for testing HTML content.
 * 
 * @author leadpony
 */
public class HtmlAssert extends AbstractAssert<HtmlAssert, String> {

    public static HtmlAssert assertThat(String actual) {
        return new HtmlAssert(actual);
    }
    
    private HtmlAssert(String actual) {
        super(normalize(actual), HtmlAssert.class);
    }

    public HtmlAssert isEqualTo(String expected) {
        expected = normalize(expected);
        return super.isEqualTo(expected);
    }
 
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
    
    private static String normalize(String html) {
        StringBuilder b = new StringBuilder();
        Matcher m = TAG_PATTERN.matcher(html);
        boolean preformatted = false;
        int last = 0;
        while (m.find()) {
            int index = m.start();
            if (last < index) {
                String content = html.substring(last, index);
                if (!preformatted) {
                    content = convertText(content);
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
            b.append(convertText(html.substring(last)));
        }
        return b.toString();
    }

    private static String convertText(String text) {
        if ("\n".equals(text)) {
            return "";
        } else if (text.startsWith("\n")){
            return text.substring(1);
        } else if (text.endsWith("\n")) {
            return text.substring(0, text.length() - 1);
        }
        return text;
    }
}
