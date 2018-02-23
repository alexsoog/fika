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
package org.leadpony.fika.core.renderer;

import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.Visitor;

/**
 * Skeletal implementation of {@link HtmlRenderer}.
 * 
 * @author leadpony
 */
public abstract class AbstractHtmlRenderer implements HtmlRenderer {
   
    private final Set<Option> options;
    private final Charset charset;
    private final Locale language;
    private final String title;
    private final List<URI> stylesheets;
    private final XmlFormatter formatter;
    private final Visitor visitor;
 
    protected AbstractHtmlRenderer(Builder builder) {
        this.options = Collections.unmodifiableSet(builder.options);
        this.charset = builder.charset;
        this.language = builder.language;
        this.title = builder.title;
        this.stylesheets = Collections.unmodifiableList(builder.stylesheet);
        this.formatter = (builder.formatter != null) ?
                builder.formatter :
                new MinimalXmlFormatter();
        this.visitor = buildVisitor(this.formatter);
    }
    
    @Override
    public void render(Node node, Writer writer) {
        this.formatter.setWriter(writer);
        if (hasOption(Option.HTML_FRAGMENT)) {
            renderContent(this.formatter, node);
        } else {
            renderFull(this.formatter, node);
        }
    }
 
    /* helper methods */

    protected boolean hasOption(Option option) {
        return options.contains(option);
    }
    
    protected XmlFormatter formatter() {
        return formatter;
    }
    
    protected void renderFull(XmlFormatter formatter, Node node) {
        formatter.doctype("html");
        Map<String, String> attributes = new HashMap<>();
        if (this.language != null) {
            attributes.put("lang", this.language.toLanguageTag());
        }
        formatter.startTag("html", attributes);
        renderHead(formatter);
        renderBody(formatter, node);
        formatter.endTag("html");
    }
    
    protected void renderHead(XmlFormatter formatter) {
        formatter.startTag("head");
        renderMeta(formatter, "charset", charset.toString());
        renderTitle(formatter, this.title);
        renderStylesheets(formatter);
        formatter.endTag("head");
    }
    
    protected void renderBody(XmlFormatter formatter, Node node) {
        formatter.startTag("body");
        renderContent(formatter, node);
        formatter.endTag("body");
    }
    
    protected void renderMeta(XmlFormatter formatter, String key, String value) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(key, value);
        formatter.emptyTag("meta", attributes);
    }
    
    protected void renderStylesheets(XmlFormatter formatter) {
        for (URI stylesheet: this.stylesheets) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("href", stylesheet.toString());
            attributes.put("rel", "stylesheet");
            formatter.emptyTag("link", attributes);
        }
    }
    
    protected void renderTitle(XmlFormatter formatter, String title) {
        if (title != null) {
            formatter.startTag("title");
            formatter.text(this.title);
            formatter.endTag("title");
        }
    }
    
    protected void renderContent(XmlFormatter formatter, Node node) {
        node.accept(this.visitor);
    }
    
    protected Visitor buildVisitor(XmlFormatter formatter) {
        return new DefaultRenderingVisitor(formatter);
    }

    /**
     * Default implementation of {@link HtmlRenderer.Builder}.
     * 
     * @author leadpony
     */
    public static abstract class Builder implements HtmlRenderer.Builder {

        private final Set<Option> options = EnumSet.noneOf(Option.class);
        private Charset charset = StandardCharsets.UTF_8;
        private Locale language;
        private String title;
        private final List<URI> stylesheet = new ArrayList<>();
        private XmlFormatter formatter;
        
        @Override
        public Builder withOption(Option option) {
            Objects.requireNonNull(option, "option must not be null");
            options.add(option);
            return this;
        }

        @Override
        public Builder withoutOption(Option option) {
            Objects.requireNonNull(option, "option must not be null");
            options.remove(option);
            return this;
        }
        
        @Override
        public Builder withCharset(Charset charset) {
            Objects.requireNonNull(charset, "charset must not be null");
            this.charset = charset;
            return this;
        }
  
        @Override
        public Builder withLanguage(Locale language) {
            Objects.requireNonNull(language, "language must not be null");
            this.language = language;
            return this;
        }
        
        @Override
        public Builder withTitle(String title) {
            Objects.requireNonNull(title, "title must not be null");
            this.title = title;
            return this;
        }
 
        @Override
        public Builder withStylesheet(URI stylesheet) {
            Objects.requireNonNull(stylesheet, "stylesheet must not be null");
            this.stylesheet.add(stylesheet);
            return this;
        }
       
        @Override
        public Builder withFormatter(XmlFormatter formatter) {
            Objects.requireNonNull(formatter, "formatter must not be null");
            this.formatter = formatter;
            return this;
        }
    }
}
