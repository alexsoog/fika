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
package io.github.leadpony.fika.parsers.markdown.inline;

import static io.github.leadpony.fika.parsers.markdown.common.Characters.isPunctuation;

import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.NodeFactory;
import io.github.leadpony.fika.core.model.Text;

/**
 * Default implementation of {@link InlineProcessor}.
 * 
 * @author leadpony
 */
public class DefaultInlineProcessor 
    implements InlineProcessor, InlineHandler.Context, InlineAppender {
    
    private static final int MAX_TRIGGER_CODE = 127;
    
    private final InlineHandler[] handlers;
    private final DelimiterProcessor delimiterProcessor = new DelimiterProcessor();
  
    private final NodeFactory nodeFactory;
    private final DelimiterStack delimiterStack = new DelimiterStack();

    private Text firstText;
    private Node parentNode;
    private Node nextSibling;
    
    private String input;
    private int currentIndex;
    
    private int appendedNodeCount;
    private StringBuilder textBuffer = null;
    
    public DefaultInlineProcessor(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.handlers = new InlineHandler[MAX_TRIGGER_CODE + 1];
    }
    
    /**
     * Installs an inline handler.
     * 
     * @param newHandler the inline handler to install.
     */
    @Override
    public void installHandler(InlineHandler newHandler) {
        for (char letter: newHandler.triggerLetters()) {
            if (letter >= MAX_TRIGGER_CODE) {
                throw new IllegalArgumentException();
            }
            InlineHandler existingHandler = handlers[letter];
            if (existingHandler != null) {
                handlers[letter] = existingHandler.or(newHandler);
            } else {
                handlers[letter] = newHandler;
            }
        }
        newHandler.bind(this);
    }
    
    @Override
    public void processInlines(Text text) {
        resetProcessor(text);
        parseInlines();
        processDelimiters();
    }
    
    /* InlinerHandler.Context interface */

    @Override
    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }
  
    @Override
    public InlineAppender getAppender() {
        return this;
    }
        
    @Override
    public DelimiterStack getDelimiterStack() {
        return delimiterStack;
    }
    
    @Override
    public DelimiterProcessor getDelimiterProcessor() {
        return delimiterProcessor;
    }
    
    /* InlineAppender interface */

    @Override
    public void appendNode(Node newNode) {
        if (newNode == null) {
            throw new NullPointerException();
        }
        flushTextBuffer();
        appendOrInsertNode(newNode);
    }
    
    @Override
    public final void appendContent(char c) {
        this.textBuffer.append(c);
    }
    
    @Override
    public final void appendContent(String s) {
        this.textBuffer.append(s);
    }

    @Override
    public final void appendContent(int length) {
        int beginIndex = currentIndex;
        int endIndex = beginIndex + length;
        String s = input.substring(beginIndex, endIndex);
        appendContent(s);
    }

    /* helper methods */
    
    private void resetProcessor(Text text) {
        this.firstText = text;
        this.parentNode = text.parentNode();
        this.nextSibling = text.nextNode();
        this.input = text.getContent();
        this.currentIndex = 0;
        this.appendedNodeCount = 0;
        this.textBuffer = new StringBuilder(text.getContent().length());
    }
    
    private void parseInlines() {
        final String input = this.input;
        final int length = input.length();
        int index = 0;
        while (index < length) {
            char c = input.charAt(index);
            int consumed = 0;
            if (c == '\\') {
                consumed = handleBackslashEscape(input, index);
            } else if (c < MAX_TRIGGER_CODE) {
                InlineHandler handler = this.handlers[c];
                if (handler != null) {
                    consumed = invokeHandler(handler, index);
                }
            }
            if (consumed > 0) {
                index += consumed;
            } else {
                appendContent(c);
                index++;
            }
        }
        flushTextBuffer();
    }
    
    private int invokeHandler(InlineHandler handler, int index) {
        this.currentIndex = index;
        int consumed = handler.handleContent(this.input, index);
        if (consumed > 0) {
            int newIndex = index + consumed;
            this.currentIndex = newIndex;
        }
        return consumed;
    }
    
    private int handleBackslashEscape(String input, int index) {
        this.currentIndex = index;
        if (index + 1 < input.length()) {
            char c = input.charAt(index + 1);
            if (c == '\n') {
                appendNode(getNodeFactory().newHardLineBreak());
                return 2;
            } else if (isPunctuation(c)) {
                appendContent(c);
                return 2;
            }
        }
        return 0;
    }
    
    private void appendOrInsertNode(Node child) {
        if (nextSibling == null) {
            parentNode.appendChild(child);
        } else {
            parentNode.insertChildBefore(child, nextSibling);
        }
        this.appendedNodeCount++;
    }
  
    private void flushTextBuffer() {
        String content = "";
        if (textBuffer.length() > 0) {
            content = textBuffer.toString();
            textBuffer.setLength(0);
        }
        if (appendedNodeCount == 0) {
            if (content.isEmpty()) {
                firstText.unlink();
            } else {
                firstText.setContent(content);
            }
        } else {
            Text text = getNodeFactory().newText();
            text.setContent(content);
            appendOrInsertNode(text);
        }
    }
    
    private void processDelimiters() {
        DelimiterStack delimiterStack = getDelimiterStack();
        if (delimiterStack.size() > 0) {
            this.delimiterProcessor.processDelimiters(delimiterStack, null);
        }
    }
}
