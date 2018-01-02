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
package io.github.leadpony.fika.parsers.markdown.inline.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.HtmlInline;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;
import io.github.leadpony.fika.parsers.markdown.inline.AbstractInlineHandler;

/**
 * @author leadpony
 *
 */
public class RawHtmlHandler extends AbstractInlineHandler {

    private static final char TRIGGER_LETTER = '<';
    
    private static final String TAG_NAME = "[a-zA-Z][a-zA-Z0-9-]*";
    private static final String ATTRIBUTE_NAME = "[a-zA-Z_:][a-zA-Z0-9_.\\:-]*";
    
    private static final String DOUBLE_QUOTED_ATTRIBUTE_VALUE =
            "\"[^\"]*\"";
    private static final String SINGLE_QUOTED_ATTRIBUTE_VALUE =
            "'[^']*'";
    private static final String UNQUOTED_ATTRIBUTE_VALUE =
            "[^\"'=<>`]+";
    private static final String ATTRIBUTE_VALUE =
            "(" +
            DOUBLE_QUOTED_ATTRIBUTE_VALUE + "|" +
            SINGLE_QUOTED_ATTRIBUTE_VALUE + "|" +
            UNQUOTED_ATTRIBUTE_VALUE +
            ")"
            ;
    private static final String ATTRIBUTE_VALUE_SPEC =
            "\\s*=\\s*" + 
            ATTRIBUTE_VALUE
            ;
    private static final String ATTRIBUTE =
            "\\s+" +
            ATTRIBUTE_NAME +
            "(" + ATTRIBUTE_VALUE_SPEC + ")?"
            ;
    private static final String OPEN_TAG =
            "<" + 
            TAG_NAME +
            "(" + ATTRIBUTE + ")*" +
            "\\s*/?>"
            ;
    private static final String CLOSING_TAG =
            "</" + 
            TAG_NAME +
            "\\s*>"
            ;
    private static final String HTML_COMMENT =
            "<!--(-[^>]|[^>])([^-]|-[^-])*-->";
            ;
    private static final String PROCESSING_INSTRUCTION =
            "<\\?.*?\\?>";
            ;
    private static final String DECLARATION =
            "<![a-zA-Z]+\\s+.+?>";
            ;
    private static final String CDATA_SECTION =
            "<!\\[CDATA\\[.*?\\]\\]>";
            ;
    private static final String HTML_TAG =
            "(" + OPEN_TAG + ")|" +
            "(" + CLOSING_TAG + ")|" +
            "(" + HTML_COMMENT + ")|" +     
            "(" + PROCESSING_INSTRUCTION + ")|" +     
            "(" + DECLARATION + ")|" +     
            "(" + CDATA_SECTION + ")"     
            ;
    private static final Pattern RAW_HTML_PATTERN = Pattern.compile(HTML_TAG);
    
    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }

    @Override
    public int handleContent(InputSequence input) {
        Matcher m = RAW_HTML_PATTERN.matcher(input);
        if (m.lookingAt()) {
            appendNode(buildNode(m.group()));
            return m.end();
        }
        return 0;
    }
    
    private HtmlInline buildNode(String html) {
        HtmlInline newNode = nodeFactory().newHtmlInline();
        newNode.setHtml(html);
        return newNode;
    }
}
