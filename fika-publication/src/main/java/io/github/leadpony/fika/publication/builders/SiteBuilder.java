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
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import io.github.leadpony.fika.core.model.Document;
import io.github.leadpony.fika.core.parser.Parser;
import io.github.leadpony.fika.core.parser.ParserFactory;
import io.github.leadpony.fika.core.renderers.AbstractHtmlRenderer;
import io.github.leadpony.fika.core.renderers.HtmlRenderer;
import io.github.leadpony.fika.core.renderers.XmlFormatter;
import io.github.leadpony.fika.publication.project.PageSource;
import io.github.leadpony.fika.publication.project.Project;

/**
 * Builder of publication composed of one or more HTML files.
 * 
 * @author leadpony
 */
public class SiteBuilder extends AbstractPublicationBuilder {
    
    private static final Logger log = Logger.getLogger(SiteBuilder.class.getName());
    
    private final Map<String, ParserFactory> parserFactories = new HashMap<>();
    private Charset charset = StandardCharsets.UTF_8;
    
    public SiteBuilder(Project project) {
        super(project);
    }
    
    @Override
    protected void processResources() throws IOException {
        Files.walkFileTree(sourceDirectory(), new ResourceFileVisitor());
    }

    @Override
    protected void compile() throws IOException {
        log.fine("Starting to build HTML document.");
        for (PageSource source: project().sources()) {
            compileSource(source);
        }
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

    private HtmlRenderer buildHtmlRenderer() {
        HtmlRenderer.Builder builder = new HtmlRendererBuilder();
        builder.withCharset(this.charset);
        builder.withLanguage(project().language());
        builder.withTitle(project().title());
        builder.withStylesheets(project().stylesheets());
        return builder.build();
    }
    
    private void renderHtml(PageSource source, Document doc) throws IOException {
        HtmlRenderer renderer = buildHtmlRenderer();
        Path path = mapSourceToTarget(source.path());
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (Writer writer = Files.newBufferedWriter(path, this.charset)) {
            renderer.render(doc, writer);
        }
    }
    
    private void renderHtmlHeader(XmlFormatter formatter) {
    }

    private void renderHtmlFooter(XmlFormatter formatter) {
        formatter.startTag("footer");
        formatter.emptyTag("hr");
        formatter.startTag("p");
        formatter.rawXml(project().copyright());
        formatter.endTag("p");
        formatter.endTag("footer");
    }
    
    private Path mapSourceToTarget(Path path) {
        Path parent = path.getParent();
        String name = path.getFileName().toString();
        int index = name.lastIndexOf('.');
        if (index >= 0) {
            name = name.substring(0, index);
        }
        Path newFileName = Paths.get(name + ".html");
        Path newPath = (parent != null) ? parent.resolve(newFileName) : newFileName;
        return resolveTarget(newPath);
    }
    
    private ParserFactory findParserFactory(String mediaType) {
        ParserFactory factory = parserFactories.get(mediaType);
        if (factory == null) {
            factory = ParserFactory.newInstance(mediaType);
            if (factory != null) {
                parserFactories.put(mediaType, factory);
            }
        }
        return factory;
    }
    
    private void copyFileToTarget(Path file) throws IOException {
        Path targetFile = resolveTarget(relativizeSource(file));
        Path parent = targetFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
    }
    
    private class ResourceFileVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String fileName = file.getFileName().toString();
            int index = fileName.lastIndexOf('.');
            if (index >= 0) {
                String extension = fileName.substring(index + 1).toLowerCase();
                if (project().resourceExtensions().contains(extension)) {
                    copyFileToTarget(file);
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }
    
    private class CustomHtmlRenderer extends AbstractHtmlRenderer {
 
        private CustomHtmlRenderer(HtmlRendererBuilder builder) {
            super(builder);
        }

        @Override
        protected void renderHeader(XmlFormatter formatter) {
            renderHtmlHeader(formatter);
        }

        @Override
        protected void renderFooter(XmlFormatter formatter) {
            renderHtmlFooter(formatter);
        }
    }
    
    private class HtmlRendererBuilder extends AbstractHtmlRenderer.Builder {
        
        @Override
        public HtmlRenderer build() {
            return new CustomHtmlRenderer(this);
        }
    }
}
