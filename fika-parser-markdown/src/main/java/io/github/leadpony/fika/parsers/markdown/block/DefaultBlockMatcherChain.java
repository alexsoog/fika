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
import java.util.List;
import java.util.Set;

import io.github.leadpony.fika.core.model.Document;
import io.github.leadpony.fika.core.model.NodeFactory;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.block.matchers.DocumentMatcher;
import io.github.leadpony.fika.parsers.markdown.common.LinkDefinitionMap;

/**
 * @author leadpony
 */
public class DefaultBlockMatcherChain implements BlockMatcherChain, BlockMatcher.Context {

    private final NodeFactory nodeFactory;
    private final LinkDefinitionMap linkDefinitions;
    private final BlockMatcherFinder matcherFinder;
    private final Set<Text> inlines = new HashSet<>();
    private final DocumentMatcher rootMatcher;
    private int lineNumber;
    
    public DefaultBlockMatcherChain(NodeFactory nodeFactory, 
            LinkDefinitionMap linkDefinitions,
            List<BlockMatcherFactory> factories) {
        this.nodeFactory = nodeFactory;
        this.linkDefinitions = linkDefinitions; 
        this.matcherFinder = new DefaultBlockMatcherFinder(factories);
        this.rootMatcher = new DocumentMatcher();
        this.rootMatcher.bind(this);
        this.lineNumber = 0;
    }
    
    /* BlockMatcherChain interface */

    @Override
    public void match(String line) {
        BlockInputSequence content = BlockInputSequence.of(line);
        lineNumber++;
        rootMatcher.match(content);
    }
    
    @Override
    public Document close() {
        return (Document)rootMatcher.close();
    }
    
    @Override
    public Set<Text> getInlines() {
        return inlines;
    }

    /* BlockMatcher.Context interface */
    
    @Override
    public int lineNo() {
        return lineNumber;
    }

    @Override
    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    @Override
    public BlockMatcherFinder finder() {
        return matcherFinder;
    }

    @Override
    public LinkDefinitionMap getLinkDefinitionMap() {
        return linkDefinitions;
    }
   
    @Override
    public void addInline(Text text) {
        this.inlines.add(text);
    }
}
