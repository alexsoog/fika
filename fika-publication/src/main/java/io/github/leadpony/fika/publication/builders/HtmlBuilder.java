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
package io.github.leadpony.fika.publication.builders;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import io.github.leadpony.fika.core.nodes.Document;
import io.github.leadpony.fika.core.parser.Parser;
import io.github.leadpony.fika.core.parser.ParserFactory;
import io.github.leadpony.fika.publication.project.PageSource;

/**
 * @author leadpony
 */
class HtmlBuilder extends BaseBuilder {
    
    private static final Logger log = Logger.getLogger(HtmlBuilder.class.getName());
    
    private final Map<String, ParserFactory> parserFactories = new HashMap<>();

    @Override
    public void doBuild() {
        log.fine("Starting to build HTML document.");
        try {
            generateAllPages();
        } catch (IOException e) {
        }
    }
    
    private void generateAllPages() throws IOException {
        for (PageSource source: project().sources()) {
            generatePage(source);
        }
    }
    
    private void generatePage(PageSource source) throws IOException {
        Path path = source.path();
        String mediaType = source.mediaType();
        log.fine("Compiling source: " + path + " [" + mediaType + "]");
        ParserFactory factory = getParserFactory(mediaType);
        if (factory == null) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(resolveSource(path))) {
            Parser parser = factory.newParser(reader);
            Document doc = parser.parse();
        }
    }
    
    private ParserFactory getParserFactory(String mediaType) {
        ParserFactory factory = parserFactories.get(mediaType);
        if (factory == null) {
            factory = ParserFactory.newInstance(mediaType);
            if (factory != null) {
                parserFactories.put(mediaType, factory);
            }
        }
        return factory;
    }
}
