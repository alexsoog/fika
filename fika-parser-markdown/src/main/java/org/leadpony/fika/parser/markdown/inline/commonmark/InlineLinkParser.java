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

import static org.leadpony.fika.parser.markdown.common.Characters.isPunctuation;
import static org.leadpony.fika.parser.markdown.common.Characters.isWhitespace;

import java.util.NoSuchElementException;

import org.leadpony.fika.parser.markdown.common.LinkDefinition;

/**
 * Parser for inline link.
 * 
 * @author leadpony
 */
class InlineLinkParser {
    
    @SuppressWarnings("serial")
    private static class SyntaxException extends RuntimeException {
    }
    
    private final CharSequence input;
    private int index;
    
    InlineLinkParser(CharSequence input, int offset) {
        this.input = input;
        this.index = offset;
    }
    
    /**
     * Parses the input and returns link reference definition.
     * 
     * @return the found link reference definition, {@code null} otherwise.
     */
    LinkDefinition parse() {
        try {
            return inlineLink();
        } catch (SyntaxException e) {
            return null;
        }
    }

    /**
     * Return the current index.
     * 
     * @return the current index.
     */
    int index() {
        return index;
    }
    
    private LinkDefinition inlineLink() {
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
        return new LinkDefinition(destination, title);
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

    private String destinationInBracket() {
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

    private String title() {
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

    private boolean hasNext() {
        return index < input.length();
    }
    
    private char next() {
        if (index >= input.length()) {
            throw new NoSuchElementException();
        }
        return input.charAt(index++);
    }
    
    private char peek() {
        if (index >= input.length()) {
            throw new NoSuchElementException();
        }
        return input.charAt(index);
    }
    
    private void rewind(int count) {
        index -= count;
    }
    
    private void whitespace() {
        while (hasNext() && isWhitespace(peek())) {
            next();
        }
    }
    
    private void escape() {
        if (hasNext() && isPunctuation(peek())) {
            next();
        }
    }
    
    private String toString(int begin, int end) {
        return input.subSequence(begin, end).toString();
    }
}
