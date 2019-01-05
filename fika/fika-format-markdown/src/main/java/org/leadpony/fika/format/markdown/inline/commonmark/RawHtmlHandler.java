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
package org.leadpony.fika.format.markdown.inline.commonmark;

import java.util.regex.Matcher;

import org.leadpony.fika.format.markdown.common.HtmlMatchers;
import org.leadpony.fika.format.markdown.common.InputSequence;
import org.leadpony.fika.format.markdown.inline.AbstractInlineHandler;
import org.leadpony.fika.format.markdown.inline.HandlerType;
import org.leadpony.fika.parser.model.HtmlInline;

/**
 * @author leadpony
 */
public class RawHtmlHandler extends AbstractInlineHandler {

    private static final char TRIGGER_LETTER = '<';
    
    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }

    @Override
    public HandlerType handlerType() {
        return BasicHandlerType.RAW_HTML;
    }
    
    @Override
    public int handleContent(InputSequence input) {
        Matcher m = HtmlMatchers.newHtmlMatcher(input);
        if (m.lookingAt()) {
            getAppender().appendNode(buildNode(m.group()));
            return m.end();
        }
        return 0;
    }
    
    private HtmlInline buildNode(String html) {
        return getNodeFactory().createHtmlInline(html);
    }
}
