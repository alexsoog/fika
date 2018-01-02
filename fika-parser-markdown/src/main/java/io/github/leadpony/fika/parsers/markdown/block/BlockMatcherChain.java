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

/**
 * @author leadpony
 */
public class BlockMatcherChain {
    
    private final Context context;
    private final DocumentMatcher rootMatcher;
    
    public BlockMatcherChain(NodeFactory nodeFactory) {
        this.context = createContext(nodeFactory);
        this.rootMatcher = new DocumentMatcher();
        this.rootMatcher.bind(this.context);
    }
    
    public void match(String line) {
        BlockInputSequence content = BlockInputSequence.of(line);
        context.lineNo++;
        rootMatcher.match(content);
    }
    
    public Document close() {
        return (Document)rootMatcher.close();
    }
    
    public Set<Text> getInlines() {
        return context.inlines;
    }
    
    private Context createContext(NodeFactory nodeFactory) {
        BlockMatcherFinder finder = BlockMatcherFinder.builder().build();
        return new Context(nodeFactory, finder);
    }
    
    private static class Context implements BlockMatcher.Context {

        private final NodeFactory nodeFactory;
        private final BlockMatcherFinder finder;
        private int lineNo;
        private final Set<Text> inlines = new HashSet<>();
        
        Context(NodeFactory nodeFactory, BlockMatcherFinder finder) {
            this.nodeFactory = nodeFactory;
            this.finder = finder;
            this.lineNo = 0;
        }
        
        @Override
        public int lineNo() {
            return lineNo;
        }

        @Override
        public BlockMatcher findMatcher(BlockInputSequence content) {
            return finder.findMatcher(content);
        }

        @Override
        public BlockMatcher findInterruptingMatcher(BlockInputSequence content, BlockMatcher current) {
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
