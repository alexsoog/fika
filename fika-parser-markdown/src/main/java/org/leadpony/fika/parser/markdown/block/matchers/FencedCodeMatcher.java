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
package org.leadpony.fika.parser.markdown.block.matchers;

import java.util.EnumSet;
import java.util.Set;

import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockMatcher;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Matcher implementation for fenced code.
 * 
 * @author leadpony
 */
public class FencedCodeMatcher implements BlockMatcher { 
    
    public FencedCodeMatcher() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.FENCED_CODE;
    }
 
    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(BasicBlockType.PARAGRAPH, BasicBlockType.LINK_DEFINITION);
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
