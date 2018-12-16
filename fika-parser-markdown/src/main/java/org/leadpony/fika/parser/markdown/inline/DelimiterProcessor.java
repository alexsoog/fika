/*
 * Copyright 2017-2018 the Fika authors.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author leadpony
 */
public class DelimiterProcessor {
    
    private final DelimiterStack stack;

    private Delimiter stackBottom;
    private final Map<String, Delimiter> openersBottom = new HashMap<>();
    
    public DelimiterProcessor(DelimiterStack stack) {
        this.stack = stack;
    }

    public void processDelimiters(Delimiter bottom) {
        this.stackBottom = bottom;
        this.openersBottom.clear();
        pairAll();
        stack.removeAfter(bottom);
    }
    
    private void pairAll() {
        Iterator<Delimiter> it = stack.iterator();
        while (it.hasNext()) {
            Delimiter current = it.next();
            if (current.canBeCloser()) {
                processCloser(current);
            }
        }
    }
    
    private void processCloser(Delimiter closer) {
        for (;;) {
            Delimiter opener = findOpener(closer);
            if (opener == null) {
                break;
            }
            makePair(opener, closer);
            if (opener.isEmpty()) {
                removeEmptyRun(opener);
            }
            if (closer.isEmpty()) {
                removeEmptyRun(closer);
                break;
            }
        }
    }
    
    private Delimiter findOpener(Delimiter closer) {
        final Delimiter bottom = getOpenersBottom(closer.delimiter());
        Delimiter previous = closer.previous();
        Delimiter newBottom = previous;
        Iterator<Delimiter> it = stack.descendingIterator(closer);
        // Skips the closer.
        it.next();
        while (it.hasNext()) {
            Delimiter current = it.next();
            if (current == bottom) {
                break;
            } else if (current.isSameTypeAs(closer) && current.canBeOpener()) {
                Delimiter opener = current;
                if (opener.canBePairedWith(closer)) {
                    return opener;
                } else {
                    newBottom = opener.previous();
                }
            }
        }
        updateOpenersBottom(closer.delimiter(), newBottom);
        if (newBottom == previous && !closer.canBeOpener()) {
            this.stack.remove(closer);
        }
        return null;
    }
    
    private void makePair(Delimiter opener, Delimiter closer) {
        opener.makePairWith(closer);
        removeDelimitersBeween(opener, closer);
    }
    
    private void removeEmptyRun(Delimiter run) {
        run.text().unlink();
        stack.remove(run);
    }

    private void removeDelimitersBeween(Delimiter opener, Delimiter closer) {
        Iterator<Delimiter> it = stack.iterator(opener);
        it.next();
        while (it.hasNext()) {
            Delimiter current = it.next();
            if (current == closer) {
                break;
            } else {
                stack.remove(current);
            }
        }
    }
    
    private Delimiter getOpenersBottom(String delimiter) {
        Delimiter bottom = this.openersBottom.get(delimiter);
        if (bottom == null) {
            bottom = this.stackBottom;
        }
        return bottom;
    }
    
    private void updateOpenersBottom(String delimiter, Delimiter newBottom) {
        this.openersBottom.put(delimiter, newBottom);
    }
}
