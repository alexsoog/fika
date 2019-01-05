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
package org.leadpony.fika.parser.markdown.block.commonmark;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockMatcher;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Matcher implementation for HTML block.
 * 
 * @author leadpony
 */
public class HtmlBlockMatcher implements BlockMatcher {

    private static final List<Function<InputSequence, HtmlBlockBuilder>> starters = new ArrayList<>();
    private static final List<Function<InputSequence, HtmlBlockBuilder>> interruptingStarters = new ArrayList<>();
    
    static {
        interruptingStarters.add(HtmlScriptBlockBuilder::start);
        interruptingStarters.add(HtmlCommentBlockBuilder::start);
        interruptingStarters.add(ProcessingInstructionBuilder::start);
        interruptingStarters.add(DeclarationBuilder::start);
        interruptingStarters.add(CDataSectionBuilder::start);
        interruptingStarters.add(HtmlElementBlockBuilder::start);
        starters.addAll(interruptingStarters);
        starters.add(HtmlTagBlockBuilder::start);
    }
    
    public HtmlBlockMatcher() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.HTML_BLOCK;
    }

    @Override
    public Set<? extends BlockType> typesToInterrupt() {
        return EnumSet.of(BasicBlockType.PARAGRAPH, BasicBlockType.LINK_DEFINITION);
    }
    
    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        return newMatcher(input, starters);
    }
    
    @Override
    public BlockBuilder newInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        return newMatcher(input, interruptingStarters);
    }

    private BlockBuilder newMatcher(InputSequence input, List<Function<InputSequence, HtmlBlockBuilder>> functions) {
        int i = input.countLeadingSpaces(0, 3);
        if (i >= input.length() || input.charAt(i) != '<') {
            return null;
        }
        input = input.subSequence(i);
        for (Function<InputSequence, HtmlBlockBuilder> function: functions) {
            BlockBuilder matcher = function.apply(input);
            if (matcher != null) {
                return matcher;
            }
        }
        return null;
    }
}
