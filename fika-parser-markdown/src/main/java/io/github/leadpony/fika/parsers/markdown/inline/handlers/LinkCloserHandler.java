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

import static io.github.leadpony.fika.parsers.markdown.common.Strings.expandReferences;
import static io.github.leadpony.fika.parsers.markdown.common.Strings.unescape;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.Link;
import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;
import io.github.leadpony.fika.parsers.markdown.inline.AbstractInlineHandler;
import io.github.leadpony.fika.parsers.markdown.inline.Delimiter;
import io.github.leadpony.fika.parsers.markdown.inline.DelimiterStack;

/**
 * @author leadpony
 */
public class LinkCloserHandler extends AbstractInlineHandler {
  
    private static final char TRIGGER_LETTER = ']';
    
    private static final String INLINE_LINK_DESTINATION =
            "(<(\\[<>]|[^\u0020\n<>])*>|(\\\\[()]|[^\u0020\\p{Cntrl}])+)";
    private static final String INLINE_LINK_TITLE =
            "(" +
            "(\"(\\\\\"|[^\"])*\")|" +
            "('(\\\\'|[^'])*')|" +
            "(\\((\\\\\\)|[^\\)])*\\))" +
            ")";
    private static final String INLINE_LINK =
            "\\(\\s*" +
            INLINE_LINK_DESTINATION + "?" +
           "(\\s+" + INLINE_LINK_TITLE + ")?" +        
            "\\s*\\)";
    private static final Pattern INLINE_LINK_PATTERN = Pattern.compile(INLINE_LINK);
   
    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }
    
    @Override
    public int handleContent(InputSequence input) {
        Text text = buildNode(LinkHandler.CLOSING_CONTENT);
        getAppender().appendNode(text);
        Delimiter closer = new ClosingDelimiter(text);
        Delimiter opener = findOpener(closer);
        if (opener != null) {
            if (opener.isActive()) {
                int consumed = parseLink(input, opener, closer);
                if (consumed > 0) {
                    return 1 + consumed;
                }
            }
            getDelimiterStack().remove(opener);
        }
        return 1;
    }
    
    private Text buildNode(String content) {
        Text text = getNodeFactory().newText();
        text.setContent(content);
        return text;
    }
    
    private int parseLink(InputSequence input, Delimiter opener, Delimiter closer) {
        int consumed = parseInlineLink(input, opener, closer);
        return consumed;
    }
    
    private int parseInlineLink(InputSequence input, Delimiter opener, Delimiter closer) {
        Matcher m = matchInput(input, INLINE_LINK_PATTERN);
        if (!m.lookingAt()) {
            return 0;
        }
        String destination = normalizeDestination(m.group(1));
        String title = normalizeTitle(m.group(5));
        makeLink(opener, closer, destination, title);
        return m.group().length();
    }
    
    private static Matcher matchInput(InputSequence input, Pattern pattern) {
        Matcher m = INLINE_LINK_PATTERN.matcher(input);
        m.region(1, input.length());
        return m;
    }
    
    private Delimiter findOpener(Delimiter closer) {
        DelimiterStack stack = getDelimiterStack();
        Iterator<Delimiter> it = stack.descendingIterator();
        while (it.hasNext()) {
            Delimiter current = it.next();
            if (current.canBeOpener() && current.canBePairedWith(closer)) {
                return current;
            }
        }
        return null;
    }
    
    private Node makeLink(Delimiter opener, Delimiter closer, String destination, String title) {
        Link link = (Link)opener.makePairWith(closer);
        if (destination != null) {
            link.setDestination(destination);
        }
        link.setTitle(title);
        processDelimitersInLinkTitle(opener);
        deactiveOpenersBefore(opener);
        getDelimiterStack().remove(opener);
        opener.text().unlink();
        closer.text().unlink();
        return link;
    }
    
    private void deactiveOpenersBefore(Delimiter opener) {
        DelimiterStack stack = getDelimiterStack();
        Iterator<Delimiter> it = stack.descendingIterator(opener);
        while (it.hasNext()) {
            Delimiter current = it.next();
            if (current.canBeOpener() && current.isSameTypeAs(opener)) {
                current.deactive();
            }
        }
    }
    
    private void processDelimitersInLinkTitle(Delimiter opener) {
        context().getDelimiterProcessor().processDelimiters(opener);
    }
    
    private static String normalizeDestination(String s) {
        if (s == null) {
            return "";
        }
        if (s.length() >= 2) {
            if (s.charAt(0) == '<' && s.charAt(s.length() - 1) == '>') {
                s = s.substring(1, s.length() - 1);
            }
        }
        s = expandReferences(s);
        return unescape(s);
    }
    
    private static String normalizeTitle(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() >= 2) {
            char first = s.charAt(0);
            char last = s.charAt(s.length() - 1);
            if ((first == '"' && last == '"') ||
                (first == '\'' && last == '\'') ||
                (first == '(' && last == ')')) {
                s = s.substring(1, s.length() - 1);
            }
        }
        s = expandReferences(s);
        return unescape(s);
    }
    
    private static class ClosingDelimiter extends Delimiter {

        protected ClosingDelimiter(Text text) {
            super(text);
        }

        @Override
        public String delimiter() {
            return text().getContent();
        }

        @Override
        public boolean canBeOpener() {
            return false;
        }

        @Override
        public boolean canBeCloser() {
            return true;
        }

        @Override
        public boolean isSameTypeAs(Delimiter closer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Node makePairWith(Delimiter closer) {
            throw new UnsupportedOperationException();
        }
    }
}
