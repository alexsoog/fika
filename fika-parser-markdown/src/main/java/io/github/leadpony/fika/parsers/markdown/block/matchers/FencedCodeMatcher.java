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
package io.github.leadpony.fika.parsers.markdown.block.matchers;

import static io.github.leadpony.fika.parsers.markdown.common.Characters.SPACE;
import static io.github.leadpony.fika.parsers.markdown.common.Strings.unescape;
import static io.github.leadpony.fika.parsers.markdown.common.Strings.expandReferences;

import java.util.EnumSet;
import java.util.Set;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.CodeBlock;
import io.github.leadpony.fika.parsers.markdown.block.AbstractBlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockTrait;
import io.github.leadpony.fika.parsers.markdown.block.MatcherMode;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

class FencedCodeMatcher extends AbstractBlockMatcher {
    
    private final int indentSize;
    private final char fenceChar;
    private final int fenceLength;
    private final String infoString;
    
    private final StringBuilder builder;
    
    FencedCodeMatcher(int indentSize, char fenceChar, int fenceLength, String infoString) {
        this.indentSize = indentSize;
        this.fenceChar = fenceChar;
        this.fenceLength = fenceLength;
        this.infoString = infoString;
        this.builder = new StringBuilder();
    }
  
    @Override
    public BlockTrait blockTrait() {
        return BlockType.FENCED_CODE;
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

/**
 * @author leadpony
 */
class FencedCodeMatcherFactory implements BlockMatcherFactory { 
    
    FencedCodeMatcherFactory() {
    }

    @Override
    public BlockTrait blockTrait() {
        return BlockType.FENCED_CODE;
    }
    
    @Override
    public Set<? extends BlockTrait> interruptible() {
        return EnumSet.of(BlockType.PARAGRAPH, BlockType.LINK_DEFINITION);
    }

    @Override
    public BlockMatcher newMatcher(InputSequence input) {
        int indentSize = input.countLeadingSpaces(0, 3);
        int i = indentSize;
        char fenceChar = input.charAt(i);
        if (fenceChar != '`' && fenceChar != '~') {
            return null;
        }
        int fenceLength = 1;
        while (++i < input.length()) {
            char c = input.charAt(i);
            if (c != fenceChar) {
                break;
            }
            ++fenceLength;
        }
        if (fenceLength < 3) {
            return null;
        }
        String infoString = extractInfoString(input, i);
        if (infoString.contains("`")) {
            return null;
        }
        return new FencedCodeMatcher(indentSize, fenceChar, fenceLength, infoString);
    }

    @Override
    public BlockMatcher newInterrupter(InputSequence input, BlockMatcher current, MatcherMode mode) {
        return newMatcher(input);
    }
    
    private String extractInfoString(InputSequence input, int offset) {
        return input.subSequence(offset).trimSpaces().toSourceString();
    }
}
