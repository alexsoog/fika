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
package org.leadpony.fika.parser.markdown.block.commonmark;

import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.core.model.ListBlock;
import org.leadpony.fika.core.model.ListType;
import org.leadpony.fika.core.model.OrderedList;
import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockContext;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.ContainerBlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Block matcher for lists.
 * 
 * @author leadpony
 */
abstract class ListBuilder extends ContainerBlockBuilder {
    
    private final ListItemBuilder firstItemBuilder;

    private boolean loose;
    private InputSequence previousInput;
   
    protected ListBuilder(ListItemBuilder firstItemMatcher) {
        this.firstItemBuilder = firstItemMatcher;
    }
    
    boolean isLoose() {
        return loose;
    }
    
    boolean canInterrupt(BlockBuilder builder) {
        BlockType type = builder.blockType();
        if (type == BasicBlockType.PARAGRAPH) {
            return firstItemBuilder.canInterruptParagraph();
        } else if (type == BasicBlockType.BULLET_LIST || type == BasicBlockType.ORDERED_LIST) {
            return !isSameTypeAs((ListBuilder)builder);
        }
        return true;
    }
    
    boolean isSameTypeAs(ListBuilder other) {
        return firstItemBuilder.isSameTypeAs(other.firstItemBuilder);
    }
    
    @Override
    public void bind(BlockContext context) {
        super.bind(context);
        this.firstItemBuilder.bind(context);
    }
    
    @Override
    public void openChildBuilder(BlockBuilder childBuilder) {
        if (hasCompletedChildren() && previousInput.isBlank()) {
            this.loose = true;
        }
        super.openChildBuilder(childBuilder);
    }
    
    @Override
    public void closeChildBuilder(BlockBuilder childBuilder) {
        if (childBuilder instanceof ListItemBuilder) {
            ListItemBuilder itemBuilder = (ListItemBuilder)childBuilder;
            if (itemBuilder.isLoose()) {
                this.loose = true;
            }
        }
        super.closeChildBuilder(childBuilder);
    }
    
    @Override
    protected Result processLine(InputSequence input) {
        if (lineCount() == 0) {
            openChildBuilder(this.firstItemBuilder);
        }
        Result result = findAndInvokeChildBuilder(input);
        if (result == Result.NOT_MATCHED) {
            if (!input.isBlank()) {
                return result;
            }
        }
        return Result.CONTINUED;
    }
    
    @Override
    protected void postprocessLine(InputSequence input) {
        this.previousInput = input;
    }
    
    @Override
    public boolean isInterruptible() {
        return true;
    }
    
    @Override
    public BlockBuilder interrupt(InputSequence input, BuilderMode mode) {
        if (hasChildBuilder()) {
            ListItemBuilder child = (ListItemBuilder)childBuilder();
            if (input.hasLeadingSpaces(child.indentSize())) {
                return null;
            }
        }
        return super.interrupt(input, mode);
    }
    
    @Override
    protected BlockBuilder findChildBuilder(InputSequence input) {
        return firstItemBuilder.interrupterOfSameType(input);
    }
}

/**
 * Block builder for bullet-type lists.
 * 
 * @author leadpony
 */
class BulletListBuilder extends ListBuilder {
    
    BulletListBuilder(BulletListItemBuilder itemBuilder) {
        super(itemBuilder);
    }
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.BULLET_LIST;
    }

    @Override
    protected Block buildBlock() {
        ListBlock block = getNodeFactory().newListBlock(ListType.UNORDERED);
        block.setTight(!isLoose());
        return block;
    }
}

/**
 * Block builder for ordered item lists.
 * 
 * @author leadpony
 */
class OrderedListBuilder extends ListBuilder {
    
    private final int startNumber;
    
    OrderedListBuilder(OrderedListItemBuilder itemBuilder) {
        super(itemBuilder);
        this.startNumber = itemBuilder.number();
    }
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.ORDERED_LIST;
    }

    @Override
    protected Block buildBlock() {
        OrderedList block = (OrderedList)getNodeFactory().newListBlock(ListType.ORDERED);
        block.setTight(!isLoose());
        block.setStartNumber(startNumber);
        return block;
    }
}
