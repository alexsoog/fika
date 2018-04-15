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

import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Builder of paragraphs.
 * 
 * @author leadpony
 */
public class ParagraphBuilder extends AbstractParagraphBuilder {
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.PARAGRAPH;
    }
    
    @Override
    public Result appendLazyLine(InputSequence input) {
        BlockBuilder interrupter = interrupt(input, BuilderMode.LAZY_CONTINUATION);
        if (interrupter != null || input.isBlank()) {
            return Result.NOT_MATCHED;
        }
        return processLine(input);
    }
    
    @Override
    protected Result processLine(InputSequence input) {
        if (lineCount() == 0) {
            accumulateLine(input);
            return Result.CONTINUED;
        } else if (input.isBlank()) {
            return Result.COMPLETED;
        } else {
            accumulateLine(input);
            return Result.CONTINUED;
        }
    }

    @Override
    protected boolean isInterruptible() {
        return true;
    }
    
    @Override
    protected Block buildBlock() {
        return buildParagraph(0);
    }
}    
