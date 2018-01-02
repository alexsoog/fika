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

import io.github.leadpony.fika.core.nodes.Heading;
import io.github.leadpony.fika.core.nodes.Text;

/**
 * @author leadpony
 */
class HeadingMatcherFactory implements BlockMatcherFactory {
    
    private static final int MAX_LEVEL = 6;

    HeadingMatcherFactory() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.HEADING;
    }

    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(BasicBlockType.PARAGRAPH);
    }
    
    @Override
    public BlockMatcher newMatcher(BlockInputSequence content) {
        int i = content.countLeadingSpaces(0, 3);
        int level = 0;
        for (; i < content.length(); i++) {
            if (content.charAt(i) != '#') {
                break;
            }
            if (++level > MAX_LEVEL) {
                return null;
            }
        }
        if (level == 0) {
            return null;
        }
        if (i < content.length()) {
            // Checks the first letter after #.
            char c = content.charAt(i++);
            if (c != '\u0020' && c != '\t') {
                return null;
            }
        }
        return new HeadingMatcher(level, extractTitle(content.subContent(i)));
    }

    @Override
    public BlockMatcher newInterrupter(BlockInputSequence content, BlockMatcher current) {
        return newMatcher(content);
    }
    
    private static String extractTitle(BlockInputSequence content) {
        content = content.trimSpaces();
        content = trimClosingSequenceOfHash(content);
        return content.trimSpaces().toSourceString();
    }
    
    private static BlockInputSequence trimClosingSequenceOfHash(BlockInputSequence content) {
        if (content.length() == 0) {
            return content;
        }
        int i = content.length() - 1;
        char c = content.charAt(i);
        if (c != '#') {
            return content;
        }
        while (i >= 0) {
            c = content.charAt(i);
            if (c != '#') {
                break;
            }
            --i;
        }
        if (i < 0) {
            return content.subContent(0, 0);
        }
        c = content.charAt(i);
        if (c == '\u0020' || c == '\t') {
            return content.subContent(0, i);
        } else {
            return content;
        }
    }

    private static class HeadingMatcher extends AbstractBlockMatcher {

        private final int level;
        private final String title;
      
        private HeadingMatcher(int level, String title) {
            this.level = level;
            this.title = title;
        }
    
        @Override
        public BlockType blockType() {
            return BasicBlockType.HEADING;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            return Result.COMPLETED;
        }
        
        @Override
        protected Heading buildBlock() {
            Heading block = nodeFactory().newHeading(this.level);
            Text text = nodeFactory().newText();
            text.setContent(this.title);
            block.appendChild(text);
            context().addInline(text);
            return block;
        }
    }
}
