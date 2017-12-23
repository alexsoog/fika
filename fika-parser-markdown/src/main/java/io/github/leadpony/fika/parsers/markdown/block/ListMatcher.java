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

import java.util.List;

import io.github.leadpony.fika.core.nodes.ListItem;
import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.nodes.Paragraph;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleOrderedList;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleUnorderedList;

/**
 * Block matcher for lists.
 * 
 * @author leadpony
 */
public abstract class ListMatcher extends ContainerBlockMatcher {
    
    private final ListItemMatcher firstItemMatcher;
    private boolean loose;
    private int lastBlankLineNo;

    static Factory factory() {
        return Factory.instance;
    }
    
    protected ListMatcher(ListItemMatcher firstItemMatcher) {
        this.firstItemMatcher = firstItemMatcher;
        this.loose = false;
        this.lastBlankLineNo = -1;
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
    public Result match(Content content) {
        final boolean isBlank = content.isBlank();
        if (isBlank) {
            this.lastBlankLineNo = lineNo();
        }
        final Result result = super.match(content);
        if (result == Result.NOT_MATCHED) {
            if (!isBlank) {
                return result;
            }
        }
        return Result.CONTINUED;
    }
    
    @Override
    protected BlockMatcher findChildMatcher(Content content) {
        BlockMatcher matched = firstItemMatcher.matcherOfSameType(content);
        if (matched != null) {
            super.openChildMatcher(matched);
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
        if (!loose) {
            for (Node item: items) {
                tightenListItem((ListItem)item);
            }
        }
        return items;
    }
    
    private void tightenListItem(ListItem item) {
        List<Node> children = item.childNodes();
        for (int i = 0; i < children.size(); ++i) {
            Node child = children.get(i);
            if (child instanceof Paragraph) {
                Node text = child.childNodes().get(0);
                children.set(i, text);
            }
        }
    }
    
    static class Factory implements BlockMatcher.Factory {
        
        private static final Factory instance = new Factory();
        
        @Override
        public BlockType blockType() {
            return BasicBlockType.LIST;
        }

        @Override
        public BlockMatcher newMatcher(Content content) {
            ListMatcher matcher = null;
            matcher = BulletListMatcher.matcher(content);
            if (matcher == null) {
                matcher = OrderedListMatcher.matcher(content);
            }
            return matcher;
        }
    }
}

class BulletListMatcher extends ListMatcher {
    
    static BulletListMatcher matcher(Content content) {
        BulletListItemMatcher itemMatcher = BulletListItemMatcher.matcher(content);
        if (itemMatcher == null) {
            return null;
        }
        return new BulletListMatcher(itemMatcher);
    }
    
    private BulletListMatcher(BulletListItemMatcher itemMatcher) {
        super(itemMatcher);
    }
    
    @Override
    protected Node buildNode() {
        return new SimpleUnorderedList();
    }
}

class OrderedListMatcher extends ListMatcher {
    
    private final int startNumber;
    
    static OrderedListMatcher matcher(Content content) {
        OrderedListItemMatcher itemMatcher = OrderedListItemMatcher.matcher(content);
        if (itemMatcher == null) {
            return null;
        }
        return new OrderedListMatcher(itemMatcher);
    }
    
    private OrderedListMatcher(OrderedListItemMatcher itemMatcher) {
        super(itemMatcher);
        this.startNumber = itemMatcher.number();
    }
    
    @Override
    protected Node buildNode() {
        return new SimpleOrderedList(startNumber);
    }
}
