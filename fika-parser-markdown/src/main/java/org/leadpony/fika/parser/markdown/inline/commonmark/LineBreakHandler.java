/*
 * Copyright 2017-2018 the Fika authors.
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
package org.leadpony.fika.parser.markdown.inline.commonmark;

import org.leadpony.fika.parser.markdown.inline.AbstractInlineHandler;
import org.leadpony.fika.parser.markdown.inline.HandlerType;
import org.leadpony.fika.parser.markdown.inline.InlineAppender;

/**
 * @author leadpony
 */
public class LineBreakHandler extends AbstractInlineHandler {

    private static final char TRIGGER_LETTER = '\n';
    
    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }

    @Override
    public HandlerType handlerType() {
        return BasicHandlerType.HARD_LINE_BREAK;
    }
    
    @Override
    public int handleContent(String input, int currentIndex) {
        InlineAppender appender = getAppender();
        int precedingSpaces = countPrecedingSpaces(input, currentIndex);
        int followingSpaces = countFollowingSpaces(input, currentIndex);
        if (precedingSpaces > 0) {
            appender.removeContent(precedingSpaces);
            if (precedingSpaces >= 2 && currentIndex < input.length() - 1) {
                appender.appendNode(getNodeFactory().newHardLineBreak());
                return 1 + followingSpaces;
            }
        }
        if (followingSpaces > 0) {
            appender.appendContent('\n');
            return 1 + followingSpaces;
        }
        return 0;
    }
    
    private static int countPrecedingSpaces(String input, int currentIndex) {
        int spaces = 0;
        while (--currentIndex >= 0) {
            if (input.charAt(currentIndex) == '\u0020') {
                ++spaces;
            } else {
                break;
            }
        }
        return spaces;
    }

    private static int countFollowingSpaces(String input, int currentIndex) {
        int spaces = 0;
        while (++currentIndex < input.length()) {
            if (input.charAt(currentIndex) == '\u0020') {
                ++spaces;
            } else {
                break;
            }
        }
        return spaces;
    }
}
