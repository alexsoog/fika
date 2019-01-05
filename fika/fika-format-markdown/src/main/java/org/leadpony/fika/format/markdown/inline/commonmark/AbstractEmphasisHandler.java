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
package org.leadpony.fika.format.markdown.inline.commonmark;

import static org.leadpony.fika.format.markdown.common.Characters.isUnicodePunctuation;
import static org.leadpony.fika.format.markdown.common.Characters.isUnicodeWhitespace;

import org.leadpony.fika.format.markdown.inline.AbstractInlineHandler;
import org.leadpony.fika.format.markdown.inline.Delimiter;
import org.leadpony.fika.format.markdown.inline.DelimiterRun;
import org.leadpony.fika.parser.model.Node;
import org.leadpony.fika.parser.model.Text;

/**
 * @author leadpony
 */
abstract class AbstractEmphasisHandler extends AbstractInlineHandler {

    private static final int MAX_LENGTH_TO_PAIR = 2;

    private final char letter;

    protected AbstractEmphasisHandler(char letter) {
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

        Delimiter run = buildDelimiterRun(text, preceding, following);
        getAppender().appendNode(text);
        getDelimiterStack().add(run);
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
        String content = input.substring(start, start + length);
        return getNodeFactory().createText(content);
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
        } else if (isUnicodePunctuation(following)) {
            return (preceding < 0 || isUnicodeWhitespace(preceding) || isUnicodePunctuation(preceding));
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
        } else if (isUnicodePunctuation(preceding)) {
            return (following < 0 || isUnicodeWhitespace(following) || isUnicodePunctuation(following));
        } else {
            return true;
        }
    }

    protected abstract Delimiter buildDelimiterRun(Text text, int preceding, int following);

    protected Delimiter createDelimiterRun(Text text, boolean opener, boolean closer) {
        return new EmphasisDelimiterRun(text, opener, closer);
    }

    /**
     * Delimiter run produced by this handler.
     *
     * @author leadpony
     */
    class EmphasisDelimiterRun extends DelimiterRun {

        private final String delimiter;
        private int length;

        EmphasisDelimiterRun(Text text, boolean opener, boolean closer) {
            super(text, opener, closer);
            String content = text.textContent();
            this.delimiter = content.substring(0, 1);
            this.length = content.length();
        }

        @Override
        public String delimiter() {
            return delimiter;
        }

        @Override
        public int length() {
            return length;
        }

        @Override
        public int maxLengthToPair() {
            return MAX_LENGTH_TO_PAIR;
        }

        /**
         * {@inheritDoc}
         *
         * Note that a delimiter run that can be both opener and closer
         * cannot form pair if the sum of the lengths of the delimiter runs
         * containing the opening and closing delimiters is a multiple of 3.
         */
        @Override
        public boolean canBePairedWith(Delimiter closer) {
            if (!super.canBePairedWith(closer)) {
                return false;
            }
            if (canBeCloser() || closer.canBeOpener()) {
                int sumOfLengths = length() + ((DelimiterRun)closer).length();
                return sumOfLengths % 3 != 0;
            }
            return true;
        }

        @Override
        protected int removeDelimiters(int length) {
            String content = text().textContent();
            content = content.substring(0, content.length() - length);
            Text newText = getNodeFactory().createText(content);
            replaceText(newText);
            this.length = content.length();
            return this.length;
        }

        @Override
        protected Node buildWrapNode(int lengthPaired) {
            return getNodeFactory().createEmphasis(lengthPaired);
        }
    }
}
