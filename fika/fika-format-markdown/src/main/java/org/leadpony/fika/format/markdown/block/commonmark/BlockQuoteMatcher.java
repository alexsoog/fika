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

import org.leadpony.fika.format.markdown.block.BlockBuilder;
import org.leadpony.fika.format.markdown.block.BlockMatcher;
import org.leadpony.fika.format.markdown.block.BlockType;
import org.leadpony.fika.format.markdown.block.BuilderMode;
import org.leadpony.fika.format.markdown.common.InputSequence;

/**
 * Matcher implementation for block quote.
 * 
 * @author leadpony
 */
public class BlockQuoteMatcher implements BlockMatcher {
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.BLOCK_QUOTE;
    }

    @Override
    public Set<? extends BlockType> typesToInterrupt() {
        return EnumSet.of(BasicBlockType.PARAGRAPH, BasicBlockType.LINK_DEFINITION);
    }
    
    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        if (BlockQuoteBuilder.BLOCK_QUOTE_MARKER.matcher(input).find()) {
            return new BlockQuoteBuilder();
        } else {
            return null;
        }
    }

    @Override
    public BlockBuilder newInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        return newBuilder(input);
    }
}

