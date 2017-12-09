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

package io.github.leadpony.fika.publication.project;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * The project for managing a set of documents.
 * 
 * @author leadpony
 */
public interface Project {
    
    Path path();
    
    Path sourceDirectory();
    
    String title();
    
    String description();
    
    String url();
    
    String copyright();
    
    List<String> authors();
    
    /**
     * Return the navigation list.
     * 
     * @return the list of the navigation items, never be {@code null}.
     */
    List<NavigationItem> navigationList();

    List<PageSource> sources();
    
    /**
     * Loads a project from file.
     * 
     * @param path the path to the file.
     * @return loaded project.
     * @throws IOException if I/O error has occurred.
     */
    static Project loadFrom(Path path) throws IOException {
        return ProjectFactory.get().load(path);
    }
}
