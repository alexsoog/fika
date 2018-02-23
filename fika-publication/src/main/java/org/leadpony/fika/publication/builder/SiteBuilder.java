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
package org.leadpony.fika.publication.builder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.leadpony.fika.publication.project.PageSource;
import org.leadpony.fika.publication.project.Project;
import org.leadpony.fika.publication.view.View;

import org.leadpony.fika.core.model.Document;
import org.leadpony.fika.core.parser.BasicFeature;
import org.leadpony.fika.core.parser.Parser;
import org.leadpony.fika.core.parser.ParserFactory;
import org.leadpony.fika.core.parser.ParserFactoryBuilder;

/**
 * Builder of publication composed of one or more HTML files.
 * 
 * @author leadpony
 */
public class SiteBuilder extends AbstractHtmlBuilder {
    
    private static final Logger log = Logger.getLogger(SiteBuilder.class.getName());
    
    private final Map<String, ParserFactory> parserFactories = new HashMap<>();
    private Charset charset = StandardCharsets.UTF_8;

    private View pageView;
   
    public SiteBuilder(Project project) {
        super(project);
    }
    
    @Override
    public String name() {
        return "site";
    }
    
    @Override
    protected void initialize() throws Exception {
        super.initialize();
        this.pageView = viewResolver().resolveView("page.ftlh");
    }
    
    @Override
    protected void compile() throws IOException {
        int count = 0;
        for (PageSource source: project().sources()) {
            compileSource(source);
            ++count;
        }
        log.fine("Compiled " + count + " source file(s).");
    }
    
    private void compileSource(PageSource source) throws IOException {
        Document doc = parseSource(source);
        if (doc != null) {
            renderHtml(source, doc);
        }
    }
    
    private Document parseSource(PageSource source) throws IOException {
        Path path = source.path();
        String mediaType = source.mediaType();
        log.fine("Compiling source: " + path + " [" + mediaType + "]");
        ParserFactory factory = findParserFactory(mediaType);
        if (factory == null) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(resolveSource(path))) {
            Parser parser = factory.newParser(reader);
            return parser.parse();
        }
    }

    private void renderHtml(PageSource source, Document doc) throws IOException {
        Path path = mapSourceToHtml(source.path());
        Path fullPath = resolveTarget(path);
        Path parent = fullPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (Writer writer = Files.newBufferedWriter(fullPath, this.charset)) {
            this.pageView.render(createViewContext(doc, path), writer);
        }
    }
    
    private Path mapSourceToHtml(Path path) {
        Path parent = path.getParent();
        String name = path.getFileName().toString();
        int index = name.lastIndexOf('.');
        if (index >= 0) {
            name = name.substring(0, index);
        }
        Path newFileName = Paths.get(name + ".html");
        Path newPath = (parent != null) ? parent.resolve(newFileName) : newFileName;
        return newPath;
    }
    
    private Map<String, Object> createViewContext(Document doc, Path path) {
        Map<String, Object> context = new HashMap<>();
        String url = mapToUrl(path);
        context.put("url", url);
        context.put("page", new DefaultPageContext(doc, url));
        return context;
    }
    
    private static String mapToUrl(Path path) {
        return path.toString().replace("\\", "/");
    }
    
    private ParserFactory findParserFactory(String mediaType) {
        ParserFactory factory = parserFactories.get(mediaType);
        if (factory == null) {
            factory = buildParserFactory(mediaType);
            if (factory != null) {
                parserFactories.put(mediaType, factory);
            }
        }
        return factory;
    }
    
    private ParserFactory buildParserFactory(String mediaType) {
        ParserFactoryBuilder builder = ParserFactory.builder(mediaType, "");
        if (builder == null) {
            return null;
        }
        builder.withFeature(BasicFeature.ADMONITION);
        builder.withFeature(BasicFeature.DEFINITION_LIST);
        return builder.build();
    }
}
