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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import io.github.leadpony.fika.publication.project.Project;

/**
 * @author leadpony
 */
abstract class AbstractPublicationBuilder implements PublicationBuilder {
    
    private static final Logger log = Logger.getLogger(AbstractPublicationBuilder.class.getName());
    
    private final Project project; 
    private final Path sourceDirectory;
    private Path targetDirectory;
    
    protected AbstractPublicationBuilder(Project project) {
        this.project = project;
        this.sourceDirectory = project.sourceDirectory();
        this.targetDirectory = project.targetDirectory();
    }

    @Override
    public Project project() {
        return project;
    }

    @Override
    public void setTargetDirectory(Path path) {
        targetDirectory = path.toAbsolutePath().normalize();
    }

    @Override
    public void setProperty(String name, Object value) {
    }
    
    @Override
    public void build() {
        try {
            initialize();
            processResources();
            compile();
        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            e.printStackTrace();
        }
    }
    
    protected Path sourceDirectory() {
        return sourceDirectory;
    }

    protected Path targetDirectory() {
        return targetDirectory;
    }
    
    protected Path resolveSource(Path path) {
        return sourceDirectory.resolve(path);
    }
    
    protected Path relativizeSource(Path path) {
        return sourceDirectory.relativize(path);
    }
    
    protected Path resolveTarget(Path path) {
        return targetDirectory.resolve(path);
    }

    protected void initialize() throws Exception {
        Files.createDirectories(targetDirectory());
    }
    
    protected void processResources() throws Exception {
    }

    protected void compile() throws Exception {
    }
}
