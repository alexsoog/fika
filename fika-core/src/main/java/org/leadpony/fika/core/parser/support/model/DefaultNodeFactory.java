/*
 * Copyright 2017-2019 the Fika authors.
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
package org.leadpony.fika.core.parser.support.model;

import java.util.Objects;

import org.leadpony.fika.core.model.Admonition;
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
import org.leadpony.fika.core.model.ListType;
import org.leadpony.fika.core.model.NodeFactory;
import org.leadpony.fika.core.model.OrderedList;
import org.leadpony.fika.core.model.Paragraph;
import org.leadpony.fika.core.model.Term;
import org.leadpony.fika.core.model.Text;
import org.leadpony.fika.core.model.ThematicBreak;
import org.leadpony.fika.core.model.UnorderedList;

/**
 * Default implementation of {@link NodeFactory}.
 * 
 * @author leadpony
 */
public class DefaultNodeFactory implements NodeFactory {
    
    public DefaultNodeFactory() {
    }
    
    @Override
    public Admonition newAdmonition(String type) {
        Objects.requireNonNull(type, "type must not be null");
        return new AdmonitionImpl(this, type);
    }

    @Override
    public BlockQuote newBlockQuote() {
        return new BlockQuoteImpl(this);
    }

    @Override
    public CodeBlock newCodeBlock(String content) {
        Objects.requireNonNull(content, "content must not be null");
        return new CodeBlockImpl(this, content);
    }
    
    @Override
    public CodeSpan newCodeSpan(String content) {
        Objects.requireNonNull(content, "content must not be null");
        return new CodeSpanImpl(this, content);
    }

    @Override
    public Definition newDefinition() {
        return new DefinitionImpl(this);
    }

    @Override
    public Document newDocument() {
        return new DocumentImpl(this);
    }

    @Override
    public Emphasis newEmphasis(int strength) {
        if (strength < Emphasis.MIN_STRENGTH || strength > Emphasis.MAX_STRENGTH) {
            throw new IllegalArgumentException("strength is out of range");
        }
        return new EmphasisImpl(this, strength);
    }

    @Override
    public HardLineBreak newHardLineBreak() {
        return new HardLineBreakImpl(this);
    }

    @Override
    public Heading newHeading(int level) {
        if (level < Heading.MIN_LEVEL || level > Heading.MAX_LEVEL) {
            throw new IllegalArgumentException("level is out of range");
        }
        return new HeadingImpl(this, level);
    }

    @Override
    public HtmlBlock newHtmlBlock(String content) {
        Objects.requireNonNull(content, "content must not be null");
        return new HtmlBlockImpl(this, content);
    }
    
    @Override
    public HtmlInline newHtmlInline(String content) {
        Objects.requireNonNull(content, "content must not be null");
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
    public ListBlock newListBlock(ListType type) {
        Objects.requireNonNull(type, "type must not be null");
        switch (type) {
        case ORDERED:
            return new OrderedListImpl(this);
        case UNORDERED:
            return new UnorderedListImpl(this);
        case DEFINITION:
            return new DefinitionListImpl(this);
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
    public Term newTerm() {
        return new TermImpl(this);
    }

    @Override
    public Text newText(String content) {
        Objects.requireNonNull(content, "content must not be null");
        return new TextImpl(this, content);
    }

    @Override
    public ThematicBreak newThematicBreak() {
        return new ThematicBreakImpl(this);
    }
    
    /**
     * Implementation of {@link Admonition}.
     * 
     * @author leadpony
     */
    private static class AdmonitionImpl extends ContainerNode implements Admonition {
        
        private String type;
        private String title;
        
        AdmonitionImpl(NodeFactory factory, String type) {
            super(factory);
            this.type = type;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public void setType(String type) {
            Objects.requireNonNull(type, "type must not be null");
            this.type = type;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public void setTitle(String title) {
            Objects.requireNonNull(title, "title must not be null");
            this.title = title;
        }
    }
    
    /**
     * Implementation of {@link BlockQuote}.
     * 
     * @author leadpony
     */
    private static class BlockQuoteImpl extends ContainerNode implements BlockQuote {
        
        BlockQuoteImpl(NodeFactory factory) {
            super(factory);
        }
    }
    
    /**
     * Implementation of {@link CodeBlock}.
     * 
     * @author leadpony
     */
    private static class CodeBlockImpl extends CodeNode implements CodeBlock {

        CodeBlockImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }
    
    /**
     * Implementation of {@link CodeSpan}.
     * 
     * @author leadpony
     */
    private static class CodeSpanImpl extends CodeNode implements CodeSpan {

        CodeSpanImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }

    /**
     * Implementation of {@link DefinitionList}.
     * 
     * @author leadpony
     */
    private static class DefinitionListImpl extends AbstractListBlock implements DefinitionList {
        
        DefinitionListImpl(NodeFactory factory) {
            super(factory);
        }
    }
    
    /**
     * Implementation of {@link Definition}.
     * 
     * @author leadpony
     */
    private static class DefinitionImpl extends ContainerNode implements Definition {
        
        DefinitionImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link Document}.
     * 
     * @author leadpony
     */
    private static class DocumentImpl extends ContainerNode implements Document {
        
        DocumentImpl(NodeFactory factory) {
            super(factory);
        }
    }
  
    /**
     * Implementation of {@link Emphasis}.
     * 
     * @author leadpony
     */
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
            if (strength < Emphasis.MIN_STRENGTH || strength > Emphasis.MAX_STRENGTH) {
                throw new IllegalArgumentException("strength is out of range");
            }
            this.strength = strength;
        }
    }
    
    /**
     * Implementation of {@link HardLineBreak}.
     * 
     * @author leadpony
     */
    private static class HardLineBreakImpl extends BaseNode implements HardLineBreak {
        
        HardLineBreakImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link Heading}.
     * 
     * @author leadpony
     */
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
            if (level < Heading.MIN_LEVEL || level > Heading.MAX_LEVEL) {
                throw new IllegalArgumentException("level is out of range");
            }
            this.level = level;
        }
    }
    
    /**
     * Implementation of {@link HtmlBlock}.
     * 
     * @author leadpony
     */
    private static class HtmlBlockImpl extends HtmlNode implements HtmlBlock {
        
        HtmlBlockImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }

    /**
     * Implementation of {@link HtmlInline}.
     * 
     * @author leadpony
     */
    private static class HtmlInlineImpl extends HtmlNode implements HtmlInline {
        
        HtmlInlineImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }

    /**
     * Implementation of {@link Image}.
     * 
     * @author leadpony
     */
    private static class ImageImpl extends ContainerNode implements Image {
        
        private String location;
        private String title;

        ImageImpl(NodeFactory factory) {
            super(factory);
            this.location = null;
            this.title = null;
        }

        @Override
        public String getLocation() {
            return location;
        }

        @Override
        public void setLocation(String location) {
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
    
    /**
     * Implementation of {@link Link}.
     * 
     * @author leadpony
     */
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

    /**
     * Implementation of {@link ListItem}.
     * 
     * @author leadpony
     */
    private static class ListItemImpl extends ContainerNode implements ListItem {
        
        ListItemImpl(NodeFactory factory) {
            super(factory);
        }
    }
    
    /**
     * Implementation of {@link OrderedList}.
     * 
     * @author leadpony
     */
    private static class OrderedListImpl extends AbstractListBlock implements OrderedList {

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
    
    /**
     * Implementation of {@link Paragraph}.
     * 
     * @author leadpony
     */
    private static class ParagraphImpl extends ContainerNode implements Paragraph {
        
        ParagraphImpl(NodeFactory factory) {
            super(factory);
        }
    }
 
    /**
     * Implementation of {@link Term}.
     * 
     * @author leadpony
     */
    private static class TermImpl extends ContainerNode implements Term {
        
        TermImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link Text}.
     * 
     * @author leadpony
     */
    private static class TextImpl extends CharDataNode implements Text {
        
        TextImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }
    
    /**
     * Implementation of {@link ThematicBreak}.
     * 
     * @author leadpony
     */
    private static class ThematicBreakImpl extends BaseNode implements ThematicBreak {
  
        ThematicBreakImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link UnorderedList}.
     * 
     * @author leadpony
     */
    private static class UnorderedListImpl extends AbstractListBlock implements UnorderedList {
        
        UnorderedListImpl(NodeFactory factory) {
            super(factory);
        }
    }
}
