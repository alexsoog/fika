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
package org.leadpony.fika.format.markdown.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Set;

import org.leadpony.fika.format.markdown.block.BlockMatcher;
import org.leadpony.fika.format.markdown.block.BlockProcessor;
import org.leadpony.fika.format.markdown.block.DefaultBlockProcessor;
import org.leadpony.fika.format.markdown.common.LinkDefinitionMap;
import org.leadpony.fika.format.markdown.inline.DefaultInlineProcessor;
import org.leadpony.fika.format.markdown.inline.InlineHandler;
import org.leadpony.fika.format.markdown.inline.InlineProcessor;
import org.leadpony.fika.parser.core.Parser;
import org.leadpony.fika.parser.core.ParsingException;
import org.leadpony.fika.parser.model.Document;
import org.leadpony.fika.parser.model.NodeFactory;
import org.leadpony.fika.parser.model.Text;

/**
 * The parser for parsing the source written in Markdown.
 *
 * @author leadpony
 */
class MarkdownParser implements Parser {

    // reader of the source.
    private final Reader reader;

    private boolean alreadyRead;
    private boolean alreadyClosed;

    // link reference definitions.
    private final LinkDefinitionMap linkDefinitions;

    // block processor.
    private final BlockProcessor blockProcessor;

    // inline processor.
    private final InlineProcessor inlineProcessor;

    public MarkdownParser(Reader reader, NodeFactory nodeFactory, Set<FeatureProvider> featureSet) {
        this.reader = reader;
        this.alreadyRead = this.alreadyClosed = false;
        this.linkDefinitions = new LinkDefinitionMap();
        ParserBuilder builder = new ParserBuilder();
        registerFeatures(featureSet, builder);
        this.blockProcessor = buildBlockProcessor(nodeFactory, builder.matchers());
        this.inlineProcessor = buildInlineProcessor(nodeFactory, builder.handlers());
    }

    @Override
    public void close() {
        if (!alreadyClosed) {
            try {
                reader.close();
                alreadyClosed = true;
            } catch (IOException e) {
                throw new ParsingException(e.getMessage(), e);
            }
        }
    }

    @Override
    public Document parse() {
        if (this.alreadyRead || this.alreadyClosed) {
            throw new IllegalStateException();
        }
        try {
            Document doc = processAllBlocks();
            processAllInlines(this.blockProcessor.getInlines());
            return doc;
        } catch (IOException e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    private Document processAllBlocks() throws IOException {
        BlockProcessor processor = this.blockProcessor;
        BufferedReader reader = new BufferedReader(this.reader);
        String line = null;
        while ((line = reader.readLine()) != null) {
            processor.process(line);
        }
        return processor.getDocument();
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
        return new DefaultBlockProcessor(nodeFactory, linkDefinitions, matchers);
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
