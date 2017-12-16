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
package io.github.leadpony.fika.parsers.markdown.block;

import java.util.regex.Pattern;

/**
 * @author leadpony
 */
class Content {
    
    private final String line;
    private final int beginIndex;
    private final int endIndex;
    private final int length;
    
    /**
     * A line containing no characters, or a line containing only spaces (U+0020) 
     * or tabs (U+0009), is called a blank line.
     */
    private static final Pattern BLANK_PATTERN = Pattern.compile("[\\u0020\\t]*");
  
    private static final String[] EXPANDED_TAB = { "\t   ", "\t  ", "\t ", "\t" }; 
    
    Content(String line) {
        this.line = expandTabs(line);
        this.beginIndex = 0;
        this.endIndex = this.length = this.line.length();
    }
    
    private Content(String line, int beginIndex, int endIndex) {
        this.line = line;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.length = endIndex - beginIndex;
    }
    
    int length() {
        return length;
    }
    
    char charAt(int index) {
        return this.line.charAt(this.beginIndex + index);
    }
    
    Content subcontent(int beginIndex) {
        return subcontent(beginIndex, length);
    }

    Content subcontent(int beginIndex, int endIndex) {
        int offset = this.beginIndex;
        return new Content(this.line, offset + beginIndex, offset + endIndex);
    }
    
    boolean isBlank() {
        String s = line.substring(beginIndex, endIndex);
        return BLANK_PATTERN.matcher(s).matches();
    }
    
    Content trimSpaces() {
        int i = 0;
        while (i < length) {
            char c = charAt(i);
            if (c != '\u0020' && c != '\t') {
                break;
            }
            ++i;
        }
        int beginIndex = i;
        i = length() - 1;
        while (i > beginIndex) {
            char c = charAt(i);
            if (c != '\u0020' && c != '\t') {
                break;
            }
            --i;
        }
        int endIndex = i + 1;
        return subcontent(beginIndex, endIndex);
    }
    
    Content trimLeadingSpaces() {
        int i = 0;
        while (i < length) {
            char c = charAt(i);
            if (c != '\u0020' && c != '\t') {
                break;
            }
            ++i;
        }
        return subcontent(i, length);
    }
    
    int skipSmallIndent() {
        int i = 0;
        while (i < length && i < 3) {
            char c = charAt(i);
            if (c != '\u0020' && c != '\t') {
                break;
            }
            ++i;
        }
        return i;
    }
    
    Content trimSmallIndent() {
        int beginIndex = skipSmallIndent();
        return subcontent(beginIndex, length());
    }
    
    @Override
    public String toString() {
        return restoreTabs(line, beginIndex, endIndex);
    }

    private static String expandTabs(String line) {
        StringBuilder b = new StringBuilder();
        int last = 0;
        for (int i = 0; i < line.length(); ++i) {
            if (line.charAt(i) == '\t') {
                b.append(line, last, i);
                b.append(EXPANDED_TAB[b.length() % 4]);
                last = i + 1;
            }
        }
        if (last < line.length()) {
            b.append(line, last, line.length());
        }
        return b.toString();
    }

    private static String restoreTabs(String line, int beginIndex, int endIndex) {
        StringBuilder b = new StringBuilder();
        int last = beginIndex;
        for (int i = beginIndex; i < endIndex;) {
            if (line.charAt(i) == '\t') {
                b.append(line, last, i + 1);
                i += EXPANDED_TAB[i % 4].length();
                last = i;
            } else {
                ++i;
            }
        }
        if (last < endIndex) {
            b.append(line, last, endIndex);
        }
        return b.toString();
    }
}
