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
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

/**
 * The project for managing a set of documents.
 * 
 * @author leadpony
 */
public interface Project {
    
    /**
     * Returns the path to this project file.
     * 
     * @return absolute path to this project file.
     */
    Path path();
    
    /**
     * Returns the path to the source directory.
     * 
     * @return the path to the source directory.
     */
    Path sourceDirectory();
    
    /**
     * Returns the path to the target directory.
     * 
     * @return the path to the target directory.
     */
    Path targetDirectory();
    
    /**
     * Returns the path to the directory containing template files.
     * 
     * @return the path to the template directory.
     */
    Path templateDirectory();
    
    String getTitle();
    
    String getLanguage();
    
    String getDescription();
    
    URI getUrl();
    
    String getCopyright();
    
    List<String> authors();
    
    /**
     * Return the navigation list.
     * 
     * @return the list of the navigation items, never be {@code null}.
     */
    List<NavigationItem> navigationList();

    List<PageSource> sources();
    
    List<URI> stylesheets();
    
    Media media(String type);
    
    /**
     * Loads a project from file.
     * 
     * @param path the path to the file.
     * @return loaded project.
     * @throws NullPointerException if given {@code path} is {@code null}.
     * @throws IOException if I/O error has occurred while loading the project.
     */
    static Project loadFrom(Path path) throws IOException {
        if (path == null) {
            throw new NullPointerException("path must not be null.");
        }
        ProjectParser parser = new ProjectParser(path);
        return parser.parse();
    }

    public interface Media {
        
        String name();

        Path templateFile();
        
        ResourceSet resourceSet();
    }
}
