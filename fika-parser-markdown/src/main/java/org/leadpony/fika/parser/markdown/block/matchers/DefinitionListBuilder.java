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
import org.leadpony.fika.core.model.ListType;
import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockBuilder.Result;
import org.leadpony.fika.parser.markdown.block.BlockContext;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Builder of definition list.
 * 
 * @author leadpony
 */
class DefinitionListBuilder extends AbstractListBuilder {
  
    private static final int INDENT_SIZE = 4;
    
    private final BlockBuilder termBuilder;
    private final BlockBuilder definitionBuilder;
    
    DefinitionListBuilder(BlockBuilder definitionBuilder) {
        this(null, definitionBuilder);
    }

    DefinitionListBuilder(BlockBuilder termBuilder, BlockBuilder definitionBuilder) {
        this.termBuilder = termBuilder;
        this.definitionBuilder = definitionBuilder;
    }
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.DEFINITION_LIST;
    }
    
    @Override
    public void bind(BlockContext context) {
        super.bind(context);
        if (this.termBuilder != null) {
            this.termBuilder.bind(context);
        }
        this.definitionBuilder.bind(context);
    }

    @Override
    public Result processLine(InputSequence input) {
        super.processLine(input);
        if (lineNo() == 1) {
            openFirstChildBuilder();
            findAndInvokeChildBuilder(input.subSequence(INDENT_SIZE));
            return Result.CONTINUED;
        } else if (input.hasLeadingSpaces(INDENT_SIZE)) {
            findAndInvokeChildBuilder(input.subSequence(INDENT_SIZE));
            return Result.CONTINUED;
        } else if (input.isBlank()) {
            findAndInvokeChildBuilder(input);
            return Result.CONTINUED;
        } else {
            return Result.NOT_MATCHED;
        }
    }
    
    @Override
    protected Block buildBlock() {
        return getNodeFactory().newLiskBlock(ListType.DEFINITION);
    }
    
    private void openFirstChildBuilder() {
        if (this.termBuilder != null) {
            openChildBuilder(this.termBuilder);
        }
        openChildBuilder(this.definitionBuilder);
    }
}

/**
 * Builder of term.
 * 
 * @author leadpony
 */
class TermBuilder extends AbstractListItemBuilder {
    
    private String term;
    
    TermBuilder(String term) {
        this.term = term;
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.TERM;
    }

    @Override
    public Result processLine(InputSequence input) {
        if (input.isBlank()) {
        }
        return super.processLine(input);
    }
    
    @Override
    protected Block buildBlock() {
        Block termBlock = getNodeFactory().newTerm();
        termBlock.appendChild(getNodeFactory().newText(this.term));
        return termBlock;
    }
}

/**
 * Builder of term definition.
 * 
 * @author leadpony
 */
class DefinitionBuilder extends AbstractListItemBuilder {
    
    private final int indentSize;
    
    static DefinitionBuilder builder(InputSequence input) {
        if (input.isBlank() || input.charAt(0) != ':') {
            return null;
        }
        int spaces = input.countLeadingSpaces(1, 4);
        if (spaces < 1) {
            return null;
        }
        return new DefinitionBuilder(1 + spaces);
    }
    
    private DefinitionBuilder(int indentSize) {
        this.indentSize = indentSize;
    }
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.DEFINITIION;
    }
    
    @Override
    public Result processLine(InputSequence input) {
        super.processLine(input);
        if (input.isBlank()) {
            findAndInvokeChildBuilder(input);
        } else if (input.hasLeadingSpaces(this.indentSize)) {
            input = input.subSequence(this.indentSize);
            findAndInvokeChildBuilder(input);
        } else {
            return Result.NOT_MATCHED;
        }
        return Result.CONTINUED;
    }

    @Override
    protected Block buildBlock() {
        return getNodeFactory().newDefinition();
    }
}
