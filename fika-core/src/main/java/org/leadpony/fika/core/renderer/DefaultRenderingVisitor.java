/*
 * Copyright 2017-2018 the Fika authors.
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

import org.leadpony.fika.core.model.Admonition;
import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.core.model.BlockQuote;
import org.leadpony.fika.core.model.CodeBlock;
import org.leadpony.fika.core.model.CodeSpan;
import org.leadpony.fika.core.model.Definition;
import org.leadpony.fika.core.model.DefinitionList;
import org.leadpony.fika.core.model.Document;
import org.leadpony.fika.core.model.Emphasis;
import org.leadpony.fika.core.model.HardLineBreak;
import org.leadpony.fika.core.model.Heading;
import org.leadpony.fika.core.model.HtmlBlock;
import org.leadpony.fika.core.model.HtmlInline;
import org.leadpony.fika.core.model.Image;
import org.leadpony.fika.core.model.Link;
import org.leadpony.fika.core.model.ListBlock;
import org.leadpony.fika.core.model.ListItem;
import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.OrderedList;
import org.leadpony.fika.core.model.Paragraph;
import org.leadpony.fika.core.model.Term;
import org.leadpony.fika.core.model.Text;
import org.leadpony.fika.core.model.ThematicBreak;
import org.leadpony.fika.core.model.UnorderedList;
import org.leadpony.fika.core.model.Visitor;

/**
 * Default {@link Visitor} implementation which will render HTML content.
 * 
 * @author leadpony
 */
public class DefaultRenderingVisitor implements Visitor {
    
    private final XmlFormatter formatter;

    private static final String[] HEADINGS = { "h1", "h2", "h3", "h4", "h5", "h6" };
    
    public DefaultRenderingVisitor(XmlFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void visit(Admonition node) {
        AttributeMap attributes = new AttributeMap();
        final String typeQualifier = node.getType().toLowerCase();
        attributes.addClass("admonition").addClass(typeQualifier);
        formatter.startTag("aside", attributes);
        String title = node.getTitle();
        if (!title.isEmpty()) {
            formatter.startTag("header");
            formatter.startTag("p");
            formatter.text(title);
            formatter.endTag("p");
            formatter.endTag("header");
        }
        visitChildren(node);
        formatter.endTag("aside");
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
        AttributeMap attributes = new AttributeMap();
        String language = node.getLanguage();
        if (language != null) {
            String classValue = "language-" + language;
            attributes.addClass(classValue);
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
    public void visit(Definition node) {
        formatter.startTag("dd");
        visitListChildren(node);
        formatter.endTag("dd");
    }

    @Override
    public void visit(DefinitionList node) {
        formatter.startTag("dl");
        visitChildren(node);
        formatter.endTag("dl");
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
        AttributeMap attributes = new AttributeMap();
        attributes.add("src", node.getLocation());
        attributes.add("title", node.getTitle());
        attributes.add("alt", node.textContent());
        formatter.emptyTag("img", attributes);
    }

    @Override
    public void visit(Link node) {
        AttributeMap attributes = new AttributeMap();
        attributes.add("href", node.getDestination());
        attributes.add("title", node.getTitle());
        formatter.startTag("a", attributes);
        visitChildren(node);
        formatter.endTag("a");
    }

    @Override
    public void visit(ListItem node) {
        formatter.startTag("li");
        visitListChildren(node);
        formatter.endTag("li");
    }

    @Override
    public void visit(OrderedList node) {
        AttributeMap attributes = new AttributeMap();
        int startNumber = node.getStartNumber();
        if (startNumber != 1) {
            attributes.add("start", String.valueOf(startNumber));
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
    public void visit(Term node) {
        formatter.startTag("dt");
        visitChildren(node);
        formatter.endTag("dt");
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
    
    private static boolean testTightness(Block node) {
        Node parent = node.parentNode();
        if (parent == null || !(parent instanceof ListBlock)) {
            return false;
        }
        return ((ListBlock)parent).isTight();
    }
    
    private void visitListChildren(Block node) {
        if (testTightness(node)) {
            visitChildrenButParagraph(node);
        } else {
            visitChildren(node);
        }
    }
    
    private void visitChildrenButParagraph(Block node) {
        for (Node child: node.childNodes()) {
            if (child instanceof Paragraph) {
                visitChildren(child);
            } else {
                child.accept(this);
            }
        }
    }
}
