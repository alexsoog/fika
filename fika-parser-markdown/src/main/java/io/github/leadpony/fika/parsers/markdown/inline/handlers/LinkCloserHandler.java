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

import java.util.Iterator;

import io.github.leadpony.fika.core.model.Link;
import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.common.LinkDefinition;
import io.github.leadpony.fika.parsers.markdown.common.LinkDefinitionParser;
import io.github.leadpony.fika.parsers.markdown.inline.AbstractInlineHandler;
import io.github.leadpony.fika.parsers.markdown.inline.Delimiter;
import io.github.leadpony.fika.parsers.markdown.inline.DelimiterStack;

/**
 * @author leadpony
 */
public class LinkCloserHandler extends AbstractInlineHandler {
  
    private static final char TRIGGER_LETTER = ']';
   
    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }
    
    @Override
    public int handleContent(String input, int currentIndex) {
        Text text = buildNode(LinkHandler.CLOSING_CONTENT);
        getAppender().appendNode(text);
        Delimiter closer = new ClosingDelimiter(text);
        Delimiter opener = findOpener(closer);
        if (opener != null) {
            if (opener.isActive()) {
                int consumed = parseLink(input, currentIndex, opener, closer);
                if (consumed > 0) {
                    return consumed;
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
    
    private int parseLink(String input, int currentIndex, Delimiter opener, Delimiter closer) {
        final int nextIndex = currentIndex + 1;
        if (input.length() > nextIndex) {
            char first = input.charAt(nextIndex);
            if (first == '(') {
                return parseInlineLink(input, currentIndex, opener, closer);
            }
        }
        return parseShortcutLink(opener, closer, currentIndex);
    }
    
    private int parseInlineLink(String input, int currentIndex, Delimiter opener, Delimiter closer) {
        LinkDefinitionParser parser = new LinkDefinitionParser();
        LinkDefinition definition = parser.parseInlineLink(input, currentIndex + 1);
        if (definition == null) {
            return 0;
        }
        makeLink(opener, closer, definition);
        return 1 + parser.length();
    }
    
    private int parseShortcutLink(Delimiter opener, Delimiter closer, int endIndex) {
        String linkLabel = extractLinkLabel(opener, closer, endIndex);
        LinkDefinition definition = context().getLinkDefinitionMap().get(linkLabel);
        if (definition != null) {
            makeLink(opener, closer, definition);
            return 1;
        } else {
            return 0;
        }
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
    
    private String extractLinkLabel(Delimiter opener, Delimiter closer, int endIndex) {
        int beginIndex = 1 + ((LinkHandler.LinkDelimiterRun)opener).getPosition();
        return context().input().substring(beginIndex, endIndex);
    }
    
    private Node makeLink(Delimiter opener, Delimiter closer, LinkDefinition definition) {
        Link link = (Link)opener.makePairWith(closer);
        link.setDestination(definition.destination());
        link.setTitle(definition.title());
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
