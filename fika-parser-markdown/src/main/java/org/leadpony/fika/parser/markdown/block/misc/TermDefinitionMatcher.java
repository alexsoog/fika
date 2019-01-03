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
package org.leadpony.fika.parser.markdown.block.misc;

import java.util.EnumSet;
import java.util.Set;

import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockMatcher;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.commonmark.BasicBlockType;
import org.leadpony.fika.parser.markdown.block.commonmark.ParagraphBuilder;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * @author leadpony
 */
public class TermDefinitionMatcher implements BlockMatcher {

    @Override
    public BlockType blockType() {
        return BasicBlockType.TERM_DEFINITIION;
    }

    @Override
    public Set<? extends BlockType> typesToInterrupt() {
        return EnumSet.of(BasicBlockType.TERM, BasicBlockType.TERM_DEFINITIION);
    }
    
    @Override
    public Set<? extends BlockType> typesToReplace() {
        return EnumSet.of(BasicBlockType.PARAGRAPH);
    }

    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        if (!matches(input)) {
            return null;
        }
        return newListBuilder(new TermDefinitionBuilder(indentSize(input)));
    }

    @Override
    public BlockBuilder newInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        if (!matches(input)) {
            return null;
        }
        return new TermDefinitionBuilder(indentSize(input));
    }

    @Override
    public BlockBuilder newReplacingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        if (!matches(input)) {
            return null;
        }
        if (current.blockType() == BasicBlockType.PARAGRAPH) {
            TermBuilder child = new TermBuilder((ParagraphBuilder)current);
            return newListBuilder(child);
        }
        return null;
    }

    private static boolean matches(InputSequence input) {
        if (input.isBlank() || input.charAt(0) != ':') {
            return false;
        }
        return input.countLeadingSpaces(1, 4) >= 1;
    }
    
    private static int indentSize(InputSequence input) {
        return input.countLeadingSpaces(1, 4) + 1;
    }
    
    private static TermDefinitionListBuilder newListBuilder(BlockBuilder child) {
        TermDefinitionListBuilder builder = new TermDefinitionListBuilder();
        builder.openChildBuilder(child);
        return builder;
    }
}
