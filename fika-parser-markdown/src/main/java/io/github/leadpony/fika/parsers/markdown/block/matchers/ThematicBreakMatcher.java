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

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.parsers.markdown.block.AbstractBlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BasicBlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * @author leadpony
 */
class ThematicBreakMatcher extends AbstractBlockMatcher {

    ThematicBreakMatcher() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.THEMATIC_BREAK;
    }

    @Override
    public Result match(InputSequence input) {
        return Result.COMPLETED;
    }
    
    @Override
    protected Block buildBlock() {
        return getNodeFactory().newThematicBreak();
    }
}

/**
 * @author leadpony
 */
class ThematicBreakMatcherFactory implements BlockMatcherFactory {
    
    private final ThematicBreakMatcher matcher = new ThematicBreakMatcher();

    ThematicBreakMatcherFactory() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.THEMATIC_BREAK;
    }
    
    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(
                BasicBlockType.PARAGRAPH,
                BasicBlockType.LINK_DEFINITION,
                BasicBlockType.LIST);
    }

    @Override
    public BlockMatcher newMatcher(InputSequence input) {
        return testLine(input) ? matcher : null;
    }

    @Override
    public BlockMatcher newInterrupter(InputSequence input, BlockMatcher current) {
        return newMatcher(input);
    }
    
    private static boolean testLine(InputSequence input) {
        int i = input.countLeadingSpaces(0, 3);
        char lineChar = input.charAt(i);
        if (lineChar != '-' && lineChar != '_' && lineChar != '*') {
            return false;
        }
        return testLine(input, i + 1, lineChar);
    }

    private static boolean testLine(InputSequence input, int offset, char lineChar) {
        int dashes = 1;
        for (int i = offset; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (c == '\u0020' || c == '\t') {
                continue;
            } else if (c == lineChar) {
                ++dashes;
            } else {
                return false;
            }
        }
        return dashes >= 3;
    }
}
