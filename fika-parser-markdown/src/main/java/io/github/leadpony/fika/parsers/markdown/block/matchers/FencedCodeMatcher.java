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

import java.util.EnumSet;
import java.util.Set;

import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockBuilder;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockTrait;
import io.github.leadpony.fika.parsers.markdown.block.BuilderMode;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * @author leadpony
 */
public class FencedCodeMatcher implements BlockMatcher { 
    
    public FencedCodeMatcher() {
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
    public BlockBuilder newBuilder(InputSequence input) {
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
        return new FencedCodeBuilder(indentSize, fenceChar, fenceLength, infoString);
    }

    @Override
    public BlockBuilder newInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        return newBuilder(input);
    }
    
    private String extractInfoString(InputSequence input, int offset) {
        return input.subSequence(offset).trimSpaces().toSourceString();
    }
}
