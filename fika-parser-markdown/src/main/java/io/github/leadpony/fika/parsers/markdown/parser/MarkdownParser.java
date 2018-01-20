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
package io.github.leadpony.fika.parsers.markdown.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import io.github.leadpony.fika.core.model.Document;
import io.github.leadpony.fika.core.model.NodeFactory;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.core.parser.Parser;
import io.github.leadpony.fika.core.parser.ParserException;
import io.github.leadpony.fika.parsers.markdown.block.BlockProcessor;
import io.github.leadpony.fika.parsers.markdown.block.DefaultBlockProcessor;
import io.github.leadpony.fika.parsers.markdown.common.LinkDefinitionMap;
import io.github.leadpony.fika.parsers.markdown.inline.InlineProcessor;
import io.github.leadpony.fika.parsers.markdown.inline.DefaultInlineProcessor;

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
        this.blockProcessor = buildBlockProcessor(nodeFactory);
        this.inlineProcessor = buildInlineProcessor(nodeFactory);
        installFeatures(featureSet);
    }

    @Override
    public Document parse() {
        try {
            return processAllBlocks();
        } catch (IOException e) {
            throw new ParserException(e);
        }
    }
    
    private Document processAllBlocks() throws IOException {
        BlockProcessor processor = this.blockProcessor;
        BufferedReader reader = new BufferedReader(this.reader);
        processor.open();
        String line = null;
        while ((line = reader.readLine()) != null) {
            processor.process(line);
        }
        Document doc = processor.close();
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
    
    protected BlockProcessor buildBlockProcessor(NodeFactory nodeFactory) {
        return new DefaultBlockProcessor(nodeFactory, linkDefinitions);
    }
    
    protected InlineProcessor buildInlineProcessor(NodeFactory nodeFactory) {
        return new DefaultInlineProcessor(nodeFactory, linkDefinitions);
    }
    
    protected void installFeatures(Set<FeatureProvider> features) {
        for (FeatureProvider feature: features) {
            feature.install(this.blockProcessor, this.inlineProcessor);
        }
    }
}

