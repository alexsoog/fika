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

import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.nodes.Text;

/**
 * @author leadpony
 */
public abstract class DelimiterRun extends LinkedStack.Entry {
    
    private final Text text;
    private final char delimiter;
    private final boolean opener;
    private final boolean closer;
    private int length;
    
    public DelimiterRun(Text text, boolean opener, boolean closer) {
        this.text = text;
        this.delimiter = text.getContent().charAt(0);
        this.opener = opener;
        this.closer = closer;
        this.length = text.getContent().length();
    }
    
    public Text text() {
        return text;
    }

    public char delimiter() {
        return delimiter;
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
    
    public boolean isEmpty() {
        return length() == 0;
    }

    public boolean canBeOpener() {
        return opener;
    }
    
    public boolean canBeCloser() {
        return closer;
    }
    
    /**
     * Tests whether this delimiter run can be paired with specified delimiter run.
     * 
     * @param closer the delimiter run to be paired.
     * @return true if this delimiter run can be paired.
     */
    public boolean canBePairedWith(DelimiterRun closer) {
        if (this.delimiter != closer.delimiter) {
            return false;
        }
        return canBeOpener() && closer.canBeCloser();
    }
    
    public Node makePairWith(DelimiterRun closer) {
        int length = Math.min(length(), closer.length());
        int maxLength = Math.min(maxLengthToPair(), closer.maxLengthToPair());
        if (length > maxLength) {
            length = maxLength;
        }
        Node wrapper = buildWrapNode(length);
        wrapNodes(wrapper, text(), closer.text());
        // Inserts wrapper immediate after the opener.
        text().parentNode().insertChildAfter(wrapper, text());
        shorten(length);
        closer.shorten(length);
        return wrapper;
    }
    
    @Override
    public String toString() {
        return text.toString();
    }
    
    private static void wrapNodes(Node wrapper, Text opener, Text closer) {
        Node current = opener.nextNode();
        while (current != closer) {
            Node next = current.nextNode();
            wrapper.appendChild(current);
            current = next;
        }
    }
    
    private int shorten(int length) {
        String content = this.text.getContent();
        content = content.substring(0, content.length() - length);
        this.text.setContent(content);
        this.length = content.length();
        return this.length;
    }
    
    protected abstract Node buildWrapNode(int length); 
}
