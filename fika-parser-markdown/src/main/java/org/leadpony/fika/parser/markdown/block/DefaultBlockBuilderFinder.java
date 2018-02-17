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

    private final List<BlockMatcher> matchers;
    private final Map<MatcherType, List<BlockMatcher>> interrupters = new HashMap<>();

    DefaultBlockBuilderFinder(List<BlockMatcher> matchers) {
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
                return builder;
            }
        }
        return null;
    }
    
    @Override
    public BlockBuilder findInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        if (input.isBlank()) {
            return null;
        }
        List<BlockMatcher> matchers = interrupters.get(current.matcherType());
        if (matchers == null) {
            return null;
        }
        for (BlockMatcher matcher: matchers) {
            BlockBuilder builder = matcher.newInterruptingBuilder(input, current, mode);
            if (builder != null) {
                return builder;
            }
        }
        return null;
    }
    
    private List<BlockMatcher> setUpMatchers(List<BlockMatcher> matchers) {
        Collections.sort(matchers, comparing(BlockMatcher::precedence));
        for (BlockMatcher matcher: matchers) {
            addInterrupter(matcher);
        }
        return matchers;
    }
    
    private void addInterrupter(BlockMatcher matcher) {
        for (MatcherType type: matcher.interruptible()) {
            List<BlockMatcher> list = interrupters.get(type);
            if (list == null) {
                list = new ArrayList<>();
                interrupters.put(type, list);
            }
            list.add(matcher);
        }
    }
}
