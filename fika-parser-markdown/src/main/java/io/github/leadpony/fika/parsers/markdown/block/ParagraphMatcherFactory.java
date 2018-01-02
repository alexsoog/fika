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

import static io.github.leadpony.fika.parsers.markdown.base.Characters.trim;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.leadpony.fika.core.nodes.Block;
import io.github.leadpony.fika.core.nodes.Text;

/**
 * @author leadpony
 */
class ParagraphMatcherFactory implements BlockMatcherFactory {

    @Override
    public BlockType blockType() {
        return BasicBlockType.PARAGRAPH;
    }

    @Override
    public BlockMatcher newMatcher(BlockInputSequence content) {
        return new ParagraphMatcher();
    }
    
    @FunctionalInterface
    private static interface BlockProvider {
        Block provide(BlockMatcher.Context context);
    }
    
    private static class ParagraphMatcher extends AbstractBlockMatcher {
        
        private final List<String> lines;
        private BlockProvider blockProvider;
        
        private static final Pattern UNDERLINE_PATTERN = Pattern.compile("\\u0020{0,3}(=+|-{2,})\\u0020*");
        
        ParagraphMatcher() {
            this.lines = new ArrayList<>();
            this.blockProvider = null;
        }
      
        @Override
        public BlockType blockType() {
            return BasicBlockType.PARAGRAPH;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            if (lineNo() <= 1) {
                appendLine(content);
                return Result.CONTINUED;
            } else if (content.isBlank() || this.blockProvider != null) {
                return Result.COMPLETED;
            }
            appendLine(content);
            return Result.CONTINUED;
        }
    
        @Override
        public boolean isInterruptible() {
            return lineNo() > 1;
        }
        
        @Override
        public BlockMatcher interrupt(BlockInputSequence content) {
            // Handles underline before interrupted by ThematicBreakMatcher. 
            if (matchHeading(content)) {
                return null;
            }
            return super.interrupt(content);
        }
    
        @Override
        public Result continueLazily(BlockInputSequence content) {
            if (super.interrupt(content) != null) {
                return Result.NOT_MATCHED;
            } else if (content.isBlank()) {
                return Result.NOT_MATCHED;
            }
            return match(content);
        }
        
        @Override
        protected Block buildBlock() {
            Block block = null;
            if (this.blockProvider != null) {
                block = this.blockProvider.provide(context());
            } else {
                block = nodeFactory().newParagraph();
            }
            Text text = nodeFactory().newText();
            text.setContent(buildContent());
            block.appendChild(text);
            context().addInline(text);
            return block;
        }
        
        private void appendLine(BlockInputSequence content) {
            String extracted = content.trimLeadingSpaces().toSourceString();
            this.lines.add(extracted);
        }
    
        private boolean matchHeading(BlockInputSequence content) {
            if (!UNDERLINE_PATTERN.matcher(content).matches()) {
                return false;
            }
            int index = content.countLeadingSpaces(0, 3);
            char c = content.charAt(index);
            int level = (c == '=') ? 1 : 2;
            this.blockProvider = HeadingProvider.of(level);
            return true;
        }
    
        /**
         * Builds the content of current paragraph.
         * 
         * The paragraph's raw content is formed by
         * concatenating the lines and removing initial and final whitespace.
         * 
         * @return the content of the paragraph.
         */
        private String buildContent() {
            String content = lines.stream().collect(Collectors.joining("\n"));
            return trim(content);    
        }
    }    

    private static class HeadingProvider implements BlockProvider {
        
        static final HeadingProvider instances[] = {
            new HeadingProvider(1),
            new HeadingProvider(2)
        };
        
        final int level;
        
        static BlockProvider of(int level) {
            return instances[level - 1];
        }
        
        private HeadingProvider(int level) {
            this.level = level;
        }
    
        @Override
        public Block provide(BlockMatcher.Context context) {
            return context.nodeFactory().newHeading(this.level);
        }
    }
}
