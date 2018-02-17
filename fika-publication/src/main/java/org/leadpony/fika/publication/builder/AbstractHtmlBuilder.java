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
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

import org.leadpony.fika.publication.project.Project;
import org.leadpony.fika.publication.theme.Resource;
import org.leadpony.fika.publication.theme.Theme;
import org.leadpony.fika.publication.view.ViewResolver;
import org.leadpony.fika.publication.view.ViewService;

/**
 * Abstract base builder for HTML media.
 * 
 * @author leadpony
 */
public abstract class AbstractHtmlBuilder extends AbstractPublicationBuilder {

    private static final Logger log = Logger.getLogger(AbstractHtmlBuilder.class.getName());
    
    private Theme theme;
    private ViewResolver viewResolver;

    protected AbstractHtmlBuilder(Project project) {
        super(project);
    }
    
    @Override
    protected void initialize() throws Exception {
        super.initialize();
        this.theme = Theme.find(Theme.DEFAULT_THEME);
        this.viewResolver = createViewResolver();
    }
    
    @Override
    protected void processResources() throws IOException {
        int count = copyThemeResources();
        count += copyResources(sourceDirectory());
        count += copyResources(templateDirectory());
        log.fine("Copied " + count + " resource file(s).");
    }
   
    private int copyResources(Path directory) throws IOException {
        ResourceFileVisitor visitor = new ResourceFileVisitor(directory);
        Files.walkFileTree(directory, visitor);
        return visitor.filesCopied;
    }
    
    private void copyFileToTarget(Path file, Path baseDirectory) throws IOException {
        Path relativePath = baseDirectory.relativize(file);
        Path targetFile = prepareTarget(relativePath);
        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
    }
    
    private int copyThemeResources() throws IOException {
        int count = 0;
        for (Resource resource : this.theme.resources()) {
            copyThemeResource(resource);
            ++count;
        }
        return count;
    }
    
    private void copyThemeResource(Resource resource) throws IOException {
        Path relativePath = Paths.get(resource.path());
        Path targetFile = prepareTarget(relativePath);
        try (InputStream in = resource.newInputStream()) {
            if (in == null) {
                log.severe("Resource not found: " + resource.path());
            } else {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
    
    private Path prepareTarget(Path relativePath) throws IOException {
        Path targetFile = resolveTarget(relativePath);
        Path parent = targetFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        return targetFile;
    }
    
    protected ViewResolver createViewResolver() {
        ViewService service = ViewService.findService("freemarker");
        ViewResolver.Builder builder = service.newViewResolverBuilder();
        builder.withSharedValue("project", project());
        builder.withSharedValue("theme", this.theme);
        builder.withTemplateDirectory(project().templateDirectory());
        return builder.build();
    }
    
    protected ViewResolver viewResolver() {
        return viewResolver;
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
