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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import io.github.leadpony.fika.parsers.markdown.block.BlockBuilder;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.MatcherType;
import io.github.leadpony.fika.parsers.markdown.block.BuilderMode;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
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
    public MatcherType matcherType() {
        return BasicMatcherType.HTML_BLOCK;
    }

    @Override
    public Set<? extends MatcherType> interruptible() {
        return EnumSet.of(BasicMatcherType.PARAGRAPH, BasicMatcherType.LINK_DEFINITION);
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
