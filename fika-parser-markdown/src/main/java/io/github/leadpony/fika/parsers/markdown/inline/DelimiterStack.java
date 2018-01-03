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

    private Entry first;
    private Entry last;
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
        Entry entry = (Entry)e;
        if (last == null) {
            first = last = e;
        } else {
            last.next = e;
            entry.previous = last;
            entry.next = null;
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
        return new AscendingIterator<Delimiter>(this.first);
    }
    
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            throw new NullPointerException();
        } else if (!(o instanceof Entry)) {
            throw new ClassCastException();
        }
        Entry entry = (Entry)o;
        final Entry previous = entry.previous;
        final Entry next = entry.next;
        if (this.first == entry) {
            this.first = next;
        }
        if (this.last == entry) {
            this.last = previous;
        }
        if (previous != null) {
            previous.next = next;
        }
        if (next != null) {
            next.previous = previous;
        }
        entry.previous = entry.next = null;
        this.size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }
    
    public Delimiter getFirst() {
        return (Delimiter)first;
    }

    public Delimiter getLast() {
        return (Delimiter)last;
    }
    
    public Iterator<Delimiter> iterator(Delimiter first) {
        return new AscendingIterator<Delimiter>(first);
    }

    public Iterator<Delimiter> descendingIterator() {
        return new DescendingIterator<Delimiter>(this.last);
    }
    
    public Iterator<Delimiter> descendingIterator(Delimiter first) {
        return new DescendingIterator<Delimiter>(first);
    }
    
    public static class Entry {
        private Entry previous;
        private Entry next;
    }
    
    private static class AscendingIterator<T extends Entry> implements Iterator<T> {
        
        private Entry current;
        
        private AscendingIterator(Entry first) {
            this.current = first;
        }

        @Override
        public boolean hasNext() {
            return (this.current != null);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Entry current = this.current;
            this.current = current.next;
            return (T)current;
        }
    }
    
    private static class DescendingIterator<T extends Entry> implements Iterator<T> {
        
        private Entry current;
        
        private DescendingIterator(Entry first) {
            this.current = first;
        }

        @Override
        public boolean hasNext() {
            return (this.current != null);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Entry current = this.current;
            this.current = current.previous;
            return (T)current;
        }
    }
}
