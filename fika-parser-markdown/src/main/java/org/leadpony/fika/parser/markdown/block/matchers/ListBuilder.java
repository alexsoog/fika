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
import org.leadpony.fika.core.model.OrderedList;
import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockMatcher;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Block matcher for lists.
 * 
 * @author leadpony
 */
abstract class ListBuilder extends AbstractListBuilder {
    
    private final BlockMatcher matcher;
    private final ListItemBuilder firstItemBuilder;

    protected ListBuilder(BlockMatcher matcher, ListItemBuilder firstItemMatcher) {
        this.matcher = matcher;
        this.firstItemBuilder = firstItemMatcher;
    }
    
    boolean canInterrupt(BlockBuilder builder) {
        BlockType type = builder.blockType();
        if (type == BasicBlockType.PARAGRAPH) {
            return firstItemBuilder.canInterruptParagraph();
        } else if (type == BasicBlockType.LIST) {
            return !isSameTypeAs((ListBuilder)builder);
        }
        return true;
    }
    
    boolean isSameTypeAs(ListBuilder other) {
        return firstItemBuilder.isSameTypeAs(other.firstItemBuilder);
    }
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.LIST;
    }
    
    @Override
    public void bind(Context context) {
        super.bind(context);
        openChildBuilder(this.firstItemBuilder);
    }
    
    @Override
    public Result append(InputSequence input) {
        super.append(input);
        Result result = findAndInvokeChildBuilder(input);
        if (result == Result.NOT_MATCHED) {
            if (!input.isBlank()) {
                return result;
            }
        }
        return Result.CONTINUED;
    }
    
    @Override
    public boolean isInterruptible() {
        return lineNo() > 1;
    }
    
    @Override
    public BlockBuilder interrupt(InputSequence input, BuilderMode mode) {
        if (hasChildBuilder()) {
            ListItemBuilder child = (ListItemBuilder)childBuilder();
            if (input.hasLeadingSpaces(child.indentSize())) {
                return null;
            }
        }
        BlockBuilder matcher = context().finder().findInterruptingBuilder(input, this, mode);
        if (matcher == null) {
            matcher = this.matcher.newInterruptingBuilder(input, this, mode);
        }
        return matcher;
    }
    
    @Override
    protected BlockBuilder findChildBuilder(InputSequence input) {
        BlockBuilder matched = firstItemBuilder.interrupterOfSameType(input);
        if (matched != null) {
            openChildBuilder(matched);
        }
        return matched;
    }
}

/**
 * Block builder for bullet-type lists.
 * 
 * @author leadpony
 */
class BulletListBuilder extends ListBuilder {
    
    BulletListBuilder(BlockMatcher matcher, BulletListItemBuilder itemBuilder) {
        super(matcher, itemBuilder);
    }
    
    @Override
    protected Block buildBlock() {
        return getNodeFactory().newLiskBlock(ListType.UNORDERED);
    }
}

/**
 * Block builder for ordered item lists.
 * 
 * @author leadpony
 */
class OrderedListBuilder extends ListBuilder {
    
    private final int startNumber;
    
    OrderedListBuilder(BlockMatcher matcher, OrderedListItemBuilder itemBuilder) {
        super(matcher, itemBuilder);
        this.startNumber = itemBuilder.number();
    }
    
    @Override
    protected Block buildBlock() {
        OrderedList block = (OrderedList)getNodeFactory().newLiskBlock(ListType.ORDERED);
        block.setStartNumber(startNumber);
        return block;
    }
}
