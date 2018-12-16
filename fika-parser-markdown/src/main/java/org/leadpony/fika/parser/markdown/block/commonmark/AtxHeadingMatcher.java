/*
 * Copyright 2017-2018 the Fika authors.
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
package org.leadpony.fika.parser.markdown.block.commonmark;

import java.util.EnumSet;
import java.util.Set;

import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockMatcher;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Matcher implementation for ATX heading.
 * 
 * @author leadpony
 */
public class AtxHeadingMatcher implements BlockMatcher {
    
    private static final int MAX_LEVEL = 6;

    @Override
    public BlockType blockType() {
        return BasicBlockType.ATX_HEADING;
    }
    
    @Override
    public Set<? extends BlockType> typesToInterrupt() {
        return EnumSet.of(BasicBlockType.PARAGRAPH, BasicBlockType.LINK_DEFINITION);
    }
    
    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        int i = input.countLeadingSpaces(0, 3);
        int level = 0;
        for (; i < input.length(); i++) {
            if (input.charAt(i) != '#') {
                break;
            }
            if (++level > MAX_LEVEL) {
                return null;
            }
        }
        if (level == 0) {
            return null;
        }
        if (i < input.length()) {
            // Checks the first letter after #.
            char c = input.charAt(i++);
            if (c != '\u0020' && c != '\t') {
                return null;
            }
        }
        return new AtxHeadingBuilder(level, extractTitle(input.subSequence(i)));
    }

    @Override
    public BlockBuilder newInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        return newBuilder(input);
    }
    
    private static String extractTitle(InputSequence input) {
        input = input.trimSpaces();
        input = trimClosingSequenceOfHash(input);
        return input.trimSpaces().toSourceString();
    }
    
    private static InputSequence trimClosingSequenceOfHash(InputSequence input) {
        if (input.length() == 0) {
            return input;
        }
        int i = input.length() - 1;
        char c = input.charAt(i);
        if (c != '#') {
            return input;
        }
        while (i >= 0) {
            c = input.charAt(i);
            if (c != '#') {
                break;
            }
            --i;
        }
        if (i < 0) {
            return input.subSequence(0, 0);
        }
        c = input.charAt(i);
        if (c == '\u0020' || c == '\t') {
            return input.subSequence(0, i);
        } else {
            return input;
        }
    }
}
