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

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Default implementation of {@link Project}.
 * 
 * @author leadpony
 */
class DefaultProject implements Project {
    
    private static final Logger log = Logger.getLogger(DefaultProject.class.getName());
    
    private static final Path DEFAULT_SOURCE_DIRECTORY = Paths.get("docs");
    private static final Path DEFAULT_TARGET_DIRECTORY = Paths.get("output");
    
    @SuppressWarnings("serial")
    private static final Set<String> DEFAULT_RESOURCE_EXTENSIONS = new HashSet<String>() {{
        add("png");
        add("jpg");
        add("jpeg");
        add("gif");
        add("css");
        add("js");
    }};
    
    private final Path path;
    private final Path sourceDirectory = DEFAULT_SOURCE_DIRECTORY;
    private final Path targetDirectory = DEFAULT_TARGET_DIRECTORY;
    private final String title;
    private final Locale language;
    private final String description;
    private final URI url;
    private final String copyright;
    private final List<String> authors;
    private final NavigationItem navigationRoot;
    private final List<URI> stylesheets;
    private final Set<String> extensions = DEFAULT_RESOURCE_EXTENSIONS;
    
    static Builder builder(Path path) {
        return new Builder(path);
    }
    
    private DefaultProject(Builder builder) {
        this.path = builder.path;
        this.title = builder.title;
        this.language = builder.language;
        this.description = builder.description;
        this.url = builder.url;
        this.copyright = builder.copyright;
        this.authors = builder.authors;
        this.stylesheets = builder.stylesheets;
        this.navigationRoot = builder.navigationRoot;
    }
    
    @Override
    public Path path() {
        return path;
    }
    
    @Override
    public Path sourceDirectory() {
        return resolvePath(sourceDirectory);
    }

    @Override
    public Path targetDirectory() {
        return resolvePath(targetDirectory);
    }
    
    @Override
    public String title() {
        return title;
    }

    @Override
    public Locale language() {
        return language;
    }
    
    @Override
    public String description() {
        return description;
    }

    @Override
    public URI url() {
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
    public List<URI> stylesheets() {
        return stylesheets;
    }
    
    @Override
    public Set<String> resourceExtensions() {
        return extensions;
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("path: ").append(path()).append('\n');
        b.append("sourceDirectory: ").append(sourceDirectory()).append('\n');
        b.append("targetDirectory: ").append(targetDirectory()).append('\n');
        b.append("title: ").append(title()).append('\n');
        b.append("language: ").append(language().toLanguageTag()).append('\n');
        b.append("description: ").append(description()).append('\n');
        b.append("url: ").append(url()).append('\n');
        b.append("copyright: ").append(copyright()).append('\n');
        b.append("authors:\n");
        for (String author: authors()) {
            b.append("  - ").append(author).append('\n');
        }
        b.append("pages:\n");
        b.append(navigationRoot.toString());
        b.append("stylesheets:\n");
        for (URI stylesheet: stylesheets()) {
            b.append("  - ").append(stylesheet).append('\n');
        }
        return b.toString();
    }
    
    private Path resolvePath(Path path) {
        if (path.isAbsolute()) {
            return path;
        } else {
            Path parent = path().getParent();
            return parent.resolve(path);
        }
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
    
    static class Builder implements ProjectParseListener {
        
        private final Path path;
        private String title = "(Untitled)";
        private Locale language = Locale.getDefault();
        private String description;
        private URI url;
        private String copyright;
        private List<String> authors = Collections.emptyList();
        private List<URI> stylesheets = Collections.emptyList();
        private NavigationItem navigationRoot;
        
        private final Deque<List<NavigationItem>> childrenStack = new ArrayDeque<>();
        private NavigationGroup currentGroup;
        
        private Builder(Path path) {
            this.path = path.toAbsolutePath().normalize();
        }
 
        @Override
        public void title(String value) {
            this.title = value;
        }

        @Override
        public void language(Locale  value) {
            this.language = value;
        }

        @Override
        public void description(String value) {
            this.description = value;
        }

        @Override
        public void url(URI value) {
            this.url = value;
        }

        @Override
        public void copyright(String value) {
            this.copyright = value;
        }

        @Override
        public void authors(List<String> values) {
            this.authors = Collections.unmodifiableList(values);
        }
        
        @Override
        public void stylesheets(List<URI> values) {
            this.stylesheets = Collections.unmodifiableList(values);
        }

        @Override
        public void enterPages() {
            this.currentGroup = NavigationGroup.root();
            this.childrenStack.push(new ArrayList<>());
        }
        
        @Override
        public void exitPages() {
            this.navigationRoot = finishGroup();
        }

        @Override
        public void enterNavigationGroup(String label) {
            NavigationGroup group = new NavigationGroup(label, currentGroup);
            addNavigationChild(group);
            currentGroup = group;
            childrenStack.push(new ArrayList<>());
        }

        @Override
        public void exitNavigationGroup() {
            finishGroup();
        }
        
        @Override
        public void navigationLeaf(String label, String path) {
            DefaultPageSource page = new DefaultPageSource(Paths.get(path));
            NavigationLeaf item = new NavigationLeaf(label, page, currentGroup);
            addNavigationChild(item);
        }
        
        private void addNavigationChild(NavigationItem child) {
            List<NavigationItem> children = childrenStack.peek();
            children.add(child);
        }
        
        private NavigationGroup finishGroup() {
            NavigationGroup group = currentGroup;
            List<NavigationItem> children = childrenStack.pop();
            currentGroup.setChildren(children);
            currentGroup = currentGroup.parentItem();
            return group;
        }
        
        Project build() {
            Project project = new DefaultProject(this);
            log.fine("Project:\n" + project.toString());
            return project;
        }
    }
}
