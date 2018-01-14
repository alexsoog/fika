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

import io.github.leadpony.fika.core.model.Heading;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.block.AbstractBlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BasicBlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Matcher for ATX headings.
 * 
 * @author leadpony
 */
class AtxHeadingMatcher extends AbstractBlockMatcher {

    private final int level;
    private final String title;
  
    AtxHeadingMatcher(int level, String title) {
        this.level = level;
        this.title = title;
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.ATX_HEADING;
    }
    
    @Override
    public Result match(InputSequence input) {
        return Result.COMPLETED;
    }
    
    @Override
    protected Heading buildBlock() {
        Heading block = getNodeFactory().newHeading(this.level);
        Text text = getNodeFactory().newText(this.title);
        block.appendChild(text);
        context().addInline(text);
        return block;
    }
}

/**
 * @author leadpony
 */
class AtxHeadingMatcherFactory implements BlockMatcherFactory {
    
    private static final int MAX_LEVEL = 6;

    AtxHeadingMatcherFactory() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.ATX_HEADING;
    }

    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(BasicBlockType.PARAGRAPH, BasicBlockType.LINK_DEFINITION);
    }
    
    @Override
    public BlockMatcher newMatcher(InputSequence input) {
        int i = input.countLeadingSpaces(0, 3);
        int level = 0;
        for (; i < input.length(); i++) {
            if (input.charAt(i) != '#') {
                break;
            }
            if (++level > MAX_LEVEL) {
                return null;
            }
        }
        if (level == 0) {
            return null;
        }
        if (i < input.length()) {
            // Checks the first letter after #.
            char c = input.charAt(i++);
            if (c != '\u0020' && c != '\t') {
                return null;
            }
        }
        return new AtxHeadingMatcher(level, extractTitle(input.subSequence(i)));
    }

    @Override
    public BlockMatcher newInterrupter(InputSequence input, BlockMatcher current) {
        return newMatcher(input);
    }
    
    private static String extractTitle(InputSequence input) {
        input = input.trimSpaces();
        input = trimClosingSequenceOfHash(input);
        return input.trimSpaces().toSourceString();
    }
    
    private static InputSequence trimClosingSequenceOfHash(InputSequence input) {
        if (input.length() == 0) {
            return input;
        }
        int i = input.length() - 1;
        char c = input.charAt(i);
        if (c != '#') {
            return input;
        }
        while (i >= 0) {
            c = input.charAt(i);
            if (c != '#') {
                break;
            }
            --i;
        }
        if (i < 0) {
            return input.subSequence(0, 0);
        }
        c = input.charAt(i);
        if (c == '\u0020' || c == '\t') {
            return input.subSequence(0, i);
        } else {
            return input;
        }
    }
}
