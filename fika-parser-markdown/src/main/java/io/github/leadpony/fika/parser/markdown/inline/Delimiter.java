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
package io.github.leadpony.fika.parser.markdown.inline;

import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.Text;

/**
 * @author leadpony
 */
public abstract class Delimiter extends DelimiterStack.Entry {
    
    private final Text text;
    private boolean active;
    
    protected Delimiter(Text text) {
        this.text = text;
        this.active = true;
    }
    
    public Text text() {
        return text;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void deactive() {
        this.active = false;
    }

    public boolean isEmpty() {
        return false;
    }

    /**
     * Returns the delimiter characters composing of this delimiter run.
     * 
     * @return the delimiter characters.
     */
    public abstract String delimiter();

    public abstract boolean canBeOpener();
    
    public abstract boolean canBeCloser();
    
    public abstract boolean isSameTypeAs(Delimiter other);
    
    /**
     * Checks if this delimiter run can be paired with specified delimiter run.
     * 
     * @param closer the delimiter run to be paired.
     * @return true if this delimiter run can be paired.
     */
    public boolean canBePairedWith(Delimiter closer) {
        return (isSameTypeAs(closer) &&
                canBeOpener() &&
                closer.canBeCloser()
                );
    }
    
    public abstract Node makePairWith(Delimiter closer, Object... params);
    
    @Override
    public String toString() {
        return text.toString();
    }
    
    protected static void wrapNodes(Node wrapper, Text opener, Text closer) {
        Node current = opener.nextNode();
        while (current != closer) {
            Node next = current.nextNode();
            wrapper.appendChild(current);
            current = next;
        }
    }
}
