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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author leadpony
 */
class BlockMatcherFinder {
    
    private final List<BlockMatcherFactory> factories;

    static Builder builder() {
        return new Builder();
    }
    
    private BlockMatcherFinder(List<BlockMatcherFactory> factories) {
        this.factories = factories;
        Collections.sort(factories, (x, y)->x.precedence() - y.precedence());
    }
    
    BlockMatcher findMatcher(Content content) {
        if (content.isBlank()) {
            return null;
        }
        for (BlockMatcherFactory factory: this.factories) {
            BlockMatcher matched = factory.newMatcher(content);
            if (matched != null) {
                return matched;
            }
        }
        return new ParagraphMatcher();
    }
    
    BlockMatcher findInterruptingMatcher(Content content, BlockMatcher current) {
        if (content.isBlank()) {
            return null;
        }
        final int precedence = current.precedence();
        for (BlockMatcherFactory factory: this.factories) {
            if (factory.precedence() < precedence) {
                BlockMatcher matched = factory.newInterrupter(content, current);
                if (matched != null) {
                    return matched;
                }
            }
        }
        return null;
    }

    static class Builder {
        
        private static final List<BlockMatcherFactory> defaultFactories = loadDefaultFactories(); 
        
        private final List<BlockMatcherFactory> factories;
        
        private Builder() {
            this.factories = defaultFactories;
        }
        
        BlockMatcherFinder build() {
            return new BlockMatcherFinder(factories);
        }
        
        private static List<BlockMatcherFactory> loadDefaultFactories() {
            @SuppressWarnings("serial")
            List<BlockMatcherFactory> list = new ArrayList<BlockMatcherFactory>() {{
                add(new ThematicBreakMatcherFactory());
                add(new HeadingMatcherFactory());
                add(new IndentedCodeMatcherFactory());
                add(new FencedCodeMatcherFactory());
                add(new BlockQuoteMatcherFactory());
                add(new ListMatcherFactory());
            }};
            return list;
        }
    }
}
