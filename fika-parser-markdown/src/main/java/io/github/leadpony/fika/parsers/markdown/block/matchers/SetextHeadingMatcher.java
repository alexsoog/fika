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
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.block.AbstractBlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockTrait;
import io.github.leadpony.fika.parsers.markdown.block.MatcherMode;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * @author leadpony
 *
 */
class SetextHeadingMatcher extends AbstractBlockMatcher {
    
    private final int level;
    private final String title;
    
    SetextHeadingMatcher(int level, String title) {
        this.level = level;
        this.title = title;
    }

    @Override
    public BlockTrait blockTrait() {
        return BlockType.SETEXT_HEADING;
    }

    @Override
    public Result match(InputSequence input) {
        return Result.COMPLETED;
    }

    @Override
    protected Block buildBlock() {
        Block heading = getNodeFactory().newHeading(this.level);
        Text text = getNodeFactory().newText(this.title);
        context().addInline(text);
        heading.appendChild(text);
        return heading;
    }
}

class SetextHeadingMatcherFactory implements BlockMatcherFactory {

    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("\\u0020{0,3}(=+|-{2,})\\u0020*");
    
    @Override
    public BlockTrait blockTrait() {
        return BlockType.SETEXT_HEADING;
    }

    @Override
    public Set<? extends BlockTrait> interruptible() {
        return EnumSet.of(BlockType.PARAGRAPH);
    }
    
    /**
     * {@inheritDoc}
     * This matcher only interrupts paragraphs.
     */
    @Override
    public BlockMatcher newMatcher(InputSequence input) {
        return null;
    }

    @Override
    public BlockMatcher newInterrupter(InputSequence input, BlockMatcher current, MatcherMode mode) {
        if (mode == MatcherMode.LAZY_CONTINUATION) {
            return null;
        }
        if (!UNDERLINE_PATTERN.matcher(input).matches()) {
            return null;
        }
        int firstIndex = input.countLeadingSpaces(0, 3);
        if (firstIndex >= input.length()) {
            return null;
        }
        char c = input.charAt(firstIndex);
        int level = (c == '=') ? 1 : 2;
        ParagraphMatcher paragraphMatcher = (ParagraphMatcher)current;
        String content = paragraphMatcher.buildContent(0);
        return new SetextHeadingMatcher(level, content);
    }
}
