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

import static io.github.leadpony.fika.parsers.markdown.common.Strings.trimWhitespace;

import java.util.Iterator;

import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;
import io.github.leadpony.fika.parsers.markdown.common.LinkDefinition;
import io.github.leadpony.fika.parsers.markdown.inline.AbstractInlineHandler;
import io.github.leadpony.fika.parsers.markdown.inline.Delimiter;
import io.github.leadpony.fika.parsers.markdown.inline.DelimiterStack;
import io.github.leadpony.fika.parsers.markdown.inline.handlers.ImageHandler.ImageDelimiterRun;
import io.github.leadpony.fika.parsers.markdown.inline.handlers.LinkHandler.LinkDelimiterRun;

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
        Delimiter closer = new ClosingDelimiter(text, currentIndex);
        Delimiter opener = findOpener(closer);
        if (opener != null) {
            if (opener.isActive()) {
                InputSequence offsetted = InputSequence.withOffset(input, currentIndex + 1);
                int consumed = parseLink(offsetted, opener, closer);
                if (consumed >= 0) {
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
        if (input.length() > 0) {
            char first = input.charAt(0);
            if (first == '(') {
                return parseInlineLink(input, opener, closer);
            } else if (first == '[' && input.length() > 1) {
                if (input.charAt(1) == ']') {
                    return parseCollapsedReferenceLink(opener, closer);
                } else {
                    return parseFullReferenceLink(input, opener, closer);
                }
            }
        }
        return parseShortcutReferenceLink(opener, closer);
    }
    
    private int parseInlineLink(InputSequence input, Delimiter opener, Delimiter closer) {
        InlineLinkParser parser = new InlineLinkParser(input, 0);
        LinkDefinition definition = parser.parse();
        if (definition == null) {
            return parseShortcutReferenceLink(opener, closer);
        }
        makeLink(opener, closer, definition);
        return parser.index();
    }
    
    private int parseFullReferenceLink(InputSequence input, Delimiter opener, Delimiter closer) {
        int i = 1;
        for (; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (c == ']') {
                break;
            } else if (c == '\\') {
                ++i;
            }
        }
        int end = i;
        String label = trimWhitespace(input.substring(1, end));
        if (label.isEmpty()) {
            return -1;
        }
        LinkDefinition definition = findDefinition(label);
        if (definition != null) {
            makeLink(opener, closer, definition);
            return end + 1;
        } else {
            return -1;
        }
    }
    
    private int parseCollapsedReferenceLink(Delimiter opener, Delimiter closer) {
        String label = extractLinkLabel(opener, closer);
        LinkDefinition definition = findDefinition(label);
        if (definition != null) {
            makeLink(opener, closer, definition);
            return 2;
        } else {
            return -1;
        }
    }
    
    private int parseShortcutReferenceLink(Delimiter opener, Delimiter closer) {
        String label = extractLinkLabel(opener, closer);
        LinkDefinition definition = findDefinition(label);
        if (definition != null) {
            makeLink(opener, closer, definition);
            return 0;
        } else {
            return -1;
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
    
    private String extractLinkLabel(Delimiter opener, Delimiter closer) {
        LinkDelimiterRun linkOpener = (LinkDelimiterRun)opener;
        int openerLength = linkOpener.delimiter().length();
        int beginIndex = openerLength + linkOpener.getPosition();
        int endIndex = ((ClosingDelimiter)closer).position;
        return context().input().substring(beginIndex, endIndex);
    }
    
    private LinkDefinition findDefinition(String label) {
        return context().getLinkDefinitionMap().get(label);
    }
    
    private Node makeLink(Delimiter opener, Delimiter closer, LinkDefinition definition) {
        Node newNode = opener.makePairWith(closer, definition);
        processDelimitersInText(opener);
        if (!(opener instanceof ImageDelimiterRun)) {
            deactiveOpenersBefore(opener);
        }
        getDelimiterStack().remove(opener);
        opener.text().unlink();
        closer.text().unlink();
        return newNode;
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
    
    private void processDelimitersInText(Delimiter opener) {
        context().getDelimiterProcessor().processDelimiters(opener);
    }
    
    private static class ClosingDelimiter extends Delimiter {
        
        private final int position;

        protected ClosingDelimiter(Text text, int position) {
            super(text);
            this.position = position;
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
        public Node makePairWith(Delimiter closer, Object... params) {
            throw new UnsupportedOperationException();
        }
    }
}
