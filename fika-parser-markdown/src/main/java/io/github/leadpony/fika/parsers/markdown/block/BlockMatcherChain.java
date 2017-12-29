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

import java.util.HashSet;
import java.util.Set;

import io.github.leadpony.fika.core.nodes.Document;
import io.github.leadpony.fika.core.nodes.NodeFactory;
import io.github.leadpony.fika.core.nodes.Text;
import io.github.leadpony.fika.core.parser.support.nodes.DefaultNodeFactory;

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
    
    public void match(String line) {
        Content content = Content.of(line);
        context.lineNo++;
        rootMatcher.match(content);
    }
    
    public Document close() {
        return (Document)rootMatcher.close();
    }
    
    public Set<Text> getInlines() {
        return context.inlines;
    }
    
    private Context createContext() {
        BlockMatcherFinder finder = BlockMatcherFinder.builder().build();
        return new Context(finder);
    }
    
    private static class Context implements BlockMatcher.Context {

        private final BlockMatcherFinder finder;
        private int lineNo;
        private final NodeFactory nodeFactory = new DefaultNodeFactory();
        private final Set<Text> inlines = new HashSet<>();
        
        Context(BlockMatcherFinder finder) {
            this.finder = finder;
            this.lineNo = 0;
        }
        
        @Override
        public int lineNo() {
            return lineNo;
        }

        @Override
        public BlockMatcher findMatcher(Content content) {
            return finder.findMatcher(content);
        }

        @Override
        public BlockMatcher findInterruptingMatcher(Content content, BlockMatcher current) {
            return finder.findInterruptingMatcher(content, current);
        }
    
        @Override
        public NodeFactory nodeFactory() {
            return nodeFactory;
        }
        
        @Override
        public void addInline(Text text) {
            this.inlines.add(text);
        }
    }
}
