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
package org.leadpony.fika.parser.markdown.common;

/**
 * @author leadpony
 */
class OffsetInputSequence implements InputSequence {
    
    private final String string;
    private int beginIndex;
    private final int endIndex;
    
    OffsetInputSequence(String string) {
        this(string, 0, string.length());
    }

    OffsetInputSequence(String string, int beginIndex) {
        this(string, beginIndex, string.length());
    }

    OffsetInputSequence(String string, int beginIndex, int endIndex) {
        this.string = string;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }
    
    OffsetInputSequence updateIndex(int beginIndex) {
        this.beginIndex = beginIndex;
        return this;
    }

    @Override
    public int length() {
        return endIndex - beginIndex;
    }

    @Override
    public char charAt(int index) {
        return string.charAt(beginIndex + index);
    }

    @Override
    public String toString() {
        return string.substring(beginIndex, endIndex);
    }

    @Override
    public String substring(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex < 0 || beginIndex > endIndex || endIndex > length()) {
            throw new IndexOutOfBoundsException();
        }
        int newBeginIndex = this.beginIndex + beginIndex;
        int newEndIndex = this.beginIndex + endIndex;
        return string.substring(newBeginIndex, newEndIndex);
    }
    
    @Override
    public InputSequence subSequence(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex < 0 || beginIndex > endIndex || endIndex > length()) {
            throw new IndexOutOfBoundsException();
        }
        if (beginIndex == 0 && endIndex == length()) {
            return this;
        }
        int newBeginIndex = this.beginIndex + beginIndex;
        int newEndIndex = this.beginIndex + endIndex;
        return new OffsetInputSequence(string, newBeginIndex, newEndIndex);
    }
}
