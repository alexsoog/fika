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
package io.github.leadpony.fika.parsers.markdown.common;

import static io.github.leadpony.fika.parsers.markdown.common.Characters.SPACE;

import java.util.regex.Pattern;

/**
 * @author leadpony
 */
public interface InputSequence extends CharSequence {
    
    /**
     * A regex pattern representing a space or a tab.
     */
    Pattern BLANK_PATTERN = Pattern.compile("\\p{Blank}*");
    
    public static InputSequence withOffset(String string, int offset) {
        return new OffsetInputSequence(string, offset);
    }
    
    /* CharSequence interface */

    @Override
    default CharSequence subSequence(int start, int end) {
        return subContent(start, end);
    }

    /* String like interface */
    
    default boolean isEmpty() {
        return length() == 0;
    }
    
    default boolean contains(CharSequence s) {
        if (s.length() == 0) {
            return true;
        }
        char first = s.charAt(0);
        int end = length() - s.length() + 1;
        for (int i = 0; i < end; ++i) {
            if (charAt(i) == first) {
                int j = 1;
                while (j < s.length()) {
                    if (charAt(i + j) != s.charAt(j)) {
                        break;
                    }
                    ++j;
                }
                if (j >= s.length()) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean startsWith(String prefix) {
        final int length = length();
        for (int i = 0; i < prefix.length(); ++i) {
            if (i >= length || charAt(i) != prefix.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    default int indexOf(char c) {
        return indexOf(c, 0);
    }

    default int indexOf(char c, int beginIndex) {
        for (int i = beginIndex; i < length(); ++i) {
            if (charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }
    
    String substring(int beginIndex, int endIndex);

    /* */
    
    /**
     * Checks if this content is blank.
     * A line containing no characters, or a line containing only spaces (U+0020) 
     * or tabs (U+0009), is called a blank line.
     * 
     * @return true if this content is blank.
     */
    default boolean isBlank() {
        if (isEmpty()) {
            return true;
        }
        return BLANK_PATTERN.matcher(this).matches();
    }
    
    default InputSequence subContent(int beginIndex) {
        if (beginIndex == 0) {
            return this;
        }
        return subContent(beginIndex, length());
    }
    
    InputSequence subContent(int beginIndex, int endIndex);
    
    default int countLeading(char c) {
        return countLeading(c, 0, length());
    }
    
    default int countLeading(char c, int beginIndex) {
        return countLeading(c, beginIndex, length());
    }

    default int countLeading(char c, int beginIndex, int endIndex) {
        if (endIndex > length()) {
            endIndex = length();
        }
        int i = beginIndex;
        while (i < endIndex) {
            if (charAt(i) != c) {
                break;
            }
            ++i;
        }
        return i - beginIndex;
    }
    
    default int countLeadingSpaces() {
        return countLeading(SPACE, 0, length());
    }
    
    default int countLeadingSpaces(int beginIndex) {
        return countLeading(SPACE, beginIndex, length());
    }

    default int countLeadingSpaces(int beginIndex, int endIndex) {
        return countLeading(SPACE, beginIndex, endIndex);
    }

    default int countLeadingNot(char c) {
        return countLeadingNot(c, 0, length());
    }

    default int countLeadingNot(char c, int beginIndex) {
        return countLeadingNot(c, beginIndex, length());
    }
    
    default int countLeadingNot(char c, int beginIndex, int endIndex) {
       int i = beginIndex;
       while (i < endIndex) {
           if (charAt(i) == c) {
               break;
           }
           ++i;
       }
       return i - beginIndex;
    }
    
    default boolean hasLeading(char c, int count) {
        int found = countLeading(c, 0, count);
        return found >= count;
    }
    
    default boolean hasLeadingSpaces(int count) {
        return hasLeading(SPACE, count);
    }
    
    default String toSourceString() {
        return toString();
    }
}
