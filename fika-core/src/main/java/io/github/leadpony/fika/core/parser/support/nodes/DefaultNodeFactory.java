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
package io.github.leadpony.fika.core.parser.support.nodes;

import io.github.leadpony.fika.core.nodes.BlockQuote;
import io.github.leadpony.fika.core.nodes.CodeBlock;
import io.github.leadpony.fika.core.nodes.Document;
import io.github.leadpony.fika.core.nodes.Heading;
import io.github.leadpony.fika.core.nodes.HtmlBlock;
import io.github.leadpony.fika.core.nodes.ListBlock;
import io.github.leadpony.fika.core.nodes.ListItem;
import io.github.leadpony.fika.core.nodes.ListType;
import io.github.leadpony.fika.core.nodes.NodeFactory;
import io.github.leadpony.fika.core.nodes.OrderedList;
import io.github.leadpony.fika.core.nodes.Paragraph;
import io.github.leadpony.fika.core.nodes.Text;
import io.github.leadpony.fika.core.nodes.ThematicBreak;
import io.github.leadpony.fika.core.nodes.UnorderedList;

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
    public CodeBlock newCodeBlock() {
        return new CodeBlockImpl(this);
    }

    @Override
    public Document newDocument() {
        return new DocumentImpl(this);
    }

    @Override
    public Heading newHeading(int level) {
        return new HeadingImpl(this, level);
    }

    @Override
    public HtmlBlock newHtmlBlock() {
        return new HtmlBlockImpl(this);
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
    public Text newText() {
        return new TextImpl(this);
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
    
    private static class CodeBlockImpl extends BaseNode implements CodeBlock {

        String content;
        String language;
        
        CodeBlockImpl(NodeFactory factory) {
            super(factory);
        }

        @Override
        public String getContent() {
            return content;
        }
        
        @Override
        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String getLanguage() {
            return language;
        }
        
        @Override
        public void setLanguage(String language) {
            this.language = language;
        }
        
        @Override
        public String toString() {
            return content;
        }
    }
    
    private static class DocumentImpl extends ContainerNode implements Document {
        
        DocumentImpl(NodeFactory factory) {
            super(factory);
        }
    }
  
    private static class HeadingImpl extends ContainerNode implements Heading {
        
        final int level;
        
        HeadingImpl(NodeFactory factory, int level) {
            super(factory);
            this.level = level;
        }

        @Override
        public int level() {
            return level;
        }
    }

    private static class HtmlBlockImpl extends BaseNode implements HtmlBlock {

        String html;
        
        HtmlBlockImpl(NodeFactory factory) {
            super(factory);
        }

        @Override
        public String getHtml() {
            return html;
        }

        @Override
        public void setHtml(String html) {
            this.html = html;
        }
        
        @Override
        public String toString() {
            return getHtml();
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
 
    private static class TextImpl extends BaseNode implements Text {
        
        String content;
        
        TextImpl(NodeFactory factory) {
            super(factory);
        }
        
        @Override
        public String getContent() {
            return content;
        }
        
        @Override
        public void setContent(String content) {
            this.content = content;
        }
        
        @Override
        public String toString() {
            return content;
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
