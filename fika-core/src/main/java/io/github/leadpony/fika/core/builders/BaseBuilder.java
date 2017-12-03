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
package io.github.leadpony.fika.core.builders;

import java.nio.file.Path;

import io.github.leadpony.fika.api.builders.Builder;
import io.github.leadpony.fika.api.project.Project;

/**
 * @author leadpony
 */
abstract class BaseBuilder implements Builder {
    
    private Project project; 
    private Path sourceDirectory;
    private Path targetDirectory;

    @Override
    public void setTargetDirectory(Path path) {
        targetDirectory = path.toAbsolutePath().normalize();
    }

    @Override
    public void setProperty(String name, Object value) {
    }

    @Override
    public void build(Project project) {
        this.project = project;
        this.sourceDirectory = project.sourceDirectory();
        doBuild();
    }
    
    protected Project project() {
        return project;
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
 
    protected abstract void doBuild();
}
