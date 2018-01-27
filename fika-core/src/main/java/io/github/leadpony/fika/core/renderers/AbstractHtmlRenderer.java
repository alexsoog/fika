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
package io.github.leadpony.fika.core.renderers;

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
import java.util.Set;

import io.github.leadpony.fika.core.model.BlockQuote;
import io.github.leadpony.fika.core.model.CodeBlock;
import io.github.leadpony.fika.core.model.CodeSpan;
import io.github.leadpony.fika.core.model.Document;
import io.github.leadpony.fika.core.model.Emphasis;
import io.github.leadpony.fika.core.model.HardLineBreak;
import io.github.leadpony.fika.core.model.Heading;
import io.github.leadpony.fika.core.model.HtmlBlock;
import io.github.leadpony.fika.core.model.HtmlInline;
import io.github.leadpony.fika.core.model.Image;
import io.github.leadpony.fika.core.model.Link;
import io.github.leadpony.fika.core.model.ListItem;
import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.OrderedList;
import io.github.leadpony.fika.core.model.Paragraph;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.core.model.ThematicBreak;
import io.github.leadpony.fika.core.model.UnorderedList;
import io.github.leadpony.fika.core.model.Visitor;

/**
 * Skeletal implementation of {@link HtmlRenderer}.
 * 
 * @author leadpony
 */
public abstract class AbstractHtmlRenderer implements HtmlRenderer, Visitor {
   
    private final Set<Option> options;
    private final Charset charset;
    private final Locale language;
    private final String title;
    private final List<URI> stylesheets;
    private final XmlFormatter formatter;
 
    private static final String[] HEADINGS = { "h1", "h2", "h3", "h4", "h5", "h6" };
    
    protected AbstractHtmlRenderer(Builder builder) {
        this.options = Collections.unmodifiableSet(builder.options);
        this.charset = builder.charset;
        this.language = builder.language;
        this.title = builder.title;
        this.stylesheets = Collections.unmodifiableList(builder.stylesheet);
        this.formatter = (builder.formatter != null) ?
                builder.formatter :
                new MinimalXmlFormatter();
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
        renderHeader(formatter);
        renderContent(formatter, node);
        renderFooter(formatter);
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
    
    protected void renderHeader(XmlFormatter formatter) {
    }

    protected void renderFooter(XmlFormatter formatter) {
    }

    protected void renderContent(XmlFormatter formatter, Node node) {
        node.accept(this);
    }
    
    /* Visitor interface */
    
    @Override
    public void visit(BlockQuote node) {
        formatter.startTag("blockquote");
        visitChildren(node);
        formatter.endTag("blockquote");
    }

    @Override
    public void visit(CodeBlock node) {
        formatter.startTag("pre");
        Map<String, String> attributes = new HashMap<>();
        String language = node.getLanguage();
        if (language != null) {
            String classValue = "language-" + language;
            attributes.put("class", classValue);
        }
        formatter.startTag("code", attributes);
        formatter.text(node.getContent());
        formatter.endTag("code");
        formatter.endTag("pre");
    }
    
    @Override
    public void visit(CodeSpan node) {
        formatter.startTag("code");
        formatter.text(node.getContent());
        formatter.endTag("code");
    }
    
    @Override
    public void visit(Document node) {
        visitChildren(node);
    }

    @Override
    public void visit(Emphasis node) {
        String tagName = (node.getStrength() > 1) ? "strong" : "em";
        formatter.startTag(tagName);
        visitChildren(node);
        formatter.endTag(tagName);
    }
    
    @Override
    public void visit(HardLineBreak node) {
        formatter.emptyTag("br");
    }

    @Override
    public void visit(Heading node) {
        String tagName = HEADINGS[node.getLevel() - 1];
        formatter.startTag(tagName);
        visitChildren(node);
        formatter.endTag(tagName);
    }
    
    @Override
    public void visit(HtmlBlock node) {
        formatter.rawXml(node.getContent());
    }
    
    @Override
    public void visit(HtmlInline node) {
        formatter.rawXml(node.getContent());
    }

    @Override
    public void visit(Image node) {
        String title = node.getTitle();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("src", node.getLocation());
        if (title != null) {
            attributes.put("title", title);
        }
        String alt = node.textContent();
        if (alt != null) {
            attributes.put("alt", alt);
        }
        formatter.emptyTag("img", attributes);
    }

    @Override
    public void visit(Link node) {
        String title = node.getTitle();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("href", node.getDestination());
        if (title != null) {
            attributes.put("title", title);
        }
        formatter.startTag("a", attributes);
        visitChildren(node);
        formatter.endTag("a");
    }

    @Override
    public void visit(ListItem node) {
        formatter.startTag("li");
        visitChildren(node);
        formatter.endTag("li");
    }

    @Override
    public void visit(OrderedList node) {
        Map<String, String> attributes = new HashMap<>();
        int startNumber = node.getStartNumber();
        if (startNumber != 1) {
            attributes.put("start", String.valueOf(startNumber));
        }
        formatter.startTag("ol", attributes);
        visitChildren(node);
        formatter.endTag("ol");
    }

    @Override
    public void visit(Paragraph node) {
        formatter.startTag("p");
        visitChildren(node);
        formatter.endTag("p");
    }

    @Override
    public void visit(Text node) {
        formatter.text(node.getContent());
    }

    @Override
    public void visit(ThematicBreak node) {
        formatter.emptyTag("hr");
    }

    @Override
    public void visit(UnorderedList node) {
        formatter.startTag("ul");
        visitChildren(node);
        formatter.endTag("ul");
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
            if (option == null) {
                throw new NullPointerException("option must not be null.");
            }
            options.add(option);
            return this;
        }

        @Override
        public Builder withoutOption(Option option) {
            if (option == null) {
                throw new NullPointerException("option must not be null.");
            }
            options.remove(option);
            return this;
        }
        
        @Override
        public Builder withCharset(Charset charset) {
            if (charset == null) {
                throw new NullPointerException("charset must not be null.");
            }
            this.charset = charset;
            return this;
        }
  
        @Override
        public Builder withLanguage(Locale language) {
            if (language == null) {
                throw new NullPointerException("language must not be null.");
            }
            this.language = language;
            return this;
        }
        
        @Override
        public Builder withTitle(String title) {
            if (title == null) {
                throw new NullPointerException("title must not be null.");
            }
            this.title = title;
            return this;
        }
 
        @Override
        public Builder withStylesheet(URI stylesheet) {
            if (stylesheet == null) {
                throw new NullPointerException("stylesheet must not be null.");
            }
            this.stylesheet.add(stylesheet);
            return this;
        }
       
        @Override
        public Builder withFormatter(XmlFormatter formatter) {
            if (formatter == null) {
                throw new NullPointerException("formatter must not be null.");
            }
            this.formatter = formatter;
            return this;
        }
    }
}
