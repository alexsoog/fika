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
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherChain;
import io.github.leadpony.fika.parsers.markdown.inline.InlineHandlerProvider;
import io.github.leadpony.fika.parsers.markdown.inline.InlineHandlerProviderRegistry;
import io.github.leadpony.fika.parsers.markdown.inline.DefaultInlineProcessor;

/**
 * @author leadpony
 */
class MarkdownParser implements Parser {
    
    private final Reader reader;
    private final BlockMatcherChain blockMatcherChain;
    private final DefaultInlineProcessor inlineProcessor;
    
    public MarkdownParser(Reader reader, NodeFactory nodeFactory) {
        this.reader = reader;
        this.blockMatcherChain = createBlockMatcherChain(nodeFactory);
        this.inlineProcessor = createInlineProcessor(nodeFactory);
    }

    @Override
    public Document parse() {
        try {
            return parseBlocks();
        } catch (IOException e) {
            throw new ParserException(e);
        }
    }
    
    private Document parseBlocks() throws IOException {
        BlockMatcherChain chain = this.blockMatcherChain;
        BufferedReader reader = new BufferedReader(this.reader);
        String line = null;
        while ((line = reader.readLine()) != null) {
            chain.match(line);
        }
        Document doc = chain.close();
        processAllInlines(chain.getInlines());
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
    
    private BlockMatcherChain createBlockMatcherChain(NodeFactory nodeFactory) {
        return new BlockMatcherChain(nodeFactory);
    }
    
    private DefaultInlineProcessor createInlineProcessor(NodeFactory nodeFactory) {
        InlineHandlerProviderRegistry registry = InlineHandlerProviderRegistry.get();
        DefaultInlineProcessor processor = new DefaultInlineProcessor(nodeFactory);
        registry.providers().stream()
                .map(InlineHandlerProvider::newHandler)
                .forEach(processor::installHandler);
        return processor;
    }
}

