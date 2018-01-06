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

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.ListItem;
import io.github.leadpony.fika.core.model.ListType;
import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.OrderedList;
import io.github.leadpony.fika.core.model.Paragraph;
import io.github.leadpony.fika.parsers.markdown.block.BasicBlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.block.ContainerBlockMatcher;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Block matcher for lists.
 * 
 * @author leadpony
 */
abstract class ListMatcher extends ContainerBlockMatcher {
    
    private final BlockMatcherFactory factory;
    private final ListItemMatcher firstItemMatcher;
    private boolean loose;
    private int lastBlankLineNo;

    protected ListMatcher(BlockMatcherFactory factory, ListItemMatcher firstItemMatcher) {
        this.factory = factory;
        this.firstItemMatcher = firstItemMatcher;
        this.loose = false;
        this.lastBlankLineNo = -1;
    }
    
    boolean isLoose() {
        return loose;
    }
    
    boolean isTight() {
        return !isLoose();
    }
    
    boolean canInterrupt(BlockMatcher matcher) {
        BlockType type = matcher.blockType();
        if (type == BasicBlockType.PARAGRAPH) {
            return firstItemMatcher.canInterruptParagraph();
        } else if (type == BasicBlockType.LIST) {
            return !isSameTypeAs((ListMatcher)matcher);
        }
        return true;
    }
    
    boolean isSameTypeAs(ListMatcher other) {
        return firstItemMatcher.isSameTypeAs(other.firstItemMatcher);
    }
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.LIST;
    }
    
    @Override
    public void bind(Context context) {
        super.bind(context);
        openChildMatcher(this.firstItemMatcher);
    }
    
    @Override
    public Result match(InputSequence input) {
        boolean isBlank = input.isBlank();
        if (isBlank) {
            this.lastBlankLineNo = lineNo();
        }
        Result result = findAndInvokeChildMatcher(input);
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
    public BlockMatcher interrupt(InputSequence input) {
        if (hasChildMatcher()) {
            ListItemMatcher child = (ListItemMatcher)childMatcher();
            if (input.hasLeadingSpaces(child.indentSize())) {
                return null;
            }
        }
        BlockMatcher matcher = context().findInterruptingMatcher(input, this);
        if (matcher == null) {
            matcher = this.factory.newInterrupter(input, this);
        }
        return matcher;
    }
    
    @Override
    protected BlockMatcher findChildMatcher(InputSequence input) {
        BlockMatcher matched = firstItemMatcher.interrupterOfSameType(input);
        if (matched != null) {
            openChildMatcher(matched);
        }
        return matched;
    }
    
    @Override
    protected void openChildMatcher(BlockMatcher childMatcher) {
        if (lineNo() == lastBlankLineNo + 1) {
            this.loose = true;
        }
        super.openChildMatcher(childMatcher);
    }
    
    @Override
    protected void closeChildMatcher(BlockMatcher childMatcher) {
        ListItemMatcher itemMatcher = (ListItemMatcher)childMatcher;
        if (itemMatcher.isLoose()) {
            this.loose = true;
        }
        super.closeChildMatcher(childMatcher);
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
 * Block matcher for bullet-type lists.
 * 
 * @author leadpony
 */
class BulletListMatcher extends ListMatcher {
    
    BulletListMatcher(BlockMatcherFactory factory, BulletListItemMatcher itemMatcher) {
        super(factory, itemMatcher);
    }
    
    @Override
    protected Block buildBlock() {
        return nodeFactory().newLiskBlock(ListType.UNORDERED);
    }
}

/**
 * Block matcher for ordered item lists.
 * 
 * @author leadpony
 */
class OrderedListMatcher extends ListMatcher {
    
    private final int startNumber;
    
    OrderedListMatcher(BlockMatcherFactory factory, OrderedListItemMatcher itemMatcher) {
        super(factory, itemMatcher);
        this.startNumber = itemMatcher.number();
    }
    
    @Override
    protected Block buildBlock() {
        OrderedList block = (OrderedList)nodeFactory().newLiskBlock(ListType.ORDERED);
        block.setStartNumber(startNumber);
        return block;
    }
}

/**
 * @author leadpony
 */
class ListMatcherFactory implements BlockMatcherFactory {
    
    ListMatcherFactory() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.LIST;
    }
  
    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(BasicBlockType.PARAGRAPH);
    }
    
    @Override
    public BlockMatcher newMatcher(InputSequence input) {
        return newListMatcher(input);
    }
    
    @Override
    public BlockMatcher newInterrupter(InputSequence input, BlockMatcher current) {
        ListMatcher matcher = newListMatcher(input);
        if (matcher == null) {
            return null;
        }
        return matcher.canInterrupt(current) ? matcher : null;
    }
    
    private ListMatcher newListMatcher(InputSequence input) {
        ListMatcher matcher = null;
        matcher = newBulletListMatcher(input);
        if (matcher == null) {
            matcher = newOrderedListMatcher(input);
        }
        return matcher;
    }

    private BulletListMatcher newBulletListMatcher(InputSequence input) {
        BulletListItemMatcher itemMatcher = BulletListItemMatcher.matcher(input, 3);
        return (itemMatcher != null) ? new BulletListMatcher(this, itemMatcher) : null;
    }

    private OrderedListMatcher newOrderedListMatcher(InputSequence input) {
        OrderedListItemMatcher itemMatcher = OrderedListItemMatcher.matcher(input, 3);
        return (itemMatcher != null) ? new OrderedListMatcher(this, itemMatcher) : null;
    }
}
