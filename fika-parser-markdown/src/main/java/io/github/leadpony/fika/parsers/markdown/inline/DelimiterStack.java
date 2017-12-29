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

import java.util.LinkedList;

/**
 * @author leadpony
 */
public class DelimiterStack {
    
    private final LinkedList<Entry> entries = new LinkedList<>();
    
    public void push(char delimiter, int length, boolean opener, boolean closer) {
        entries.add(new Entry(delimiter, length, opener, closer));
    }
    
    public static class Entry {
        
        private final char delimiter;
        private int length;
        private boolean active;
        private final boolean opener;
        private final boolean closer;
        
        private Entry(char delimiter, int length, boolean opener, boolean closer) {
            this.delimiter = delimiter;
            this.length = length;
            this.active = true;
            this.opener = opener;
            this.closer = closer;
        }
        
        public char delimiter() {
            return delimiter;
        }
        
        public int length() {
            return length;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public void deactivate() {
            this.active = false;
        }
        
        public boolean isPotentialOpener() {
            return opener;
        }
        
        public boolean isPotentialCloser() {
            return closer;
        }
    }
}
