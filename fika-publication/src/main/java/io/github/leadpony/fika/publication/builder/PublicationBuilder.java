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

import java.nio.file.Path;

import io.github.leadpony.fika.publication.project.Project;
import io.github.leadpony.fika.publication.project.ResourceSet;

/**
 * Publication builder.
 * 
 * @author leadpony
 */
public interface PublicationBuilder {

    /**
     * Returns the name of this builder.
     * 
     * @return the name of this builder.
     */
    String name();
    
    /**
     * Returns the project for which this builder performs the build.
     * 
     * @return the project for which this builder performs the build.
     */
    Project project();
    
    default Project.Media media() {
        return project().media(name());
    }
    
    default ResourceSet resourceSet() {
        return media().resourceSet();
    }

    void setTargetDirectory(Path path);
    
    void setProperty(String name, Object value);
    
    void build();
}
