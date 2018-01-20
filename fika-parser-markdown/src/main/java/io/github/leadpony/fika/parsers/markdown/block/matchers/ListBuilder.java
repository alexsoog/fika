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

import java.util.List;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.ListItem;
import io.github.leadpony.fika.core.model.ListType;
import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.OrderedList;
import io.github.leadpony.fika.core.model.Paragraph;
import io.github.leadpony.fika.parsers.markdown.block.BlockBuilder;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.MatcherType;
import io.github.leadpony.fika.parsers.markdown.block.ContainerBlockBuilder;
import io.github.leadpony.fika.parsers.markdown.block.BuilderMode;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Block matcher for lists.
 * 
 * @author leadpony
 */
abstract class ListBuilder extends ContainerBlockBuilder {
    
    private final BlockMatcher factory;
    private final ListItemBuilder firstItemBuilder;
    private boolean loose;
    private int lastBlankLineNo;

    protected ListBuilder(BlockMatcher factory, ListItemBuilder firstItemMatcher) {
        this.factory = factory;
        this.firstItemBuilder = firstItemMatcher;
        this.loose = false;
        this.lastBlankLineNo = -1;
    }
    
    boolean isLoose() {
        return loose;
    }
    
    boolean isTight() {
        return !isLoose();
    }
    
    boolean canInterrupt(BlockBuilder builder) {
        MatcherType type = builder.matcherType();
        if (type == BasicMatcherType.PARAGRAPH) {
            return firstItemBuilder.canInterruptParagraph();
        } else if (type == BasicMatcherType.LIST) {
            return !isSameTypeAs((ListBuilder)builder);
        }
        return true;
    }
    
    boolean isSameTypeAs(ListBuilder other) {
        return firstItemBuilder.isSameTypeAs(other.firstItemBuilder);
    }
    
    @Override
    public MatcherType matcherType() {
        return BasicMatcherType.LIST;
    }
    
    @Override
    public void bind(Context context) {
        super.bind(context);
        openChildBuilder(this.firstItemBuilder);
    }
    
    @Override
    public Result match(InputSequence input) {
        boolean isBlank = input.isBlank();
        if (isBlank) {
            this.lastBlankLineNo = lineNo();
        }
        Result result = findAndInvokeChildBuilder(input);
        if (result == Result.NOT_MATCHED) {
            if (!isBlank) {
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
            matcher = this.factory.newInterruptingBuilder(input, this, mode);
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
    
    @Override
    protected void openChildBuilder(BlockBuilder childMatcher) {
        if (lineNo() == lastBlankLineNo + 1) {
            this.loose = true;
        }
        super.openChildBuilder(childMatcher);
    }
    
    @Override
    protected void closeChildBuilder(BlockBuilder childMatcher) {
        ListItemBuilder itemMatcher = (ListItemBuilder)childMatcher;
        if (itemMatcher.isLoose()) {
            this.loose = true;
        }
        super.closeChildBuilder(childMatcher);
    }
    
    @Override
    protected List<Node> childNodes() {
        return buildChildNodes();
    }
    
    private List<Node> buildChildNodes() {
        List<Node> items = super.childNodes();
        if (isTight()) {
            for (Node item: items) {
                tightenListItem((ListItem)item);
            }
        }
        return items;
    }
    
    private void tightenListItem(ListItem item) {
        Node child = item.firstChildNode();
        while (child != null) {
            if (child instanceof Paragraph) {
                Node text = child.firstChildNode();
                item.replaceChild(text, child);
                child = text;
            }
            child = child.nextNode();
        }
    }
}

/**
 * Block builder for bullet-type lists.
 * 
 * @author leadpony
 */
class BulletListBuilder extends ListBuilder {
    
    BulletListBuilder(BlockMatcher factory, BulletListItemBuilder itemBuilder) {
        super(factory, itemBuilder);
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
    
    OrderedListBuilder(BlockMatcher factory, OrderedListItemBuilder itemBuilder) {
        super(factory, itemBuilder);
        this.startNumber = itemBuilder.number();
    }
    
    @Override
    protected Block buildBlock() {
        OrderedList block = (OrderedList)getNodeFactory().newLiskBlock(ListType.ORDERED);
        block.setStartNumber(startNumber);
        return block;
    }
}
