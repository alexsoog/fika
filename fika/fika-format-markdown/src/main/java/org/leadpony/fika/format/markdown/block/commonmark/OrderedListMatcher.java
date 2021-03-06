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
package org.leadpony.fika.format.markdown.block.commonmark;

import java.util.EnumSet;
import java.util.Set;

import org.leadpony.fika.format.markdown.block.BlockType;
import org.leadpony.fika.format.markdown.common.InputSequence;

/**
 * @author leadpony
 */
public class OrderedListMatcher extends AbstractListMatcher {

    @Override
    public BlockType blockType() {
        return BasicBlockType.ORDERED_LIST;
    }

    @Override
    public Set<? extends BlockType> typesToInterrupt() {
        return EnumSet.of(
                BasicBlockType.PARAGRAPH, 
                BasicBlockType.LINK_DEFINITION,
                BasicBlockType.BULLET_LIST,
                BasicBlockType.ORDERED_LIST);
    }
    
    @Override
    protected ListBuilder newListBuilder(InputSequence input) {
        OrderedListItemBuilder itemBuilder = OrderedListItemBuilder.builder(input, 3);
        if (itemBuilder == null) {
            return null;
        }
        return new OrderedListBuilder(itemBuilder);
    }
}
