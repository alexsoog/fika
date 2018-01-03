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

import java.util.HashMap;
import java.util.Map;

import io.github.leadpony.fika.core.model.BlockQuote;
import io.github.leadpony.fika.core.model.CodeBlock;
import io.github.leadpony.fika.core.model.CodeSpan;
import io.github.leadpony.fika.core.model.Document;
import io.github.leadpony.fika.core.model.Emphasis;
import io.github.leadpony.fika.core.model.HardLineBreak;
import io.github.leadpony.fika.core.model.Heading;
import io.github.leadpony.fika.core.model.HtmlBlock;
import io.github.leadpony.fika.core.model.HtmlInline;
import io.github.leadpony.fika.core.model.Link;
import io.github.leadpony.fika.core.model.ListItem;
import io.github.leadpony.fika.core.model.OrderedList;
import io.github.leadpony.fika.core.model.Paragraph;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.core.model.ThematicBreak;
import io.github.leadpony.fika.core.model.UnorderedList;
import io.github.leadpony.fika.core.model.Visitor;

/**
 * @author leadpony
 */
class HtmlRenderingVisitor implements Visitor {
    
    protected final HtmlFormatter formatter;

    private static final String[] HEADINGS = { "h1", "h2", "h3", "h4", "h5", "h6" };
    
    HtmlRenderingVisitor(HtmlFormatter formatter) {
        this.formatter = formatter;
    }
    
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
        formatter.rawHtml(node.getHtml());
    }
    
    @Override
    public void visit(HtmlInline node) {
        formatter.rawHtml(node.getHtml());
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
}
