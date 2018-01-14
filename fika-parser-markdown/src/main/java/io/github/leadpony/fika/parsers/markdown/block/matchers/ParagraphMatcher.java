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

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.parsers.markdown.block.BasicBlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Matcher for paragraphs.
 * 
 * @author leadpony
 */
class ParagraphMatcher extends AbstractParagraphMatcher {
    
    private boolean lazy;
    private boolean canceled;
    
    ParagraphMatcher() {
        this.lazy = false;
        this.canceled = false;
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
        } else if (input.isBlank()) {
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
        BlockMatcher interrupter = super.interrupt(input);
        if (interrupter instanceof SetextHeadingMatcher) {
            this.canceled = true;
        }
        return interrupter;
    }

    @Override
    public Result continueLazily(InputSequence input) {
        this.lazy = true;
        BlockMatcher interrupter = interrupt(input);
        this.lazy = false;
        if (interrupter != null || input.isBlank()) {
            return Result.NOT_MATCHED;
        }
        return match(input);
    }
    
    public boolean isLazy() {
        return lazy;
    }
    
    @Override
    protected Block buildBlock() {
        if (canceled) {
            return null;
        }
        return buildParagraph(0);
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
