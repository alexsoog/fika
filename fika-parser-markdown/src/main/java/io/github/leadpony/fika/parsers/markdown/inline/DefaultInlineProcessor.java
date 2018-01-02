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

import static io.github.leadpony.fika.parsers.markdown.base.Characters.unescape;

import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.nodes.NodeFactory;
import io.github.leadpony.fika.core.nodes.Text;

/**
 * Default implementation of {@link InlineProcessor}.
 * 
 * @author leadpony
 */
public class DefaultInlineProcessor implements InlineProcessor {
    
    private static final int MAX_TRIGGER_CODE = 127;
    
    private final InlineHandler[] handlers;
    private final Context context;
    private final DelimiterRunProcessor delimiterProcessor;
    
    public DefaultInlineProcessor(NodeFactory nodeFactory) {
        this.handlers = new InlineHandler[MAX_TRIGGER_CODE + 1];
        this.context = createContext(nodeFactory);
        this.delimiterProcessor = new DelimiterRunProcessor();
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
        newHandler.bind(context);
    }
    
    @Override
    public void processInlines(Text text) {
        resetProcessor();
        parseText(text);
        processDelimiters();
        text.setContent(unescape(text.getContent()));
    }
    
    private void resetProcessor() {
    }
    
    private void parseText(Text text) {
        context.assign(text);
        final String input = text.getContent();
        final int length = input.length();
        int index = 0;
        while (index < length) {
            char c = input.charAt(index);
            if (c < MAX_TRIGGER_CODE) {
                InlineHandler handler = this.handlers[c];
                if (handler != null) {
                    int consumed = invokeHandler(handler, index);
                    if (consumed > 0) {
                        index += consumed;
                        continue;
                    }
                }
            }
            index++;
        }
        context.appendTailText();
    }
    
    private int invokeHandler(InlineHandler handler, int index) {
        context.index = index;
        final int currentCount = context.appendedNodeCount;
        int consumed = handler.handleContent(context.input, index);
        if (consumed > 0) {
            int newIndex = index + consumed;
            if (context.appendedNodeCount > currentCount) {
                context.textOffset = newIndex;
            }
            context.index = index;
        }
        return consumed;
    }
    
    private Context createContext(NodeFactory nodeFactory) {
        return new Context(nodeFactory);
    }
    
    private void processDelimiters() {
        LinkedStack<DelimiterRun> delimiterStack = context.delimiterStack;
        if (delimiterStack.size() > 0) {
            delimiterProcessor.processDelimiters(delimiterStack);
        }
    }
    
    private static class Context implements InlineHandler.Context {
        
        private final NodeFactory nodeFactory;
        private final LinkedStack<DelimiterRun> delimiterStack = new LinkedStack<>();

        private Node parentNode;
        private Node nextSibling;
        private Text firstText;
        private int appendedNodeCount;
        private int textOffset;
        private String input;
        private int index;
        
        Context(NodeFactory nodeFactory) {
            this.nodeFactory = nodeFactory;
        }
        
        Context assign(Text text) {
            this.parentNode = text.parentNode();
            this.nextSibling = text.nextNode();
            this.firstText = text;
            this.textOffset = 0;
            this.input = text.getContent();
            this.index = 0;
            this.delimiterStack.clear();
            return this;
        }
        
        void appendTailText() {
            if (textOffset == 0) {
                return;
            } else if (textOffset < input.length()) {
                appendText(input.length());
            }
        }
        
        /* InlineHandler.Context interface */
        
        @Override
        public NodeFactory nodeFactory() {
            return nodeFactory;
        }
        
        @Override
        public Context appendNode(Node newNode) {
            if (newNode == null) {
                throw new NullPointerException();
            }
            if (index == 0) {
                firstText.unlink();
            } else if (index > textOffset) {
                if (textOffset == 0) {
                    firstText.setContent(input.substring(0, index));
                } else {
                    appendText(index);
                }
            }
            appendOrInsertNode(newNode);
            return this;
        }
        
        @Override
        public Context appendDelimiterRun(DelimiterRun delimiterRun) {
            this.delimiterStack.add(delimiterRun);
            return this;
        }
        
        private void appendText(int endIndex) {
            Text text = nodeFactory().newText();
            text.setContent(input.substring(textOffset, endIndex));
            appendOrInsertNode(text);
        }
        
        private void appendOrInsertNode(Node child) {
            if (nextSibling == null) {
                parentNode.appendChild(child);
            } else {
                parentNode.insertChildBefore(child, nextSibling);
            }
            ++this.appendedNodeCount;
        }
    }
}
