/*
 * Copyright 2017-2019 the Fika authors.
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
package org.leadpony.fika.parser.markdown.block.misc;

import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.parser.markdown.block.AbstractBlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.block.commonmark.BasicBlockType;
import org.leadpony.fika.parser.markdown.block.commonmark.ParagraphBuilder;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Builder of term in definition list.
 * 
 * @author leadpony
 */
class TermBuilder extends AbstractBlockBuilder {

    private final ParagraphBuilder paragraphBuilder;
    
    TermBuilder(ParagraphBuilder replacedBuilder) {
        this.paragraphBuilder = replacedBuilder;
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.TERM;
    }
    
    @Override
    public int firstLineNo() {
        return paragraphBuilder.firstLineNo();
    }
    
    public ParagraphBuilder internalBuilder() {
        return paragraphBuilder;
    }
    
    @Override
    protected boolean isInterruptible() {
        return true;
    }
    
    @Override
    protected Result processLine(InputSequence input) {
        Result result = paragraphBuilder.appendLine(input);
        if (result == Result.CONTINUED) {
            return result;
        } else {
            setSuccessor(paragraphBuilder);
            return Result.REPLACED;
        }
    }
    
    @Override
    protected Block buildBlock() {
        Block block = getNodeFactory().newTerm();
        String content = paragraphBuilder.getContent();
        block.appendChild(getNodeFactory().newText(content));
        return block;
    }
}
