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

import java.util.EnumSet;
import java.util.Set;

import io.github.leadpony.fika.core.nodes.Block;

/**
 * @author leadpony
 */
class ThematicBreakMatcherFactory implements BlockMatcherFactory {
    
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
                BasicBlockType.LIST);
    }

    @Override
    public BlockMatcher newMatcher(BlockInputSequence content) {
        return testLine(content) ? ThematicBreakMatcher.instance : null;
    }

    @Override
    public BlockMatcher newInterrupter(BlockInputSequence content, BlockMatcher current) {
        return newMatcher(content);
    }
    
    private static boolean testLine(BlockInputSequence content) {
        int i = content.countLeadingSpaces(0, 3);
        char lineChar = content.charAt(i);
        if (lineChar != '-' && lineChar != '_' && lineChar != '*') {
            return false;
        }
        return testLine(content, i + 1, lineChar);
    }

    private static boolean testLine(BlockInputSequence content, int offset, char lineChar) {
        int dashes = 1;
        for (int i = offset; i < content.length(); ++i) {
            char c = content.charAt(i);
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

    private static class ThematicBreakMatcher extends AbstractBlockMatcher {

        static final ThematicBreakMatcher instance = new ThematicBreakMatcher();
    
        ThematicBreakMatcher() {
        }
    
        @Override
        public BlockType blockType() {
            return BasicBlockType.THEMATIC_BREAK;
        }
    
        @Override
        public Result match(BlockInputSequence content) {
            return Result.COMPLETED;
        }
        
        @Override
        protected Block buildBlock() {
            return nodeFactory().newThematicBreak();
        }
    }
}
