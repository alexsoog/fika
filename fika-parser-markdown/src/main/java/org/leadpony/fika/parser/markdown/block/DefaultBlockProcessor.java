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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.leadpony.fika.core.model.Document;
import org.leadpony.fika.core.model.NodeFactory;
import org.leadpony.fika.core.model.Text;
import org.leadpony.fika.core.parser.ParserException;
import org.leadpony.fika.parser.markdown.common.InputSequence;
import org.leadpony.fika.parser.markdown.common.LinkDefinitionMap;

/**
 * Default implementation of {@link BlockProcessor}.
 * 
 * @author leadpony
 */
public class DefaultBlockProcessor implements BlockProcessor, BlockContext {

    private final BufferedReader reader;
    private final NodeFactory nodeFactory;
    private final LinkDefinitionMap linkDefinitions;
    private final Set<Text> inlines = new HashSet<>();
    private final DocumentBuilder rootBuilder;

    private DefaultBlockBuilderFinder builderFinder;
    private final LinkedList<InputSequence> cachedLines;
    private int lineNumber;
    
    public DefaultBlockProcessor(
            Reader reader,
            NodeFactory nodeFactory, 
            LinkDefinitionMap linkDefinitions,
            List<BlockMatcher> matchers) {
        this.reader = new BufferedReader(reader);
        this.nodeFactory = nodeFactory;
        this.linkDefinitions = linkDefinitions; 
        this.builderFinder = new DefaultBlockBuilderFinder(this, matchers);
        this.rootBuilder = new DocumentBuilder();
        this.rootBuilder.bind(this);
        this.cachedLines = new LinkedList<>();
        this.lineNumber = 0;
    }
    
    /* BlockProcessor interface */

    @Override
    public void processAll() {
        InputSequence input = null;
        while ((input = fetchLine()) != null) {
            this.lineNumber++;
            this.rootBuilder.processLine(input);
        }
    }
    
    @Override
    public Document getDocument() {
        return (Document)rootBuilder.build();
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
    public InputSequence peekLine(int index) {
        if (index < 1) {
            throw new IllegalArgumentException();
        }
        while (index > this.cachedLines.size()) {
            InputSequence line = readLineFromReader();
            if (line == null) {
                return null;
            }
            this.cachedLines.addLast(line);
        }
        return this.cachedLines.get(index - 1);
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

    private InputSequence fetchLine() {
        if (hasCachedLine()) {
            return readLineFromCache();
        } else {
            return readLineFromReader();
        }
    }
    
    private boolean hasCachedLine() {
        return !this.cachedLines.isEmpty();
    }
    
    private InputSequence readLineFromReader() {
        try {
            String line = this.reader.readLine();
            if (line == null) {
                return null;
            }
            return BlockInputSequence.of(line);
        } catch (IOException e) {
            throw new ParserException(e);
        }
    }
    
    private InputSequence readLineFromCache() {
        return this.cachedLines.removeFirst();
    }
}
