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
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Builder of definition list.
 * 
 * @author leadpony
 */
class DefinitionListBuilder extends AbstractListBuilder {
  
    private static final int INDENT_SIZE = 4;
    
    private String term;
    
    DefinitionListBuilder() {
        this.term = null;
    }

    DefinitionListBuilder(String term) {
        this.term = term;
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.DEFINITION_LIST;
    }

    @Override
    public Result append(InputSequence input) {
        super.append(input);
        if (lineNo() == 1) {
            openChildBuilder();
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
    
    private void openChildBuilder() {
        if (this.term != null) {
            Node termNode = getNodeFactory().newTerm();
            termNode.appendChild(getNodeFactory().newText(this.term));
            addChildNode(termNode);
        }
        openChildBuilder(new DefinitionBuilder());
    }
}

/**
 * Builder of term definition.
 * 
 * @author leadpony
 */
class DefinitionBuilder extends AbstractListItemBuilder {
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.DEFINITIION;
    }
    
    @Override
    public Result append(InputSequence input) {
        super.append(input);
        findAndInvokeChildBuilder(input);
        return Result.CONTINUED;
    }

    @Override
    protected Block buildBlock() {
        return getNodeFactory().newDefinition();
    }
}
