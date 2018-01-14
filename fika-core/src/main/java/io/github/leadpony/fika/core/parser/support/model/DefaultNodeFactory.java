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
package io.github.leadpony.fika.core.parser.support.model;

import java.net.URI;

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
import io.github.leadpony.fika.core.model.ListBlock;
import io.github.leadpony.fika.core.model.ListItem;
import io.github.leadpony.fika.core.model.ListType;
import io.github.leadpony.fika.core.model.NodeFactory;
import io.github.leadpony.fika.core.model.OrderedList;
import io.github.leadpony.fika.core.model.Paragraph;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.core.model.ThematicBreak;
import io.github.leadpony.fika.core.model.UnorderedList;

/**
 * Default implementation of {@link NodeFactory}.
 * 
 * @author leadpony
 */
public class DefaultNodeFactory implements NodeFactory {
    
    public DefaultNodeFactory() {
    }

    @Override
    public BlockQuote newBlockQuote() {
        return new BlockQuoteImpl(this);
    }

    @Override
    public CodeBlock newCodeBlock(String content) {
        return new CodeBlockImpl(this, content);
    }
    
    @Override
    public CodeSpan newCodeSpan(String content) {
        return new CodeSpanImpl(this, content);
    }

    @Override
    public Document newDocument() {
        return new DocumentImpl(this);
    }

    @Override
    public Emphasis newEmphasis(int strength) {
        return new EmphasisImpl(this, strength);
    }

    @Override
    public HardLineBreak newHardLineBreak() {
        return new HardLineBreakImpl(this);
    }

    @Override
    public Heading newHeading(int level) {
        return new HeadingImpl(this, level);
    }

    @Override
    public HtmlBlock newHtmlBlock(String content) {
        return new HtmlBlockImpl(this, content);
    }
    
    @Override
    public HtmlInline newHtmlInline(String content) {
        return new HtmlInlineImpl(this, content);
    }

    @Override
    public Image newImage() {
        return new ImageImpl(this);
    }

    @Override
    public Link newLink() {
        return new LinkImpl(this);
    }

    @Override
    public ListBlock newLiskBlock(ListType type) {
        switch (type) {
        case ORDERED:
            return new OrderedListImpl(this);
        case UNORDERED:
            return new UnorderedListImpl(this);
        }
        return null;
    }

    @Override
    public ListItem newListItem() {
        return new ListItemImpl(this);
    }

    @Override
    public Paragraph newParagraph() {
        return new ParagraphImpl(this);
    }

    @Override
    public Text newText(String content) {
        return new TextImpl(this, content);
    }

    @Override
    public ThematicBreak newThematicBreak() {
        return new ThematicBreakImpl(this);
    }

    private static class BlockQuoteImpl extends ContainerNode implements BlockQuote {
        
        BlockQuoteImpl(NodeFactory factory) {
            super(factory);
        }
    }
    
    private static class CodeBlockImpl extends CodeNode implements CodeBlock {

        CodeBlockImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }
    
    private static class CodeSpanImpl extends CodeNode implements CodeSpan {

        CodeSpanImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }

    private static class DocumentImpl extends ContainerNode implements Document {
        
        DocumentImpl(NodeFactory factory) {
            super(factory);
        }
    }
  
    private static class EmphasisImpl extends ContainerNode implements Emphasis {
        
        private int strength;
        
        EmphasisImpl(NodeFactory factory, int strength) {
            super(factory);
            this.strength = strength;
        }
        
        @Override
        public int getStrength() {
            return strength;
        }
        
        @Override
        public void setStrength(int strength) {
            this.strength = strength;
        }
    }
    
    private static class HardLineBreakImpl extends BaseNode implements HardLineBreak {
        
        HardLineBreakImpl(NodeFactory factory) {
            super(factory);
        }
    }

    private static class HeadingImpl extends ContainerNode implements Heading {
        
        private int level;
        
        HeadingImpl(NodeFactory factory, int level) {
            super(factory);
            this.level = level;
        }

        @Override
        public int getLevel() {
            return level;
        }
        
        @Override
        public void setLevel(int level) {
            this.level = level;
        }
    }
    
    private static class HtmlBlockImpl extends HtmlNode implements HtmlBlock {
        
        HtmlBlockImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }

    private static class HtmlInlineImpl extends HtmlNode implements HtmlInline {
        
        HtmlInlineImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }

    private static class ImageImpl extends ContainerNode implements Image {
        
        private URI location;
        private String title;

        ImageImpl(NodeFactory factory) {
            super(factory);
            this.location = null;
            this.title = null;
        }

        @Override
        public URI getLocation() {
            return location;
        }

        @Override
        public void setLocation(URI location) {
            this.location = location;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public void setTitle(String title) {
            this.title = title;
        }
    }
    
    private static class LinkImpl extends ContainerNode implements Link {
        
        private String destination;
        private String title;
        
        LinkImpl(NodeFactory factory) {
            super(factory);
            this.destination = null;
            this.title = null;
        }

        @Override
        public String getDestination() {
            return destination;
        }

        @Override
        public void setDestination(String destination) {
            this.destination = destination;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public void setTitle(String title) {
            this.title = title;
        }
        
        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("(link ").append(super.toString()).append(")");
            return b.toString();
        }
    }

    private static class ListItemImpl extends ContainerNode implements ListItem {
        
        ListItemImpl(NodeFactory factory) {
            super(factory);
        }
    }
    
    private static class OrderedListImpl extends ContainerNode implements OrderedList {

        int startNumber;
        
        OrderedListImpl(NodeFactory factory) {
            super(factory);
            this.startNumber = 1;
        }

        @Override
        public int getStartNumber() {
            return startNumber;
        }
        
        @Override
        public void setStartNumber(int startNumber) {
            this.startNumber = startNumber;
        }
    }
    
    private static class ParagraphImpl extends ContainerNode implements Paragraph {
        
        ParagraphImpl(NodeFactory factory) {
            super(factory);
        }
    }
 
    private static class TextImpl extends CharDataNode implements Text {
        
        TextImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }
    
    private static class ThematicBreakImpl extends BaseNode implements ThematicBreak {
  
        ThematicBreakImpl(NodeFactory factory) {
            super(factory);
        }
    }

    private static class UnorderedListImpl extends ContainerNode implements UnorderedList {
        
        UnorderedListImpl(NodeFactory factory) {
            super(factory);
        }
    }
}
