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

package io.github.leadpony.fika.core.project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.github.leadpony.fika.api.project.NavigationItem;
import io.github.leadpony.fika.api.project.PageSource;
import io.github.leadpony.fika.api.project.Project;

/**
 *
 */
public class BasicProject implements Project {
    
    private static final Path DEFAULT_SOURCE_DIRECTORY = Paths.get("docs");
    
    final Path path;
    Path sourceDirectory = DEFAULT_SOURCE_DIRECTORY;
    String title;
    String description;
    String url;
    String copyright;
    List<String> authors;
    NavigationItem navigationRoot;
    
    public BasicProject(Path path) {
        this.path = path.toAbsolutePath().normalize();
        this.authors = Collections.emptyList();
    }
    
    @Override
    public Path path() {
        return path;
    }
    
    @Override
    public Path sourceDirectory() {
        if (sourceDirectory.isAbsolute()) {
            return sourceDirectory;
        } else {
            Path base = path().getParent();
            return base.resolve(sourceDirectory);
        }
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String copyright() {
        return copyright;
    }

    @Override
    public List<String> authors() {
        return authors;
    }
    
    @Override
    public List<NavigationItem> navigationList() {
        if (navigationRoot == null) {
            return Collections.emptyList();
        }
        return navigationRoot.children(); 
    }
    
    @Override
    public List<PageSource> sources() {
        List<PageSource> sources = new ArrayList<>();
        collectPageSources(navigationRoot, sources);
        return Collections.unmodifiableList(sources);
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("path: ").append(path()).append('\n');
        b.append("sourceDirectory: ").append(sourceDirectory()).append('\n');
        b.append("title: ").append(title()).append('\n');
        b.append("description: ").append(description()).append('\n');
        b.append("url: ").append(url()).append('\n');
        b.append("copyright: ").append(copyright()).append('\n');
        b.append("authors:\n");
        for (String author: authors()) {
            b.append("  - ").append(author).append('\n');
        }
        b.append("pages:\n");
        b.append(navigationRoot.toString());
        return b.toString();
    }
    
    private static void collectPageSources(NavigationItem group, List<PageSource> sources) {
        for (NavigationItem child: group) {
            Optional<PageSource> source = child.pageSource();
            if (source.isPresent()) {
                sources.add(source.get());
            } else {
                collectPageSources(child, sources);
            }
        }
    }
}
