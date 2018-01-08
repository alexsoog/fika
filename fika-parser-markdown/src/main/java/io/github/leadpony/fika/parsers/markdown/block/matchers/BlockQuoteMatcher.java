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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.BlockQuote;
import io.github.leadpony.fika.parsers.markdown.block.BasicBlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.block.ContainerBlockMatcher;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * @author leadpony
 */
class BlockQuoteMatcher extends ContainerBlockMatcher {
    
    static final Pattern BLOCK_QUOTE_MARKER = Pattern.compile("^\\u0020{0,3}>\\u0020?");

    BlockQuoteMatcher() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.BLOCK_QUOTE;
    }
   
    @Override
    public Result match(InputSequence input) {
        Matcher m = BLOCK_QUOTE_MARKER.matcher(input);
        if (m.find()) {
            int skip = m.group(0).length();
            findAndInvokeChildMatcher(input.subSequence(skip));
            return Result.CONTINUED;
        } else {
            return matchLazyContinuationLine(input);
        }
    }

    @Override
    protected BlockQuote buildBlock() {
        return getNodeFactory().newBlockQuote();
    }
}

/**
 * @author leadpony
 */
class BlockQuoteMatcherFactory implements BlockMatcherFactory {
    
    BlockQuoteMatcherFactory() {
    }
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.BLOCK_QUOTE;
    }

    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(BasicBlockType.PARAGRAPH, BasicBlockType.LINK_DEFINITION);
    }
    
    @Override
    public BlockMatcher newMatcher(InputSequence input) {
        if (BlockQuoteMatcher.BLOCK_QUOTE_MARKER.matcher(input).find()) {
            return new BlockQuoteMatcher();
        } else {
            return null;
        }
    }

    @Override
    public BlockMatcher newInterrupter(InputSequence input, BlockMatcher current) {
        return newMatcher(input);
    }
}

