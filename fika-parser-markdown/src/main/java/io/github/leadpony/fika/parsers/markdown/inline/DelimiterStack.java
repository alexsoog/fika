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

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Delimiter stack.
 * 
 * @author leadpony
 */
public class DelimiterStack extends AbstractCollection<Delimiter> {

    private Delimiter first;
    private Delimiter last;
    private int size;
    
    public DelimiterStack() {
        this.first = null;
        this.last = null;
        this.size = 0;
    }
    
    /* Collection interface */
    
    @Override
    public boolean add(Delimiter e) {
        if (e == null) {
            throw new NullPointerException();
        }
        if (last == null) {
            first = last = e;
        } else {
            ((Entry)last).next = e;
            ((Entry)e).previous = last;
            ((Entry)e).next = null;
            last = e;
        }
        this.size++;
        return true;
    }
    
    @Override
    public void clear() {
        this.first = null;
        this.last = null;
        this.size = 0;
    }
    
    @Override
    public Iterator<Delimiter> iterator() {
        return new AscendingIterator(this.first);
    }
    
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            throw new NullPointerException();
        } else if (!(o instanceof Entry)) {
            throw new ClassCastException();
        }
        Delimiter entry = (Delimiter)o;
        final Delimiter previous = entry.previous();
        final Delimiter next = entry.next();
        if (this.first == entry) {
            this.first = next;
        }
        if (this.last == entry) {
            this.last = previous;
        }
        if (previous != null) {
            ((Entry)previous).next = next;
        }
        if (next != null) {
            ((Entry)next).previous = previous;
        }
        ((Entry)entry).previous = ((Entry)entry).next = null;
        this.size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }
    
    public Delimiter getFirst() {
        return first;
    }

    public Delimiter getLast() {
        return last;
    }
    
    public Iterator<Delimiter> iterator(Delimiter first) {
        return new AscendingIterator(first);
    }

    public Iterator<Delimiter> descendingIterator() {
        return new DescendingIterator(this.last);
    }
    
    public Iterator<Delimiter> descendingIterator(Delimiter first) {
        return new DescendingIterator(first);
    }
    
    /**
     * Entry of this stack.
     * 
     * @author leadpony
     */
    public static class Entry {
        
        private Delimiter previous;
        private Delimiter next;
        
        public Delimiter previous() {
            return previous;
        }
        
        public Delimiter next() {
            return next;
        }
    }
    
    private static class AscendingIterator implements Iterator<Delimiter> {
        
        private Delimiter current;
        
        private AscendingIterator(Delimiter first) {
            this.current = first;
        }

        @Override
        public boolean hasNext() {
            return (this.current != null);
        }

        @Override
        public Delimiter next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Delimiter current = this.current;
            this.current = current.next();
            return current;
        }
    }
    
    private static class DescendingIterator implements Iterator<Delimiter> {
        
        private Delimiter current;
        
        private DescendingIterator(Delimiter first) {
            this.current = first;
        }

        @Override
        public boolean hasNext() {
            return (this.current != null);
        }

        @Override
        public Delimiter next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Delimiter current = this.current;
            this.current = current.previous();
            return current;
        }
    }
}
