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
package io.github.leadpony.fika.parsers.markdown.block;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * An implementation of {@link BlockMatcherFinder}.
 * 
 * @author leadpony
 */
class DefaultBlockMatcherFinder implements BlockMatcherFinder {

    private final List<BlockMatcherFactory> factories;
    private final Map<BlockType, List<BlockMatcherFactory>> interrupters = new HashMap<>();

    /**
     * @param factories
     */
    DefaultBlockMatcherFinder(List<BlockMatcherFactory> factories) {
        this.factories = factories;
        setUpMatcherFactories();
    }

    @Override
    public BlockMatcher findMatcher(InputSequence input) {
        if (input.isBlank()) {
            return null;
        }
        for (BlockMatcherFactory factory: this.factories) {
            BlockMatcher matched = factory.newMatcher(input);
            if (matched != null) {
                return matched;
            }
        }
        return null;
    }
    
    @Override
    public BlockMatcher findInterruptingMatcher(InputSequence input, BlockMatcher current) {
        if (input.isBlank()) {
            return null;
        }
        List<BlockMatcherFactory> factories = interrupters.get(current.blockType());
        if (factories == null) {
            return null;
        }
        for (BlockMatcherFactory factory: factories) {
            BlockMatcher matched = factory.newInterrupter(input, current);
            if (matched != null) {
                return matched;
            }
        }
        return null;
    }
    
    private void setUpMatcherFactories() {
        Collections.sort(this.factories, comparing(BlockMatcherFactory::precedence));
        for (BlockMatcherFactory factory: this.factories) {
            addInterrupter(factory);
        }
    }
    
    private void addInterrupter(BlockMatcherFactory factory) {
        for (BlockType type: factory.interruptible()) {
            List<BlockMatcherFactory> list = interrupters.get(type);
            if (list == null) {
                list = new ArrayList<>();
                interrupters.put(type, list);
            }
            list.add(factory);
        }
    }
}
