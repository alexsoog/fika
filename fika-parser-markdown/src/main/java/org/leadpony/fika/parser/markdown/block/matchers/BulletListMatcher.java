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

import java.util.EnumSet;
import java.util.Set;

import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * @author leadpony
 */
public class BulletListMatcher extends AbstractListMatcher {

    @Override
    public BlockType blockType() {
        return BasicBlockType.BULLET_LIST;
    }
    
    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(
                BasicBlockType.PARAGRAPH, 
                BasicBlockType.LINK_DEFINITION,
                BasicBlockType.BULLET_LIST,
                BasicBlockType.ORDERED_LIST);
    }
    
    @Override
    protected ListBuilder newListBuilder(InputSequence input) {
        BulletListItemBuilder itemBuilder = BulletListItemBuilder.builder(input, 3);
        if (itemBuilder == null) {
            return null;
        }
        return new BulletListBuilder(itemBuilder);
    }
}
