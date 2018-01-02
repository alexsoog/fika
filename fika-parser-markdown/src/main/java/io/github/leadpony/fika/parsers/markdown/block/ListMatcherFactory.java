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
package io.github.leadpony.fika.parsers.markdown.block;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import io.github.leadpony.fika.core.nodes.Block;
import io.github.leadpony.fika.core.nodes.ListItem;
import io.github.leadpony.fika.core.nodes.ListType;
import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.nodes.OrderedList;
import io.github.leadpony.fika.core.nodes.Paragraph;

/**
 * 
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
    public BlockMatcher newMatcher(BlockInputSequence content) {
        return newListMatcher(content);
    }
    
    @Override
    public BlockMatcher newInterrupter(BlockInputSequence content, BlockMatcher current) {
        ListMatcher matcher = newListMatcher(content);
        if (matcher == null) {
            return null;
        }
        return matcher.canInterrupt(current) ? matcher : null;
    }
    
    private ListMatcher newListMatcher(BlockInputSequence content) {
        ListMatcher matcher = null;
        matcher = newBulletListMatcher(content);
        if (matcher == null) {
            matcher = newOrderedListMatcher(content);
        }
        return matcher;
    }

    /**
     * Block matcher for lists.
     * 
     * @author leadpony
     */
    private abstract class ListMatcher extends ContainerBlockMatcher {
        
        private final ListItemMatcher firstItemMatcher;
        private boolean loose;
        private int lastBlankLineNo;
    
        private ListMatcher(ListItemMatcher firstItemMatcher) {
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
        public Result match(BlockInputSequence content) {
            boolean isBlank = content.isBlank();
            if (isBlank) {
                this.lastBlankLineNo = lineNo();
            }
            Result result = findAndInvokeChildMatcher(content);
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
        public BlockMatcher interrupt(BlockInputSequence content) {
            if (hasChildMatcher()) {
                ListItemMatcher child = (ListItemMatcher)childMatcher();
                if (content.hasLeadingSpaces(child.indentSize())) {
                    return null;
                }
            }
            BlockMatcher matcher = context().findInterruptingMatcher(content, this);
            if (matcher == null) {
                matcher = ListMatcherFactory.this.newInterrupter(content, this);
            }
            return matcher;
        }
        
        @Override
        protected BlockMatcher findChildMatcher(BlockInputSequence content) {
            BlockMatcher matched = firstItemMatcher.interrupterOfSameType(content);
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
    private class BulletListMatcher extends ListMatcher {
        
        private BulletListMatcher(BulletListItemMatcher itemMatcher) {
            super(itemMatcher);
        }
        
        @Override
        protected Block buildBlock() {
            return nodeFactory().newLiskBlock(ListType.UNORDERED);
        }
    }

    private BulletListMatcher newBulletListMatcher(BlockInputSequence content) {
        BulletListItemMatcher itemMatcher = BulletListItemMatcher.matcher(content, 3);
        return (itemMatcher != null) ? new BulletListMatcher(itemMatcher) : null;
    }

    /**
     * Block matcher for ordered item lists.
     * 
     * @author leadpony
     */
    private class OrderedListMatcher extends ListMatcher {
        
        private final int startNumber;
        
        private OrderedListMatcher(OrderedListItemMatcher itemMatcher) {
            super(itemMatcher);
            this.startNumber = itemMatcher.number();
        }
        
        @Override
        protected Block buildBlock() {
            OrderedList block = (OrderedList)nodeFactory().newLiskBlock(ListType.ORDERED);
            block.setStartNumber(startNumber);
            return block;
        }
    }

    private OrderedListMatcher newOrderedListMatcher(BlockInputSequence content) {
        OrderedListItemMatcher itemMatcher = OrderedListItemMatcher.matcher(content, 3);
        return (itemMatcher != null) ? new OrderedListMatcher(itemMatcher) : null;
    }
}
