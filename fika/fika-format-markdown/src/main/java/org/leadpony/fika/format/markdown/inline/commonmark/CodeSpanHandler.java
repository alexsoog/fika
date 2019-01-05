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

import java.util.regex.Pattern;

import org.leadpony.fika.format.markdown.common.InputSequence;
import org.leadpony.fika.format.markdown.inline.AbstractInlineHandler;
import org.leadpony.fika.format.markdown.inline.HandlerType;
import org.leadpony.fika.core.model.CodeSpan;

/**
 * @author leadpony
 */
public class CodeSpanHandler extends AbstractInlineHandler {

    private static final char BACKTICK = '`';
    private static final Pattern LEADING_SPACE_OR_LINE_ENDING = 
            Pattern.compile("^[\\u0020\\r\\n]+");
    private static final Pattern TRAILING_SPACE_OR_LINE_ENDING = 
            Pattern.compile("[\\u0020\\r\\n]+$");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    
    @Override
    public char[] triggerLetters() {
        return new char[] { BACKTICK };
    }

    @Override
    public HandlerType handlerType() {
        return BasicHandlerType.CODE_SPAN;
    }
    
    @Override
    public int handleContent(InputSequence input) {
        final int backticks = input.countLeading(BACKTICK);
        final int start = backticks;
        int i = start;
        for (;;) {
            i = input.indexOf(BACKTICK, i);
            if (i < 0) {
                break;
            }
            int found = input.countLeading(BACKTICK, i);
            if (found == backticks) {
                getAppender().appendNode(buildNode(input, start, i));
                return i + backticks;
            }
            i += found;
        }
        getAppender().appendContentTo(backticks);
        return backticks;
    }
    
    private CodeSpan buildNode(InputSequence input, int start, int end) {
        String code = normalizeCode(input.substring(start, end));
        return getNodeFactory().createCodeSpan(code);
    }
    
    private static String normalizeCode(String string) {
        string = LEADING_SPACE_OR_LINE_ENDING.matcher(string).replaceAll("");
        string = TRAILING_SPACE_OR_LINE_ENDING.matcher(string).replaceAll("");
        return WHITESPACE.matcher(string).replaceAll("\u0020");
    }
}
