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
package io.github.leadpony.fika.parser.markdown.block.matchers;

import static io.github.leadpony.fika.parser.markdown.common.Characters.SPACE;
import static io.github.leadpony.fika.parser.markdown.common.Strings.expandReferences;
import static io.github.leadpony.fika.parser.markdown.common.Strings.unescape;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.CodeBlock;
import io.github.leadpony.fika.parser.markdown.block.AbstractBlockBuilder;
import io.github.leadpony.fika.parser.markdown.block.MatcherType;
import io.github.leadpony.fika.parser.markdown.common.InputSequence;

class FencedCodeBuilder extends AbstractBlockBuilder {
    
    private final int indentSize;
    private final char fenceChar;
    private final int fenceLength;
    private final String infoString;
    
    private final StringBuilder builder;
    
    FencedCodeBuilder(int indentSize, char fenceChar, int fenceLength, String infoString) {
        this.indentSize = indentSize;
        this.fenceChar = fenceChar;
        this.fenceLength = fenceLength;
        this.infoString = infoString;
        this.builder = new StringBuilder();
    }
  
    @Override
    public MatcherType matcherType() {
        return BasicMatcherType.FENCED_CODE;
    }
    
    @Override
    public Result match(InputSequence input) {
        if (lineNo() <= 1) {
            return Result.CONTINUED;
        }
        if (testClosingFence(input)) {
            return Result.COMPLETED;
        }
        appendLine(input);
        return Result.CONTINUED;
    }

    @Override
    protected Block buildBlock() {
        CodeBlock block = getNodeFactory().newCodeBlock(builder.toString());
        String infoString = unescape(expandReferences(this.infoString));
        String[] words = infoString.split("\\s+");
        String language = words[0];
        if (!language.isEmpty()) {
            block.setLanguage(language);
        }
        return block;
    }

    private boolean testClosingFence(InputSequence input) {
        int i = input.countLeadingSpaces(0, 3);
        if (i >= input.length()) {
            return false;
        }
        char c = input.charAt(i);
        if (c != fenceChar) {
            return false;
        }
        int length = 1;
        while (++i < input.length()) {
            c = input.charAt(i);
            if (c == fenceChar) {
                ++length;    
            } else if (c == SPACE) {
                break;
            } else {
                return false;
            }
        }
        if (length < fenceLength) {
            return false;
        }
        while (++i < input.length()) {
            c = input.charAt(i);
            if (c != SPACE) {
                return false;
            }
        }
        return true;
    }
    
    private void appendLine(InputSequence input) {
        if (indentSize > 0) {
            int beginIndex = input.countLeadingSpaces(0,  indentSize);
            input = input.subSequence(beginIndex);
        }
        builder.append(input.toSourceString()).append('\n');
    }
}
