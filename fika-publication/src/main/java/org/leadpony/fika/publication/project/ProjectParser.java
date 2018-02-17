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
package org.leadpony.fika.publication.project;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.leadpony.fika.publication.common.MediaTypeRegistry;
import org.yaml.snakeyaml.Yaml;

/**
 * @author leadpony
 */
public class ProjectParser {
    
    private static final Logger log = Logger.getLogger(ProjectParser.class.getName());
    
    private static final String DEFAULT_SOURCE_PATH = "docs";
    private static final String DEFAULT_TARGET_PATH = "output";
    private static final String DEFAULT_TEMPLATE_PATH = "template";
    
    private final Path path;
    
    private final List<PageSource> pageSources = new ArrayList<>();
    
    public ProjectParser(Path path) {
        this.path = path;
    }
    
    public Project parse() throws IOException {
        Project project = parseProject(loadYaml());
        log.info(project.toString());
        return project;
    }
    
    private Object loadYaml() throws IOException {
        try (InputStream in = Files.newInputStream(this.path)) {
            Yaml yaml = new Yaml();
            return yaml.load(in);
        }
    }

    private Project parseProject(Object root) {
        assertType("root", root, Map.class);
        ProjectImpl project = new ProjectImpl(this.path);
        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>)root;
        for (Map.Entry<String, ?> entry: map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            switch (key) {
            case "title":
                project.title = valueAsString(key, value);
                break;
            case "language":
                project.language = valueAsString(key, value);
                break;
            case "description":
                project.description = valueAsString(key, value);
                break;
            case "url":
                project.url = valueAsUri(key, value);
                break;
            case "copyright":
                project.copyright = valueAsString(key, value);
                break;
            case "authors":
                project.authors = valueAsStringList(key, value);
                break;
            case "stylesheets":
                project.stylesheets = valueAsUriList(key, value);
                break;
            case "pages":
                project.navigationList = parsePages(key, value);
                project.pageSources = Collections.unmodifiableList(this.pageSources);
                break;
            case "media":
                project.media = parseMediaMap(key, value);
                break;
            }
        }
        return fillProject(project);
    }
    
    private List<NavigationItem> parsePages(String key, Object value) {
        assertType(key, value, List.class);
        return parsePageList((List<?>)value, null);
    }
    
    private List<NavigationItem> parsePageList(List<?> list, GroupNavigationItem parent) {
        List<NavigationItem> items = new ArrayList<>();
        for (Object item: list) {
            items.add(parsePage(item, parent));
        }
        return Collections.unmodifiableList(items);
    }
    
    private NavigationItem parsePage(Object value, GroupNavigationItem parent) {
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>)value;
            return parseLabeledPage(map, parent);
        } else {
            assertType("page path", value, String.class);
            return newLeafNavigationItem(null, value.toString(), parent);
        }
    }
    
    private NavigationItem parseLabeledPage(Map<String, ?> map, GroupNavigationItem parent) {
        if (map.size() != 1) {
            // TODO:
        }
        Map.Entry<String, ?> entry = map.entrySet().iterator().next();
        String label = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof List) {
            GroupNavigationItem group = newGroupNavigationItem(label, parent);
            group.children = parsePageList((List<?>)value, group);
            return group;
        } else {
            assertType("page path", value, String.class);
            return newLeafNavigationItem(label, value.toString(), parent);
        }
    }
    
    private Map<String, Project.Media> parseMediaMap(String key, Object value) {
        assertType(key, value, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, ?> valueAsMap = (Map<String, ?>)value;
        Map<String, Media> mediaMap = newMediaMap();
        for (Map.Entry<String, ?> entry: valueAsMap.entrySet()) {
            String type = entry.getKey();
            Media media = mediaMap.get(type);
            if (media == null) {
                media = new Media(type);
                mediaMap.put(type, media);
            }
            parseMedia(type, entry.getValue(), media);
        }
        return Collections.unmodifiableMap(mediaMap);
    }
    
    private Media parseMedia(String key, Object value, Media media) {
        assertType(key, value, Map.class);
        for (Map.Entry<String, ?> entry: valueAsMap(key, value).entrySet()) {
            switch (entry.getKey()) {
            case "template":
                media.templateFile = valueAsPath(entry.getKey(), entry.getValue());
                break;
            }
        }
        return media;
    }
    
    private static void assertType(String key, Object value, Class<?> type) {
        if (!type.isInstance(value)) {
            throw new ProjectParseException("Unexpected type for value of \"" + key + "\".");
        }
    }
    
    @SuppressWarnings("unchecked")
    private static Map<String, ?> valueAsMap(String name, Object value) {
        assertType(name, value, Map.class);
        return (Map<String, ?>)value;
    }
    
    private static String valueAsString(String name, Object value) {
        if (value instanceof Map || value instanceof List) {
        }
        return value.toString();
    }
    
    private static URI valueAsUri(String name, Object value) {
        return URI.create(valueAsString(name, value));
    }
    
    private static Path valueAsPath(String name, Object value) {
        return Paths.get(valueAsString(name, value));
    }
    
    private static List<String> valueAsStringList(String name, Object value) {
        if (value instanceof Map) {
        }
        List<String> list = new ArrayList<>();
        if (value instanceof List) {
            for (Object item: (List<?>)value) {
                if (item instanceof List || item instanceof Map) {
                }
                list.add(item.toString());
            }
        } else {
            list.add(value.toString());
        }
        return Collections.unmodifiableList(list);
    }

    private static List<URI> valueAsUriList(String name, Object value) {
        if (value instanceof Map) {
        }
        List<URI> list = new ArrayList<>();
        if (value instanceof List) {
            for (Object item: (List<?>)value) {
                assertType(name, item, String.class);
                list.add(URI.create(item.toString()));
            }
        } else {
            assertType(name, value, String.class);
            list.add(URI.create(value.toString()));
        }
        return Collections.unmodifiableList(list);
    }
    
    private LeafNavigationItem newLeafNavigationItem(String label, String value, GroupNavigationItem parent) {
        return new LeafNavigationItem(label, newPageSource(value), parent);
    }

    private GroupNavigationItem newGroupNavigationItem(String label, GroupNavigationItem parent) {
        return new GroupNavigationItem(label, parent);
    }
    
    private PageSource newPageSource(String pathValue) {
        Path path = Paths.get(pathValue);
        String mediaType = MediaTypeRegistry.get().guessMediaType(path);
        PageSource source = new PageSourceImpl(path, mediaType);
        this.pageSources.add(source);
        return source;
    }
    
    private Map<String, Media> newMediaMap() {
        Map<String, Media> mediaMap = new HashMap<>();
        mediaMap.put("site", newSiteMedia());
        return mediaMap;
    }
    
    private Media newSiteMedia() {
        return new Media("site", newDefaultResourceSet());
    }
    
    private ResourceSet newDefaultResourceSet() {
        DefaultResourceSet rs = new DefaultResourceSet()
            .include("**/*.png")
            .include("**/*.jpg")
            .include("**/*.jpeg")
            .include("**/*.gif")
            .include("**/*.eot")
            .include("**/*.ttf")
            .include("**/*.svg")
            .include("**/*.woff")
            .include("**/*.woff2")
            .include("**/*.css")
            .include("**/*.js")
            ;
        return rs;
    }
    
    private ProjectImpl fillProject(ProjectImpl project) {
        if (project.media == null) {
            project.media = Collections.unmodifiableMap(newMediaMap());
        }
        return project;
    }
    
    private static class ProjectImpl implements Project {
        
        private final Path path;
        private Path sourcePath;
        private Path targetPath;
        private Path templatePath;
        private String title;
        private String language;
        private String description;
        private URI url;
        private String copyright;
        private List<String> authors;
        private List<URI> stylesheets;
        private List<NavigationItem> navigationList;
        private List<PageSource> pageSources;
        private Map<String, Media> media;
        
        ProjectImpl(Path path) {
            this.path = path.toAbsolutePath().normalize();
            Path parent = this.path.getParent();
            this.sourcePath = parent.resolve(DEFAULT_SOURCE_PATH);
            this.targetPath = parent.resolve(DEFAULT_TARGET_PATH);
            this.templatePath = parent.resolve(DEFAULT_TEMPLATE_PATH);
            this.title = "(Untitled)";
            this.language = Locale.getDefault().toLanguageTag();
            this.authors = Collections.emptyList();
            this.stylesheets = Collections.emptyList();
            this.navigationList = Collections.emptyList();
            this.media = Collections.emptyMap();
        }

        @Override
        public Path path() {
            return path;
        }

        @Override
        public Path sourceDirectory() {
            return sourcePath;
        }

        @Override
        public Path targetDirectory() {
            return targetPath;
        }

        @Override
        public Path templateDirectory() {
            return templatePath;
        }
        
        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getLanguage() {
            return language;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public URI getUrl() {
            return url;
        }

        @Override
        public String getCopyright() {
            return copyright;
        }

        @Override
        public List<String> authors() {
            return authors;
        }

        @Override
        public List<NavigationItem> navigationList() {
            return navigationList;
        }

        @Override
        public List<PageSource> sources() {
            return pageSources;
        }

        @Override
        public List<URI> stylesheets() {
            return stylesheets;
        }

        @Override
        public Media media(String type) {
            if (type == null) {
                throw new NullPointerException("type must not be null.");
            }
            return this.media.get(type);
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("path: ").append(path()).append('\n');
            b.append("sourceDirectory: ").append(sourceDirectory()).append('\n');
            b.append("targetDirectory: ").append(targetDirectory()).append('\n');
            b.append("title: ").append(getTitle()).append('\n');
            b.append("language: ").append(getLanguage()).append('\n');
            b.append("description: ").append(getDescription()).append('\n');
            b.append("url: ").append(getUrl()).append('\n');
            b.append("copyright: ").append(getCopyright()).append('\n');
            b.append("authors:\n");
            for (String author: authors()) {
                b.append("  - ").append(author).append('\n');
            }
            b.append("pages:\n");
            b.append(new NavigationItemPrinter().print(navigationList, 1));
            b.append("stylesheets:\n");
            for (URI stylesheet: stylesheets()) {
                b.append("  - ").append(stylesheet).append('\n');
            }
            return b.toString();
        }
    }
    
    private static class PageSourceImpl implements PageSource {
        
        private final Path path;
        private final String mediaType;
        
        PageSourceImpl(Path path, String mediaType) {
            this.path = path;
            this.mediaType = mediaType;
        }

        @Override
        public Path path() {
            return path;
        }

        @Override
        public String mediaType() {
            return mediaType;
        }

        @Override
        public String toString() {
            return path.toString();
        }
    }
    
    private static abstract class AbstractNavigationItem implements NavigationItem {
        
        private final Optional<String> label;
        private final AbstractNavigationItem parent;
        
        AbstractNavigationItem(String label, AbstractNavigationItem parent) {
            this.label = Optional.ofNullable(label);
            this.parent = parent;
        }

        @Override
        public Optional<String> label() {
            return label;
        }

        @Override
        public NavigationItem parentItem() {
            return parent;
        }
    }
    
    private static class LeafNavigationItem extends AbstractNavigationItem {
        
        private final Optional<PageSource> source;
        
        LeafNavigationItem(String label, PageSource source, GroupNavigationItem parent) {
            super(label, parent);
            this.source = Optional.of(source);
        }

        @Override
        public Optional<PageSource> pageSource() {
            return source;
        }

        @Override
        public List<NavigationItem> children() {
            return Collections.emptyList();
        }
    }
    
    private static class GroupNavigationItem extends AbstractNavigationItem {
    
        private List<NavigationItem> children;
        
        GroupNavigationItem(String label, GroupNavigationItem parent) {
            super(label, parent);
            this.children = Collections.emptyList();
        }

        @Override
        public Optional<PageSource> pageSource() {
            return Optional.empty();
        }

        @Override
        public List<NavigationItem> children() {
            return children;
        }
    }
    
    private static class Media implements Project.Media {
        
        private final String type;
        private Path templateFile;
        private ResourceSet resourceSet;
        
        private Media(String type) {
            this(type, null);
        }
        
        private Media(String type, ResourceSet resourceSet) {
            this.type = type;
            this.resourceSet = resourceSet;
        }

        @Override
        public String name() {
            return type;
        }

        @Override
        public Path templateFile() {
            return templateFile;
        }
        
        @Override
        public ResourceSet resourceSet() {
            return resourceSet;
        }
    }
}
