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

import org.leadpony.fika.parser.model.Node;
import org.leadpony.fika.parser.model.Text;

/**
 * @author leadpony
 */
public abstract class DelimiterRun extends Delimiter {

    private final boolean opener;
    private final boolean closer;
    private int length;

    /**
     * Constructs this delimiter run.
     *
     * @param text
     * @param opener
     * @param closer
     */
    protected DelimiterRun(Text text, boolean opener, boolean closer) {
        super(text);
        this.opener = opener;
        this.closer = closer;
        this.length = text.textContent().length();
    }

    @Override
    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public boolean canBeOpener() {
        return opener;
    }

    @Override
    public boolean canBeCloser() {
        return closer;
    }

    @Override
    public boolean isSameTypeAs(Delimiter other) {
        return delimiter().equals(other.delimiter());
    }

    @Override
    public Node makePairWith(Delimiter closer, Object... params) {
        return makePairWith((DelimiterRun)closer);
    }

    /**
     * Returns the run length of this delimiter run.
     *
     * @return the run length.
     */
    public int length() {
        return length;
    }

    /**
     * Returns the maximum length to be paired.
     *
     * @return the maximum length to be paired.
     */
    public int maxLengthToPair() {
        return length();
    }

    private Node makePairWith(DelimiterRun closer) {
        int length = Math.min(length(), closer.length());
        int maxLength = Math.min(maxLengthToPair(), closer.maxLengthToPair());
        if (length > maxLength) {
            length = maxLength;
        }
        Node wrapper = buildWrapNode(length);
        wrapNodes(wrapper, text(), closer.text());
        // Inserts wrapper immediate after the opener.
        text().getParentNode().insertChildAfter(wrapper, text());
        removeDelimiters(length);
        closer.removeDelimiters(length);
        return wrapper;
    }

    /**
     * Shortens the delimiters.
     *
     * @param length the number of characters to remove from the last.
     * @return new length after this operation.
     */
    protected abstract int removeDelimiters(int length);

    protected abstract Node buildWrapNode(int length);
}
