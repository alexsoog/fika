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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import io.github.leadpony.fika.core.nodes.Document;
import io.github.leadpony.fika.core.parser.helper.nodes.Modifiable;

/**
 * @author leadpony
 */
public class BlockMatcherChain {
    
    private final Context context;
    private final DocumentMatcher rootMatcher;
    
    public BlockMatcherChain() {
        this.context = createContext();
        this.rootMatcher = new DocumentMatcher();
        this.rootMatcher.bind(this.context);
    }
    
    public boolean match(String line) {
        Content content = new Content(line);
        return rootMatcher.match(content);
    }
    
    public Document close() {
        return rootMatcher.close();
    }
    
    public Map<Modifiable, String> getInlines() {
        return context.inlines;
    }
    
    private Context createContext() {
        List<BlockMatcher.Factory> buidlerFactories = createBlockBuilderFactries();
        return new Context(buidlerFactories);
    }
    
    private List<BlockMatcher.Factory> createBlockBuilderFactries() {
        List<BlockMatcher.Factory> factories = new ArrayList<>();
        factories.add(new ThematicBreakMatcher.Factory());
        factories.add(new HeadingMatcher.Factory());
        factories.add(new IndentedCodeMatcher.Factory());
        Collections.sort(factories, (x, y)->x.precedence() - y.precedence());
        return factories;
    }
    
    private static class Context implements BlockMatcher.Context {

        private final List<BlockMatcher.Factory> factories;
        private final Map<Modifiable, String> inlines = new IdentityHashMap<>();
        
        Context(List<BlockMatcher.Factory> factories) {
            this.factories = factories;
        }

        @Override
        public BlockMatcher match(Content content) {
            if (content.isBlank()) {
                return null;
            }
            for (BlockMatcher.Factory factory: this.factories) {
                BlockMatcher matched = factory.newMatcher(content);
                if (matched != null) {
                    return matched;
                }
            }
            return new ParagraphMatcher(content);
        }

        @Override
        public BlockMatcher match(Content content, int precedence) {
            if (content.isBlank()) {
                return null;
            }
            for (BlockMatcher.Factory factory: this.factories) {
                if (factory.precedence() < precedence) {
                    BlockMatcher matched = factory.newMatcher(content);
                    if (matched != null) {
                        return matched;
                    }
                }
            }
            return null;
        }
        
        @Override
        public void addInline(Modifiable container, String content) {
            this.inlines.put(container, content);
        }
    }
}
