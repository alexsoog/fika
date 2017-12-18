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
import java.util.List;
import java.util.Map;

import io.github.leadpony.fika.core.nodes.Document;
import io.github.leadpony.fika.core.nodes.Inline;
import io.github.leadpony.fika.core.parser.Parser;
import io.github.leadpony.fika.core.parser.ParserException;
import io.github.leadpony.fika.core.parser.helper.nodes.ContainerNode;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherChain;
import io.github.leadpony.fika.parsers.markdown.inline.InlineProcessor;

/**
 * @author leadpony
 */
class MarkdownParser implements Parser {
    
    private final Reader reader;
    private final InlineProcessor inlineProcessor;
    
    public MarkdownParser(Reader reader) {
        this.reader = reader;
        this.inlineProcessor = new InlineProcessor();
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
        BlockMatcherChain chain = new BlockMatcherChain();
        BufferedReader reader = new BufferedReader(this.reader);
        String line = null;
        while ((line = reader.readLine()) != null) {
            chain.match(line);
        }
        Document doc = chain.close();
        processInlines(chain.getInlines());
        return doc;
    }
    
    private void processInlines(Map<ContainerNode, String> inlines) {
        for (Map.Entry<ContainerNode, String> entry: inlines.entrySet()) {
            processInline(entry.getKey(), entry.getValue());
        }
    }
    
    private void processInline(ContainerNode container, String content) {
        List<Inline> children = inlineProcessor.process(content);
        container.replaceChildren(children);
    }
}

