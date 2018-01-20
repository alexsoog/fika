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

import io.github.leadpony.fika.parsers.markdown.block.BlockBuilder;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.MatcherType;
import io.github.leadpony.fika.parsers.markdown.block.BuilderMode;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * @author leadpony
 */
public class BlockQuoteMatcher implements BlockMatcher {
    
    @Override
    public MatcherType matcherType() {
        return BasicMatcherType.BLOCK_QUOTE;
    }

    @Override
    public Set<? extends MatcherType> interruptible() {
        return EnumSet.of(BasicMatcherType.PARAGRAPH, BasicMatcherType.LINK_DEFINITION);
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

