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
package org.leadpony.fika.parser.markdown.block.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockMatcher;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.commonmark.BasicBlockType;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Matcher implementation for admonition.
 * 
 * @author leadpony
 */
public class AdmonitionMatcher implements BlockMatcher {

    private static final Pattern TYPE_PATTERN = Pattern.compile(
            "\\s*(\\S+)" +
            "(" +
            "\\s*|" +
            "\\s+\"((\\\\\"|[^\"])*)\"\\s*" + 
            ")"
            );
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.ADMONITION;
    }

    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        final int spaces = input.countLeadingSpaces(0, 3);
        if (spaces >= 4) {
            return null;
        }
        final int count = input.countLeading('!', spaces, spaces + 3);
        if (count != 3) {
            return null;
        }
        InputSequence content = input.subSequence(spaces + count);
        Matcher m = TYPE_PATTERN.matcher(content);
        if (!m.matches()) {
            return null;
        }
        String type = m.group(1);
        String title = m.group(3);
        return new AdmonitionBuilder(type, title);
    }

    @Override
    public BlockBuilder newInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        return newBuilder(input);
    }
}
