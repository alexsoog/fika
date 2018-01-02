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

import static io.github.leadpony.fika.parsers.markdown.base.Characters.isPunctuation;
import static io.github.leadpony.fika.parsers.markdown.base.Characters.isUnicodeWhitespace;

import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.nodes.Text;
import io.github.leadpony.fika.parsers.markdown.inline.AbstractInlineHandler;
import io.github.leadpony.fika.parsers.markdown.inline.DelimiterRun;

/**
 * @author leadpony
 */
abstract class EmphasisHandler extends AbstractInlineHandler {
    
    private static final int MAX_LENGTH_TO_PAIR = 2;
   
    private final char letter;
    
    protected EmphasisHandler(char letter) {
        this.letter = letter;
    }

    @Override
    public char[] triggerLetters() {
        return new char[] { this.letter };
    }

    @Override
    public int handleContent(String input, int currentIndex) {
        final char delimiter = input.charAt(currentIndex);
        final int length = countRunLength(input, currentIndex, delimiter);
        Text text = buildTextNode(input, currentIndex, length); 

        final int preceding = extractPrecedingChar(input, currentIndex);
        final int following = extractFollowingChar(input, currentIndex, length);
        
        DelimiterRun run = buildDelimiterRun(text, preceding, following);
        appendNode(text);
        appendDelimiterRun(run);
        return length;
    }
    
    private int countRunLength(String input, int currentIndex, char delimiter) {
        int length = 1;
        for (int i = currentIndex + 1; i < input.length(); ++i) {
            if (input.charAt(i) == delimiter) {
                ++length;
            } else {
                break;
            }
        }
        return length;
    }
    
    private Text buildTextNode(String input, int start, int length) {
        Text newNode = nodeFactory().newText();
        newNode.setContent(input.substring(start, start + length));
        return newNode;
    }
    
    private static int extractPrecedingChar(String input, int index) {
        if (index > 0) {
            return input.charAt(index - 1);
        } else {
            return -1;
        }
    }
    
    private static int extractFollowingChar(String input, int index, int length) {
        if (index + length < input.length()) {
            return input.charAt(index + length);
        } else {
            return -1;
        }
    }

    /**
     * Checks if the delimiter run is left-flanking.
     * A left-flanking delimiter run is a delimiter run that is 
     * (a) not followed by Unicode whitespace.
     * AND
     * (b) not followed by punctuation character.
     *     OR
     *     preceded by Unicode whitespace or a punctuation character. 
     * 
     * @param preceding preceding character.
     * @param following following character.
     * @return true if the delimiter run is left-flanking, otherwise false.
     */
    protected static boolean calculateLeftFlanking(int preceding, int following) {
        if (following < 0 || isUnicodeWhitespace(following)) {
            return false;
        } else if (isPunctuation(following)) {
            return (preceding < 0 || isUnicodeWhitespace(preceding) || isPunctuation(preceding));
        } else {
            return true;
        }
    }

    /**
     * Checks if the delimiter run is right-flanking.
     * A right-flanking delimiter run is a delimiter run that is 
     * (a) not preceded by Unicode whitespace.
     * AND
     * (b) not preceded by punctuation character.
     *     OR
     *     followed by Unicode whitespace or a punctuation character. 
     * 
     * @param preceding preceding character.
     * @param following following character.
     * @return true if the delimiter run is left-flanking, otherwise false.
     */
    protected static boolean calculateRightFlanking(int preceding, int following) {
        if (preceding < 0 || isUnicodeWhitespace(preceding)) {
            return false;
        } else if (isPunctuation(preceding)) {
            return (following < 0 || isUnicodeWhitespace(following) || isPunctuation(following));
        } else {
            return true;
        }
    }
   
    protected abstract DelimiterRun buildDelimiterRun(Text text, int preceding, int following);
    
    protected DelimiterRun createDelimiterRun(Text text, boolean opener, boolean closer) {
        return new EmphasisDelimiterRun(text, opener, closer);
    }
    
    /**
     * Delimiter run produced by this handler.
     *  
     * @author leadpony
     */
    class EmphasisDelimiterRun extends DelimiterRun {
        
        public EmphasisDelimiterRun(Text text, boolean opener, boolean closer) {
            super(text, opener, closer);
        }
        
        @Override
        public int maxLengthToPair() {
            return MAX_LENGTH_TO_PAIR;
        }
        
        @Override
        protected Node buildWrapNode(int lengthPaired) {
            return nodeFactory().newEmphasis(lengthPaired);
        }
    }
}

/**
 * @author leadpony
 */
class AsteriskEmphasisHandler extends EmphasisHandler {

    AsteriskEmphasisHandler() {
        super('*');
    }
    
    @Override
    protected DelimiterRun buildDelimiterRun(Text text, int preceding, int following) {
        boolean opener = calculateLeftFlanking(preceding, following);
        boolean closer = calculateRightFlanking(preceding, following);
        return createDelimiterRun(text, opener, closer);
    }
}

/**
 * @author leadpony
 */
class UnderscoreEmphasisHandler extends EmphasisHandler {

    UnderscoreEmphasisHandler() {
        super('_');
    }

    @Override
    protected DelimiterRun buildDelimiterRun(Text text, int preceding, int following) {
        boolean leftFlanking = calculateLeftFlanking(preceding, following);
        boolean rightFlanking = calculateRightFlanking(preceding, following);
        boolean opener = leftFlanking && (!rightFlanking || isPunctuation(preceding));
        boolean closer = rightFlanking && (!leftFlanking || isPunctuation(following));
        return createDelimiterRun(text, opener, closer);
    }
}
