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
import java.util.Set;

import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockBuilder;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockTrait;
import io.github.leadpony.fika.parsers.markdown.block.BuilderMode;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * @author leadpony
 */
public class ListMatcher implements BlockMatcher {
    
    @Override
    public BlockTrait blockTrait() {
        return BlockType.LIST;
    }
  
    @Override
    public Set<? extends BlockTrait> interruptible() {
        return EnumSet.of(BlockType.PARAGRAPH, BlockType.LINK_DEFINITION);
    }
    
    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        return newListBuilder(input);
    }
    
    @Override
    public BlockBuilder newInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        ListBuilder matcher = newListBuilder(input);
        if (matcher == null) {
            return null;
        }
        return matcher.canInterrupt(current) ? matcher : null;
    }
    
    private ListBuilder newListBuilder(InputSequence input) {
        ListBuilder builder = newBulletListBuilder(input);
        if (builder == null) {
            builder = newOrderedListBuilder(input);
        }
        return builder;
    }

    private BulletListBuilder newBulletListBuilder(InputSequence input) {
        BulletListItemBuilder itemMatcher = BulletListItemBuilder.matcher(input, 3);
        return (itemMatcher != null) ? new BulletListBuilder(this, itemMatcher) : null;
    }

    private OrderedListBuilder newOrderedListBuilder(InputSequence input) {
        OrderedListItemBuilder itemMatcher = OrderedListItemBuilder.matcher(input, 3);
        return (itemMatcher != null) ? new OrderedListBuilder(this, itemMatcher) : null;
    }
}
