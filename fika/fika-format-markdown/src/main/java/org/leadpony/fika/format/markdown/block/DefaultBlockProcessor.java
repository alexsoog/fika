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
package org.leadpony.fika.format.markdown.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.leadpony.fika.format.markdown.common.InputSequence;
import org.leadpony.fika.format.markdown.common.LinkDefinitionMap;
import org.leadpony.fika.core.model.Document;
import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.NodeFactory;
import org.leadpony.fika.core.model.Text;

/**
 * Default implementation of {@link BlockProcessor}.
 * 
 * @author leadpony
 */
public class DefaultBlockProcessor implements BlockProcessor, BlockContext {

    private final NodeFactory nodeFactory;
    private final LinkDefinitionMap linkDefinitions;
    private final Set<Text> inlines = new HashSet<>();
    private final DocumentBuilder rootBuilder;

    private DefaultBlockBuilderFinder builderFinder;
    private int lineNumber;
    
    public DefaultBlockProcessor(
            NodeFactory nodeFactory, 
            LinkDefinitionMap linkDefinitions,
            List<BlockMatcher> matchers) {
        this.nodeFactory = nodeFactory;
        this.linkDefinitions = linkDefinitions; 
        this.builderFinder = new DefaultBlockBuilderFinder(this, matchers);
        this.rootBuilder = new DocumentBuilder();
        this.rootBuilder.bind(this);
        this.lineNumber = 0;
    }
    
    /* BlockProcessor interface */

    @Override
    public void process(String line) {
        InputSequence input = BlockInputSequence.of(line);
        this.lineNumber++;
        this.rootBuilder.processLine(input);
    }
    
    @Override
    public Document getDocument() {
        List<Node> nodes = new ArrayList<>();
        this.rootBuilder.build(nodes);
        return (Document)nodes.get(0);
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
    public BlockBuilderFinder finder() {
        return builderFinder;
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
