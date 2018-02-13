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
package io.github.leadpony.fika.parser.markdown.block.matchers;

import static io.github.leadpony.fika.parser.markdown.common.Characters.isPunctuation;
import static io.github.leadpony.fika.parser.markdown.common.Characters.isWhitespace;
import static io.github.leadpony.fika.parser.markdown.common.Strings.trimWhitespace;

import java.util.function.Consumer;

import io.github.leadpony.fika.parser.markdown.common.LinkDefinition;

/**
 * Link definition recognizer or acceptor.
 * 
 * @author leadpony
 */
class LinkDefinitionRecognizer {
    
    /**
     * A state of state machine.
     * 
     * @author leadpony
     */
    @FunctionalInterface
    private static interface State {
        State accept(char c);
    }

    private static final State COMPLETED = new FinalState();
    private static final State IDLE = new FinalState();

    private final Consumer<LinkDefinition> consumer;

    private State INITIAL;
    private State LABEL;
    private State COMMA;
    private State WHITESPACE_AFTER_COMMA;
    private State DESTINATION;
    private State BRACKETED_DESTINATION;
    private State WHITESPACE_AFTER_DESTINATION;
    private State SINGLE_QUOTED_TITLE = new TitleState('\'');
    private State DOUBLE_QUOTED_TITLE = new TitleState('"');
    private State PARENTHESIZED_TITLE = new TitleState(')');
    private State WHITESPACE_AFTER_TITLE;
  
    private State currentState;
    private int lineNo;
    
    private final StringBuilder tokenBuilder = new StringBuilder();
    
    private String label;
    private String destination;
    private String title;
    
    private int destinationLineNo;
    private boolean readyToDeliver;
    private int linesConsumed;
    
    LinkDefinitionRecognizer(Consumer<LinkDefinition> consumer) {
        this.consumer = consumer;
        this.lineNo = 0;
        this.linesConsumed = 0;
        this.destinationLineNo = -1;
        this.readyToDeliver = false;
        
        INITIAL = (c)->{
            if (c == '[') {
                return LABEL;
            }
            return INITIAL;
        };
        
        LABEL = (c)-> {
            if (c == '\\') {
                return (d)->{
                    tokenBuilder.append('\\');
                    if (isPunctuation(d)) {
                        tokenBuilder.append(d);
                    }
                    return LABEL;
                };
            } else if (c == '[') {
                return IDLE;
            } else if (c == ']') {
                return buildLabel().isEmpty() ? IDLE : COMMA;
            }
            tokenBuilder.append(c);
            return LABEL;
        };
       
        COMMA = (c)->{
            if (c == ':') {
                return WHITESPACE_AFTER_COMMA;
            } else {
                return IDLE;
            }
        };
       
        WHITESPACE_AFTER_COMMA = (c)->{
            if (isWhitespace(c)) {
                return WHITESPACE_AFTER_COMMA;
            } else if (c == '<') {
                return BRACKETED_DESTINATION;
            } else {
                tokenBuilder.append(c);
                return DESTINATION;
            }
        };
        
        DESTINATION = (c)->{
            if (c == '\\') {
                return (d)->{
                    appendEscaped(d);
                    return DESTINATION;
                };
            } else if (isWhitespace(c)) {
                buildDestination();
                if (c == '\n') {
                    readyToDeliver = true;
                }
                return WHITESPACE_AFTER_DESTINATION;
            }
            tokenBuilder.append(c);
            return DESTINATION;
        };
        
        BRACKETED_DESTINATION = (c)->{
            if (c == '\\') {
                return (d)->{
                    appendEscaped(d);
                    return DESTINATION;
                };
            } else if (c == '>') {
                buildDestination();
                return WHITESPACE_AFTER_DESTINATION;
            } else if (isWhitespace(c)) {
                return IDLE;
            } else {
                tokenBuilder.append(c);
                return BRACKETED_DESTINATION;
            }
        };
        
        WHITESPACE_AFTER_DESTINATION = (c)->{
            if (c == '\n') {
                readyToDeliver = true;
                return WHITESPACE_AFTER_DESTINATION;
            } else if (isWhitespace(c)) {
                return WHITESPACE_AFTER_DESTINATION;
            } else if (c == '[') {
                if (isReadyToDeliver()) {
                    deliver();
                    return LABEL;
                } else {
                    return IDLE;
                }
            } else if (c == '\'') {
                return SINGLE_QUOTED_TITLE;
            } else if (c == '"') {
                return DOUBLE_QUOTED_TITLE;
            } else if (c == '(') {
                return PARENTHESIZED_TITLE;
            } else {
                deliverIfReady();
                return IDLE;
            }
        };
        
        WHITESPACE_AFTER_TITLE = (c)->{
            if (c == '\n') {
                deliver();
                return COMPLETED;
            } else if (isWhitespace(c)) {
                return WHITESPACE_AFTER_TITLE;
            } else {
                this.title = null;
                deliverIfReady();
                return IDLE;
            }
        };

        this.currentState = INITIAL;
    }
    
    /**
     * Accepts a new line.
     * 
     * @param input a new line.
     * @return {@code true} if current link definition is completed.
     */
    boolean acceptLine(CharSequence input) {
        this.lineNo++;
        if (currentState == IDLE) {
            return false;
        }
        for (int i = 0; i < input.length(); ++i) {
            currentState = currentState.accept(input.charAt(i));
            if (currentState == IDLE) {
                return false;
            }
        }
        currentState = currentState.accept('\n');
        return currentState == COMPLETED;
    }
    
    /**
     * Flushes the link definition if it is ready.
     * 
     * @return the total number of lines consumed.
     */
    int flush() {
        deliverIfReady();
        return linesConsumed;
    }
    
    private boolean isReadyToDeliver() {
        return readyToDeliver;
    }
    
    private void deliverIfReady() {
        if (isReadyToDeliver()) {
            deliver();
        }
    }
    
    private void deliver() {
        LinkDefinition definition = new LinkDefinition(label, destination, title);
        consumer.accept(definition);
        if (this.title != null) {
            linesConsumed = lineNo;
        } else {
            linesConsumed = destinationLineNo;
        }
        reset();
    }
    
    private void reset() {
        this.destinationLineNo = -1;
        this.readyToDeliver = false;
    }

    private String buildToken() {
        String token = tokenBuilder.toString();
        tokenBuilder.setLength(0);
        return token;
    }
    
    private String buildLabel() {
        String token = buildToken();
        label = trimWhitespace(token);
        return label;
    }

    private String buildDestination() {
        destination = buildToken();
        destinationLineNo = lineNo;
        return destination;
    }
    
    private void appendEscaped(char c) {
        if (isPunctuation(c)) {
            tokenBuilder.append(c);
        } else {
            tokenBuilder.append('\\').append(c);
        }
    }
   
    private static class FinalState implements State {
        @Override
        public State accept(char c) {
            return this;
        }
    }
    
    private class TitleState implements State {
        
        private final char closer;
        
        TitleState(char closer) {
            this.closer = closer;
        }

        @Override
        public State accept(char c) {
            if (c == '\\') {
                return (d)->{
                    appendEscaped(d);
                    return this;
                };
            } else if (c == this.closer) {
                title = buildToken();
                return WHITESPACE_AFTER_TITLE;
            }
            tokenBuilder.append(c);
            return this;
        }
    }
}
