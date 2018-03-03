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
import java.util.regex.Pattern;

import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockMatcher;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Matcher implementation for setext heading.
 * 
 * @author leadpony
 */
public class SetextHeadingMatcher implements BlockMatcher {

    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("\\u0020{0,3}(=+|-{2,})\\u0020*");
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.SETEXT_HEADING;
    }

    @Override
    public Set<? extends BlockType> typesToReplace() {
        return EnumSet.of(BasicBlockType.PARAGRAPH);
    }
    
    /**
     * {@inheritDoc}
     * This matcher only interrupts paragraphs.
     */
    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        return null;
    }

    @Override
    public BlockBuilder newReplacingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        if (mode == BuilderMode.LAZY_CONTINUATION) {
            return null;
        }
        if (!UNDERLINE_PATTERN.matcher(input).matches()) {
            return null;
        }
        int firstIndex = input.countLeadingSpaces(0, 3);
        if (firstIndex >= input.length()) {
            return null;
        }
        int level = headingLevel(input.charAt(firstIndex));
        return new SetextHeadingBuilder((ParagraphBuilder)current, level);
    }
    
    private static int headingLevel(char c) {
        return (c == '=') ? 1 : 2;
    }
}
