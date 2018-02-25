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
package org.leadpony.fika.parser.markdown.parser;

import java.io.Reader;
import java.util.List;
import java.util.Set;

import org.leadpony.fika.core.model.Document;
import org.leadpony.fika.core.model.NodeFactory;
import org.leadpony.fika.core.model.Text;
import org.leadpony.fika.core.parser.Parser;
import org.leadpony.fika.parser.markdown.block.BlockMatcher;
import org.leadpony.fika.parser.markdown.block.BlockProcessor;
import org.leadpony.fika.parser.markdown.block.DefaultBlockProcessor;
import org.leadpony.fika.parser.markdown.common.LinkDefinitionMap;
import org.leadpony.fika.parser.markdown.inline.DefaultInlineProcessor;
import org.leadpony.fika.parser.markdown.inline.InlineHandler;
import org.leadpony.fika.parser.markdown.inline.InlineProcessor;

/**
 * The parser to parse source written in Markdown.
 * 
 * @author leadpony
 */
class MarkdownParser implements Parser {
    
    // reader of the source.
    private final Reader reader;

    // link reference definitions.
    private final LinkDefinitionMap linkDefinitions;
    
    // block processor.
    private final BlockProcessor blockProcessor;
    
    // inline processor.
    private final InlineProcessor inlineProcessor;
    
    public MarkdownParser(Reader reader, NodeFactory nodeFactory, Set<FeatureProvider> featureSet) {
        this.reader = reader;
        this.linkDefinitions = new LinkDefinitionMap();
        ParserBuilder builder = new ParserBuilder();
        registerFeatures(featureSet, builder);
        this.blockProcessor = buildBlockProcessor(nodeFactory, builder.matchers());
        this.inlineProcessor = buildInlineProcessor(nodeFactory, builder.handlers());
    }

    @Override
    public Document parse() {
        return processAllBlocks();
    }
    
    private Document processAllBlocks() {
        BlockProcessor processor = this.blockProcessor;
        processor.processAll();
        Document doc = processor.getDocument();
        processAllInlines(processor.getInlines());
        return doc;
    }
    
    private void processAllInlines(Set<Text> inlines) {
        for (Text text: inlines) {
            processInline(text);
        }
    }
    
    private void processInline(Text text) {
        inlineProcessor.processInlines(text);
    }
    
    protected BlockProcessor buildBlockProcessor(NodeFactory nodeFactory, List<BlockMatcher> matchers) {
        return new DefaultBlockProcessor(this.reader, nodeFactory, linkDefinitions, matchers);
    }
    
    protected InlineProcessor buildInlineProcessor(NodeFactory nodeFactory, List<InlineHandler> handlers) {
        return new DefaultInlineProcessor(nodeFactory, linkDefinitions, handlers);
    }
    
    protected void registerFeatures(Set<FeatureProvider> features, ParserBuilder builder) {
        for (FeatureProvider feature: features) {
            feature.provide(builder);
        }
    }
}
