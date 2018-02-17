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
package org.leadpony.fika.parser.markdown.inline.handlers;

import static org.leadpony.fika.parser.markdown.common.Characters.sanitize;
import static org.leadpony.fika.parser.markdown.common.Strings.CHARACTER_REFERENCE_PATTERN;

import java.util.regex.Matcher;

import org.leadpony.fika.parser.markdown.common.EntityResolver;
import org.leadpony.fika.parser.markdown.common.InputSequence;
import org.leadpony.fika.parser.markdown.inline.AbstractInlineHandler;
import org.leadpony.fika.parser.markdown.inline.HandlerType;

/**
 * @author leadpony
 */
public class CharacterReferenceHandler extends AbstractInlineHandler {

    private static final char TRIGGER_LETTER = '&';
    
    private final EntityResolver resolver = EntityResolver.get();
    
    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }

    @Override
    public HandlerType handlerType() {
        return BasicHandlerType.CHARACTER_REFERENCE;
    }
    
    @Override
    public int handleContent(InputSequence input) {
        Matcher m = CHARACTER_REFERENCE_PATTERN.matcher(input);
        if (m.find()) {
            return resolveReference(m);
        } else {
            return 0;
        }
    }
    
    private int resolveReference(Matcher m) {
        final int end = m.end();
        if (m.group(3) != null) {
            // Hexadecimal numeric character
            int c = Integer.parseInt(m.group(3), 16);
            getAppender().appendContent(sanitize(c));
            return end;
        } else if (m.group(5) != null) {
            // Decimal numeric character
            int c = Integer.parseInt(m.group(5));
            getAppender().appendContent(sanitize(c));
            return end;
        } else {
            // Entity reference
            String resolved = resolver.resolve(m.group(1));
            if (resolved != null) {
                getAppender().appendContent(resolved);
                return end;
            }
        }
        return 0;
    }
}
