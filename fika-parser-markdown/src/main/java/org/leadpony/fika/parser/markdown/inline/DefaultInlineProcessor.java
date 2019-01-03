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
package org.leadpony.fika.parser.markdown.inline;

import java.util.List;

import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.NodeFactory;
import org.leadpony.fika.core.model.Text;
import org.leadpony.fika.parser.markdown.common.LinkDefinitionMap;

/**
 * Default implementation of {@link InlineProcessor}.
 * 
 * @author leadpony
 */
public class DefaultInlineProcessor 
    implements InlineProcessor, InlineHandler.Context, InlineAppender {
    
    private final InlineHandler[] handlers;
  
    private final NodeFactory nodeFactory;
    private final LinkDefinitionMap linkDefinitionMap;
    private final DelimiterStack delimiterStack = new DelimiterStack();
    private final DelimiterProcessor delimiterProcessor = new DelimiterProcessor(delimiterStack);

    private Text firstText;
    private Node parentNode;
    private Node nextSibling;
    
    private String input;
    private int currentIndex;
    
    private int appendedNodeCount;
    private StringBuilder textBuffer = null;
    
    public DefaultInlineProcessor(
            NodeFactory nodeFactory, 
            LinkDefinitionMap linkDefinitionMap,
            List<InlineHandler> handlers) {
        this.nodeFactory = nodeFactory;
        this.linkDefinitionMap = linkDefinitionMap; 
        this.handlers = new InlineHandler[MAX_TRIGGER_CODE + 1];
        installHandlers(handlers);
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
    public String input() {
        return input;
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
   
    @Override
    public LinkDefinitionMap getLinkDefinitionMap() {
        return linkDefinitionMap;
    }
    
    /* InlineAppender interface */

    @Override
    public InlineAppender appendNode(Node newNode) {
        if (newNode == null) {
            throw new NullPointerException();
        }
        flushTextBuffer();
        appendOrInsertNode(newNode);
        return this;
    }
    
    @Override
    public final InlineAppender appendContent(char c) {
        this.textBuffer.append(c);
        return this;
    }
   
    @Override
    public InlineAppender appendContent(int codePoint) {
        this.textBuffer.appendCodePoint(codePoint);
        return this;
    }
    
    @Override
    public InlineAppender appendContent(String s) {
        this.textBuffer.append(s);
        return this;
    }

    @Override
    public InlineAppender appendContentTo(int length) {
        int beginIndex = currentIndex;
        int endIndex = beginIndex + length;
        String s = input.substring(beginIndex, endIndex);
        return appendContent(s);
    }
    
    @Override
    public InlineAppender removeContent(int length) {
        int newLength = this.textBuffer.length() - length;
        this.textBuffer.setLength(newLength);
        return this;
    }

    /* helper methods */
    
    private void installHandlers(List<InlineHandler> handlers) {
        for (InlineHandler handler: handlers) {
            installHandler(handler);
        }
    }
    
    /**
     * Installs an inline handler.
     * 
     * @param newHandler the inline handler to install.
     */
    private void installHandler(InlineHandler newHandler) {
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
            if (c < MAX_TRIGGER_CODE) {
                InlineHandler handler = this.handlers[c];
                if (handler != null) {
                    consumed = invokeHandler(handler, index);
                }
            }
            if (consumed > 0) {
                index += consumed;
            } else {
                appendContent(c);
                ++index;
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
            Text text = getNodeFactory().newText(content);
            appendOrInsertNode(text);
        }
    }
    
    private void processDelimiters() {
        DelimiterStack delimiterStack = getDelimiterStack();
        if (delimiterStack.size() > 0) {
            this.delimiterProcessor.processDelimiters(null);
        }
    }
}
