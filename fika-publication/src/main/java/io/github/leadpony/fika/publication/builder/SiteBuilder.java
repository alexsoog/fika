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
package io.github.leadpony.fika.publication.builder;

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
import io.github.leadpony.fika.publication.project.PageSource;
import io.github.leadpony.fika.publication.project.Project;
import io.github.leadpony.fika.publication.view.View;
import io.github.leadpony.fika.publication.view.ViewResolver;

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
        final ViewResolver viewResolver = viewResolver();
        this.pageView = viewResolver.resolveView("page.ftlh");
    }
    
    @Override
    protected void processResources() throws IOException {
        int count = copyResources(sourceDirectory());
        count += copyResources(templateDirectory());
        log.fine("Copied " + count + " resource file(s).");
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
            Map<String, Object> context = new HashMap<>();
            context.put("url", mapToUrl(path));
            this.pageView.render(doc, context, writer);
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
    
    private static String mapToUrl(Path path) {
        return path.toString().replace("\\", "/");
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
    
    private int copyResources(Path directory) throws IOException {
        ResourceFileVisitor visitor = new ResourceFileVisitor(directory);
        Files.walkFileTree(directory, visitor);
        return visitor.filesCopied;
    }
    
    private void copyFileToTarget(Path file, Path baseDirectory) throws IOException {
        Path relativePath = baseDirectory.relativize(file);
        Path targetFile = resolveTarget(relativePath);
        Path parent = targetFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
    }
    
    private class ResourceFileVisitor extends SimpleFileVisitor<Path> {

        private final Path directory;
        private int filesCopied;
        
        private ResourceFileVisitor(Path directory) {
            this.directory = directory;
        }
        
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Path relative = this.directory.relativize(file);
            if (resourceSet().contains(relative)) {
                copyFileToTarget(file, this.directory);
                this.filesCopied++;
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
