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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author leadpony
 */
class DelimiterRunProcessor {
    
    private LinkedStack<DelimiterRun> stack;
    /*
     * Note that we process delimiter runs INCLUDING the bottom.
     */
    private DelimiterRun stackBottom;
    private final Map<Character, DelimiterRun> openersBottom = new HashMap<>();

    void processDelimiters(LinkedStack<DelimiterRun> stack) {
        this.stack = stack;
        this.stackBottom = stack.getFirst();
        this.openersBottom.clear();
        pairAll();
        stack.clear();
    }
    
    private void pairAll() {
        Iterator<DelimiterRun> it = stack.iterator();
        while (it.hasNext()) {
            DelimiterRun current = it.next();
            if (current.canBeCloser()) {
                processCloser(current);
            }
        }
    }
    
    private void processCloser(DelimiterRun closer) {
        for (;;) {
            DelimiterRun opener = findOpener(closer);
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
    
    private DelimiterRun findOpener(DelimiterRun closer) {
        final DelimiterRun bottom = getOpenersBottom(closer.delimiter());
        DelimiterRun nextBottom = closer;
        Iterator<DelimiterRun> it = stack.descendingIterator(closer);
        // skips the closer.
        it.next();
        while (it.hasNext()) {
            DelimiterRun current = it.next();
            if (current.canBePairedWith(closer)) {
                if (shouldPair(current, closer)) {
                    return current;
                } else {
                    nextBottom = current;
                }
            }
            if (current == bottom) {
                break;
            }
        }
        updateOpenersBottom(closer.delimiter(), nextBottom);
        if (nextBottom == closer && !closer.canBeOpener()) {
            this.stack.remove(closer);
        }
        return null;
    }
    
    /**
     * Checks if opener and closer should be paired.
     * 
     * Note that a delimiter run that can be both opener and closer
     * cannot form pair if the sum of the lengths of the delimiter runs
     * containing the opening and closing delimiters is a multiple of 3.
     *  
     * @param opener the opening delimiter run.
     * @param closer the closing delimiter run.
     * @return true if delimiter runs should be paired.
     */
    private boolean shouldPair(DelimiterRun opener, DelimiterRun closer) {
        if (opener.canBeCloser() || closer.canBeOpener()) {
            int sumOfLengths = opener.length() + closer.length();
            return sumOfLengths % 3 != 0;
        }
        return true;
    }
    
    private void makePair(DelimiterRun opener, DelimiterRun closer) {
        opener.makePairWith(closer);
        removeDelimitersBeween(opener, closer);
    }
    
    private void removeEmptyRun(DelimiterRun run) {
        run.text().unlink();
        stack.remove(run);
    }

    private void removeDelimitersBeween(DelimiterRun opener, DelimiterRun closer) {
        Iterator<DelimiterRun> it = stack.iterator(opener);
        it.next();
        while (it.hasNext()) {
            DelimiterRun current = it.next();
            if (current == closer) {
                break;
            } else {
                stack.remove(current);
            }
        }
    }
    
    private DelimiterRun getOpenersBottom(char delimiter) {
        DelimiterRun bottom = this.openersBottom.get(delimiter);
        if (bottom == null) {
            bottom = this.stackBottom;
        }
        return bottom;
    }
    
    private void updateOpenersBottom(char delimiter, DelimiterRun newBottom) {
        this.openersBottom.put(delimiter, newBottom);
    }
}
