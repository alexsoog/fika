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

import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.block.BasicBlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * @author leadpony
 */
class ParagraphMatcher extends AbstractParagraphMatcher {
    
    private BlockBuilder blockBuilder;
    
    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("\\u0020{0,3}(=+|-{2,})\\u0020*");

    ParagraphMatcher() {
        this.blockBuilder = null;
    }
  
    @Override
    public BlockType blockType() {
        return BasicBlockType.PARAGRAPH;
    }
    
    @Override
    public Result match(InputSequence input) {
        if (lineNo() <= 1) {
            appendLine(input);
            return Result.CONTINUED;
        } else if (input.isBlank() || this.blockBuilder != null) {
            return Result.COMPLETED;
        } else {
            appendLine(input);
            return Result.CONTINUED;
        }
    }

    @Override
    public boolean isInterruptible() {
        return lineNo() > 1;
    }
    
    @Override
    public BlockMatcher interrupt(InputSequence input) {
        // Handles underline before interrupted by ThematicBreakMatcher. 
        if (matchHeading(input)) {
            return null;
        }
        return super.interrupt(input);
    }

    @Override
    public Result continueLazily(InputSequence input) {
        if (super.interrupt(input) != null) {
            return Result.NOT_MATCHED;
        } else if (input.isBlank()) {
            return Result.NOT_MATCHED;
        }
        return match(input);
    }
    
    @Override
    protected Block buildBlock() {
        String content = buildContent(0);
        if (this.blockBuilder != null) {
            return this.blockBuilder.buildBlock(content);
        } else {
            return buildParagraph(content);
        }
    }
    
    private boolean matchHeading(InputSequence input) {
        if (!UNDERLINE_PATTERN.matcher(input).matches()) {
            return false;
        }
        int index = input.countLeadingSpaces(0, 3);
        char c = input.charAt(index);
        int level = (c == '=') ? 1 : 2;
        this.blockBuilder = new HeadingBuilder(level);
        return true;
    }
    
    private Block buildParagraph(String content) {
        if (content.isEmpty()) {
            return null;
        }
        Block block = getNodeFactory().newParagraph();
        block.appendChild(addText(content));
        return block;
    }
    
    private Text addText(String content) {
        Text text = getNodeFactory().newText();
        text.setContent(content);
        context().addInline(text);
        return text;
    }

    @FunctionalInterface
    private static interface BlockBuilder {
        Block buildBlock(String content);
    }
    
    private class HeadingBuilder implements BlockBuilder {
        
        private int level;
        
        public HeadingBuilder(int level) {
            this.level = level;
        }
    
        @Override
        public Block buildBlock(String content) {
            Block block = getNodeFactory().newHeading(this.level);
            block.appendChild(addText(content));
            return block;
        }
    }
}    

/**
 * @author leadpony
 */
class ParagraphMatcherFactory implements BlockMatcherFactory {

    @Override
    public BlockType blockType() {
        return BasicBlockType.PARAGRAPH;
    }

    @Override
    public BlockMatcher newMatcher(InputSequence input) {
        return new ParagraphMatcher();
    }
}
