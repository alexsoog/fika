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
package org.leadpony.fika.parser.markdown.block;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * An implementation of {@link BlockBuilderFinder}.
 * 
 * @author leadpony
 */
class DefaultBlockBuilderFinder implements BlockBuilderFinder {

    private final BlockContext context;
    private final List<BlockMatcher> matchers;
    private final Map<BlockType, List<BlockMatcher>> interrupters = new HashMap<>();
    private final Map<BlockType, List<BlockMatcher>> replacers = new HashMap<>();

    DefaultBlockBuilderFinder(BlockContext context, List<BlockMatcher> matchers) {
        this.context = context;
        this.matchers = setUpMatchers(matchers);
    }

    @Override
    public BlockBuilder findBuilder(InputSequence input) {
        if (input.isBlank()) {
            return null;
        }
        for (BlockMatcher matchers: this.matchers) {
            BlockBuilder builder = matchers.newBuilder(input);
            if (builder != null) {
                return bindContextTo(builder);
            }
        }
        return null;
    }
    
    @Override
    public BlockBuilder findInterruptingBuilder(
            InputSequence input, BlockBuilder current, BuilderMode mode) {
        if (input.isBlank()) {
            return null;
        }
        List<BlockMatcher> matchers = interrupters.get(current.blockType());
        if (matchers == null) {
            return null;
        }
        for (BlockMatcher matcher: matchers) {
            BlockBuilder builder = matcher.newInterruptingBuilder(input, current, mode);
            if (builder != null) {
                return bindContextTo(builder);
            }
        }
        return null;
    }
   
    @Override
    public BlockBuilder findReplacingBuilder(
            InputSequence input, BlockBuilder current, BuilderMode mode) {
        if (input.isBlank()) {
            return null;
        }
        List<BlockMatcher> matchers = replacers.get(current.blockType());
        if (matchers == null) {
            return null;
        }
        for (BlockMatcher matcher: matchers) {
            BlockBuilder builder = matcher.newReplacingBuilder(input, current, mode);
            if (builder != null) {
                return bindContextTo(builder);
            }
        }
        return null;
    }
    
    private BlockBuilder bindContextTo(BlockBuilder builder) {
        builder.bind(this.context);
        return builder;
    }
    
    private List<BlockMatcher> setUpMatchers(List<BlockMatcher> matchers) {
        Collections.sort(matchers, comparing(BlockMatcher::precedence));
        for (BlockMatcher matcher: matchers) {
            addInterruptingMatcher(matcher);
            addReplacingMatcher(matcher);
        }
        return matchers;
    }
    
    private void addInterruptingMatcher(BlockMatcher matcher) {
        for (BlockType type: matcher.typesToInterrupt()) {
            List<BlockMatcher> list = interrupters.computeIfAbsent(type, DefaultBlockBuilderFinder::newMatcherList);
            list.add(matcher);
        }
    }

    private void addReplacingMatcher(BlockMatcher matcher) {
        for (BlockType type: matcher.typesToReplace()) {
            List<BlockMatcher> list = replacers.computeIfAbsent(type, DefaultBlockBuilderFinder::newMatcherList);
            list.add(matcher);
        }
    }
    
    private static List<BlockMatcher> newMatcherList(BlockType type) {
        return new ArrayList<>();
    }
}
