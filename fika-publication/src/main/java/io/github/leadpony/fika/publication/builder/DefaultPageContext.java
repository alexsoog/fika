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
import java.nio.file.Paths;

import io.github.leadpony.fika.core.model.Document;
import io.github.leadpony.fika.core.renderer.HtmlRenderer;
import io.github.leadpony.fika.publication.view.PageContext;

/**
 * Default implementation of {@link PageContext}.
 * 
 * @author leadpony
 */
public class DefaultPageContext implements PageContext {
    
    private final String url;
    private final Document document;
    private String content;
    
    public DefaultPageContext(Document document, String url) {
        this.document = document;
        this.url = url;
    }
    
    @Override
    public String getUrl() {
        return url;
    }
    
    @Override
    public String getContent() {
        if (content == null) {
            content = renderContent();
        }
        return content;
    }
    
    @Override
    public String relativizeUrl(String other) {
        Path basePath = Paths.get(url);
        Path parentPath = basePath.getParent();
        if (parentPath == null) {
            return other;
        }
        Path otherPath = Paths.get(other);
        Path relative = parentPath.relativize(otherPath);
        return pathToUrl(relative);
    }
    
    private static String pathToUrl(Path path) {
        return path.toString().replace("\\", "/");
    }
    
    private String renderContent() {
        HtmlRenderer.Builder builder = HtmlRenderer.builder();
        builder.withOption(HtmlRenderer.Option.HTML_FRAGMENT);
        HtmlRenderer renderer = builder.build();
        return renderer.render(this.document);
    }
}
