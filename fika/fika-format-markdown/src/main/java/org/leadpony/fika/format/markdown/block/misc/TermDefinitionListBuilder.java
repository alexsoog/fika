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
package org.leadpony.fika.format.markdown.block.misc;

import java.util.List;

import org.leadpony.fika.format.markdown.block.BlockBuilder;
import org.leadpony.fika.format.markdown.block.BlockType;
import org.leadpony.fika.format.markdown.block.ContainerBlockBuilder;
import org.leadpony.fika.format.markdown.block.commonmark.BasicBlockType;
import org.leadpony.fika.format.markdown.block.commonmark.ParagraphBuilder;
import org.leadpony.fika.format.markdown.common.InputSequence;
import org.leadpony.fika.parser.model.Block;
import org.leadpony.fika.parser.model.Node;

/**
 * Builder of term definition list.
 *
 * @author leadpony
 */
class TermDefinitionListBuilder extends ContainerBlockBuilder {

    private boolean loose;

    @Override
    public BlockType blockType() {
        return BasicBlockType.TERM_DEFINITION_LIST;
    }

    @Override
    public void closeChildBuilder(BlockBuilder childBuilder) {
        if (childBuilder instanceof TermDefinitionBuilder) {
            TermDefinitionBuilder definitionBuilder = (TermDefinitionBuilder)childBuilder;
            if (definitionBuilder.isLoose()) {
                this.loose = true;
            }
        }
        super.closeChildBuilder(childBuilder);
    }

    @Override
    public void build(List<Node> nodes) {
        BlockBuilder childBuilder = childBuilder();
        if (childBuilder == null || childBuilder.blockType() == BasicBlockType.TERM) {
            buildExcludingLastTerm(nodes);
            if (childBuilder != null) {
                TermBuilder termBuilder = (TermBuilder)childBuilder;
                termBuilder.internalBuilder().build(nodes);
            }
        } else {
            super.build(nodes);
        }
    }

    @Override
    protected Block buildBlock() {
        return getNodeFactory().createDefinitionList(!loose);
    }

    @Override
    protected BlockBuilder findChildBuilder(InputSequence input) {
        BlockBuilder found = super.findChildBuilder(input);
        if (found == null || found.blockType() != BasicBlockType.PARAGRAPH) {
            return null;
        }
        TermBuilder child = new TermBuilder((ParagraphBuilder)found);
        child.bind(context());
        return child;
    }

    @Override
    protected Result invokeChildBuilder(InputSequence input) {
        BlockBuilder child = childBuilder();
        if (child.blockType() == BasicBlockType.TERM) {
            return invokeTermBuiler(child, input);
        } else {
            return super.invokeChildBuilder(input);
        }
    }

    private Result invokeTermBuiler(BlockBuilder childBuilder, InputSequence input) {
        Result result = childBuilder.appendLine(input);
        if (result == Result.CONTINUED) {
            return result;
        } else if (result == Result.INTERRUPTED) {
            BlockBuilder successor = childBuilder.successor();
            closeChildBuilder(childBuilder);
            openChildBuilder(successor);
            return invokeChildBuilder(input);
        } else {
            resetChildBuilder();
            setSuccessor(childBuilder.successor());
            return Result.INTERRUPTED;
        }
    }

    private void buildExcludingLastTerm(List<Node> nodes) {
        Block block = buildBlock();
        block.appendChildren(collectChildNodes());
        nodes.add(block);
    }
}
