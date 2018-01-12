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
package io.github.leadpony.fika.parsers.markdown.common;

import static io.github.leadpony.fika.parsers.markdown.common.Characters.isPunctuation;
import static io.github.leadpony.fika.parsers.markdown.common.Characters.isWhitespace;
import static io.github.leadpony.fika.parsers.markdown.common.Strings.expandReferences;
import static io.github.leadpony.fika.parsers.markdown.common.Strings.trimWhitespace;
import static io.github.leadpony.fika.parsers.markdown.common.Strings.unescape;

import java.util.NoSuchElementException;

/**
 * @author leadpony
 */
public abstract class LinkParser {
    
    private final CharSequence input;
    private int index;
    
    public static LinkParser definitionParser(CharSequence input, int offset) {
        return new DefinitionParser(input, offset);
    }
    
    public static LinkParser inlineParser(CharSequence input, int offset) {
        return new InlineParser(input, offset);
    }

    protected LinkParser(CharSequence input, int offset) {
        this.input = input;
        this.index = offset;
    }
    
    public abstract LinkDefinition parse();
    
    public int index() {
        return index;
    }
    
    @Override
    public String toString() {
        return input.toString();
    }
    
    protected String destinationInBracket() {
        // opening angle bracket
        next();
        int begin = index();
        while (hasNext()) {
            char c = next();
            if (isWhitespace(c)) {
                break;
            } else if (c == '\\') {
                escape();
            } else if (c == '>') {
                // closing angle bracket
                return toString(begin, index() - 1);
            }
        }
        throw new SyntaxException();
    }

    protected String title() {
        final char opener = next();
        final char closer = (opener == '(') ? ')' : opener;
        final int begin = index();
        while (hasNext()) {
            char c = next();
            if (c == '\\') {
                escape();
            } else if (c == closer) {
                return toString(begin, index() - 1);
            }
        }
        throw new SyntaxException();
    }

    protected boolean hasNext() {
        return index < input.length();
    }
    
    protected char next() {
        if (index >= input.length()) {
            throw new NoSuchElementException();
        }
        return input.charAt(index++);
    }
    
    protected char peek() {
        if (index >= input.length()) {
            throw new NoSuchElementException();
        }
        return input.charAt(index);
    }
    
    protected void rewind(int count) {
        index -= count;
    }
    
    protected void reset(int index) {
        this.index = index;
    }
    
    protected void whitespace() {
        while (hasNext() && isWhitespace(peek())) {
            next();
        }
    }
    
    protected void indent() {
        int spaces = 0;
        while (hasNext() && spaces < 3) {
            char c = peek();
            if (c == '\u0020' || c == 't') {
                next();
                ++spaces;
            } else {
                break;
            }
        }
    }

    protected void escape() {
        if (hasNext() && isPunctuation(peek())) {
            next();
        }
    }
    
    protected String toString(int begin, int end) {
        return input.subSequence(begin, end).toString();
    }

    protected LinkDefinition createLinkDefinition(String label, String destination, String title) {
        return new LinkDefinition(
                label, 
                convertDestination(destination), 
                convertTitle(title)
                );
    }
    
    private static String convertDestination(String destination) {
        if (destination == null) {
            return "";
        }
        destination = unescape(expandReferences(destination));
        return new UrlEncoder().encode(destination);
    }
    
    private static String convertTitle(String title) {
        if (title == null) {
            return null;
        }
        return unescape(expandReferences(title));
    }

    @SuppressWarnings("serial")
    private static class SyntaxException extends RuntimeException {
    }
    
    private static class DefinitionParser extends LinkParser {

        DefinitionParser(CharSequence input, int offset) {
            super(input, offset);
        }

        @Override
        public LinkDefinition parse() {
            try {
                return linkDefinition();
            } catch (SyntaxException e) {
                return null;
            }
        }

        LinkDefinition linkDefinition() {
            indent();
            // opening bracket
            if (!hasNext() || peek() != '[') {
                return null;
            }
            String label = linkLabel();
            label = trimWhitespace(label);
            if (label.isEmpty()) {
                return null;
            }
            if (!hasNext() || next() != ':') {
                return null;
            }
            whitespace();
            if (!hasNext()) {
                return null;
            }
            String destination = (peek() == '<') ?
                    destinationInBracket() : destination();
            int lineStart = -1;
            for (; hasNext(); next()) {
                char c = peek();
                if (c == '\n') {
                    lineStart = index() + 1;
                } else if (!isWhitespace(c)) {
                    break;
                }
            }
            String title = null;
            if (hasNext()) {
                try {
                    char c = peek();
                    if (c == '\'' || c == '"' || c == '(') {
                        title = title();
                        whitespaceToLineEnd();
                    }
                } catch (SyntaxException e) {
                    if (lineStart >= 0) {
                        reset(lineStart);
                        title = null;
                    } else {
                        return null;
                    }
                }
            }
            return createLinkDefinition(label, destination, title);
        }

        String linkLabel() {
            next();
            final int begin = index();
            while (hasNext()) {
                char c = next();
                if (c == '\\') {
                    escape();
                } else if (c == ']') {
                    return toString(begin, index() - 1);
                }
            }
            throw new SyntaxException();
        }
        
        String destination() {
            final int begin = index();
            while (hasNext()) {
                char c = next();
                if (c == '\\') {
                    escape();
                } else if (isWhitespace(c)) {
                    rewind(1);
                    break;
                }
            }
            return toString(begin, index());
        }
        
        void whitespaceToLineEnd() {
            while (hasNext()) {
                char c = next();
                if (c == '\n') {
                    break;
                } else if (!isWhitespace(c)) {
                    throw new SyntaxException();
                }
            }
        }
    }

    private static class InlineParser extends LinkParser {

        InlineParser(CharSequence input, int offset) {
            super(input, offset);
        }

        @Override
        public LinkDefinition parse() {
            try {
                return inlineLink();
            } catch (SyntaxException e) {
                return null;
            }
        }

        LinkDefinition inlineLink() {
            if (!hasNext() || next() != '(') {
                return null;
            }
            whitespace();
            if (!hasNext()) {
                return null;
            }
            String destination = null;
            char c = peek();
            if (c != ')') {
                if (peek() == '<') {
                    destination = destinationInBracket();
                } else {
                    destination = destination();
                }
            }
            whitespace();
            if (!hasNext()) {
                return null;
            }
            String title = null;
            c = peek();
            if (c == '\'' || c == '"' || c == '(') {
                title = title();
                whitespace();
            }
            if (!hasNext() || next() != ')') {
                return null;
            }
            return createLinkDefinition(null, destination, title);
        }
 
        private String destination() {
            final int begin = index();
            int parentheses = 0;
            while (hasNext()) {
                char c = next();
                if (c == '\\') {
                    escape();
                } else if (c == '(') {
                    ++parentheses;
                } else if (c == ')') {
                    if (parentheses > 0) {
                        --parentheses;
                    } else {
                        rewind(1);
                        break;
                    }
                } else if (isWhitespace(c)) {
                    rewind(1);
                    break;
                }
            }
            return toString(begin, index());
        }
    }
}
