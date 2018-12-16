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
package org.leadpony.fika.parser.markdown.block.misc;

import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.block.ContainerBlockBuilder;
import org.leadpony.fika.parser.markdown.block.commonmark.BasicBlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Builder of term definition in definition list.
 * 
 * @author leadpony
 */
class TermDefinitionBuilder extends ContainerBlockBuilder {

    private final int indentSize;
    private boolean loose;
    private InputSequence previousInput;
    
    TermDefinitionBuilder(int identSize) {
        this.indentSize = identSize;
    }
    
    boolean isLoose() {
        return loose;
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.TERM_DEFINITIION;
    }
    
    @Override
    public boolean isInterruptible() {
        return true;
    }

    @Override
    protected Result processLine(InputSequence input) {
        if (input.isBlank()) {
            findAndInvokeChildBuilder(input);
            return Result.CONTINUED;
        } else if (lineCount() == 0 || input.hasLeadingSpaces(this.indentSize)) {
            findAndInvokeChildBuilder(input.subSequence(this.indentSize));
            return Result.CONTINUED;
        }
        return Result.NOT_MATCHED;
    }

    @Override
    protected void postprocessLine(InputSequence input) {
        this.previousInput = input;
    }
    
    @Override
    protected Block buildBlock() {
        return getNodeFactory().newDefinition();
    }

    @Override
    public void openChildBuilder(BlockBuilder childBuilder) {
        if (hasCompletedChildren() && this.previousInput.isBlank()) {
            this.loose = true;
        }
        super.openChildBuilder(childBuilder);
    }
}
