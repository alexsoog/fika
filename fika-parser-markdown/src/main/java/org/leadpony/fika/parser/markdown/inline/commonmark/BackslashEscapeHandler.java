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
package org.leadpony.fika.parser.markdown.inline.commonmark;

import static org.leadpony.fika.parser.markdown.common.Characters.isPunctuation;

import org.leadpony.fika.parser.markdown.inline.AbstractInlineHandler;
import org.leadpony.fika.parser.markdown.inline.HandlerType;

/**
 * @author leadpony
 */
public class BackslashEscapeHandler extends AbstractInlineHandler {

    private static final char TRIGGER_LETTER = '\\';
    
    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }

    @Override
    public HandlerType handlerType() {
        return BasicHandlerType.BACKSLASH_ESCAPE;
    }
    
    @Override
    public int handleContent(String input, int currentIndex) {
        if (currentIndex + 1 < input.length()) {
            char c = input.charAt(currentIndex + 1);
            if (isPunctuation(c)) {
                getAppender().appendContent(c);
                return 2;
            }
        }
        return 0;
    }
}
