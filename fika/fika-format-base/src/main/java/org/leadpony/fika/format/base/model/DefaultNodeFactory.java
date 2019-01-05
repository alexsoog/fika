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
package org.leadpony.fika.format.base.model;

import static java.util.Objects.requireNonNull;

import org.leadpony.fika.parser.model.Admonition;
import org.leadpony.fika.parser.model.BlockQuote;
import org.leadpony.fika.parser.model.CodeBlock;
import org.leadpony.fika.parser.model.CodeSpan;
import org.leadpony.fika.parser.model.Definition;
import org.leadpony.fika.parser.model.DefinitionList;
import org.leadpony.fika.parser.model.Document;
import org.leadpony.fika.parser.model.Emphasis;
import org.leadpony.fika.parser.model.HardLineBreak;
import org.leadpony.fika.parser.model.Heading;
import org.leadpony.fika.parser.model.HtmlBlock;
import org.leadpony.fika.parser.model.HtmlInline;
import org.leadpony.fika.parser.model.Image;
import org.leadpony.fika.parser.model.Link;
import org.leadpony.fika.parser.model.ListItem;
import org.leadpony.fika.parser.model.NodeFactory;
import org.leadpony.fika.parser.model.OrderedList;
import org.leadpony.fika.parser.model.Paragraph;
import org.leadpony.fika.parser.model.Term;
import org.leadpony.fika.parser.model.Text;
import org.leadpony.fika.parser.model.ThematicBreak;
import org.leadpony.fika.parser.model.UnorderedList;

/**
 * Default implementation of {@link NodeFactory}.
 *
 * @author leadpony
 */
public class DefaultNodeFactory implements NodeFactory {

    /**
     * Constructs this node factory.
     */
    public DefaultNodeFactory() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Admonition createAdmonition(String type, String title) {
        requireNonNull(type, "type must not be null");
        return new AdmonitionImpl(this, type, title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockQuote createBlockQuote() {
        return new BlockQuoteImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeBlock createCodeBlock(String content, String language) {
        requireNonNull(content, "content must not be null");
        return new CodeBlockImpl(this, content, language);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeSpan createCodeSpan(String content) {
        requireNonNull(content, "content must not be null");
        return new CodeSpanImpl(this, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Definition createDefinition() {
        return new DefinitionImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document createDocument() {
        return new DocumentImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Emphasis createEmphasis(int strength) {
        if (strength < Emphasis.MIN_STRENGTH || strength > Emphasis.MAX_STRENGTH) {
            throw new IllegalArgumentException("strength is out of range");
        }
        return new EmphasisImpl(this, strength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HardLineBreak createHardLineBreak() {
        return new HardLineBreakImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Heading createHeading(int level) {
        if (level < Heading.MIN_LEVEL || level > Heading.MAX_LEVEL) {
            throw new IllegalArgumentException("level is out of range");
        }
        return new HeadingImpl(this, level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlBlock createHtmlBlock(String content) {
        requireNonNull(content, "content must not be null");
        return new HtmlBlockImpl(this, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlInline createHtmlInline(String content) {
        requireNonNull(content, "content must not be null");
        return new HtmlInlineImpl(this, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image createImage(String location, String title) {
        requireNonNull(location, "location must not be null");
        return new ImageImpl(this, location, title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Link createLink(String destination, String title) {
        requireNonNull(destination, "destination must not be null");
        return new LinkImpl(this, destination, title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderedList createOrderedList(boolean tight, int startNumber) {
        return new OrderedListImpl(this, tight, startNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnorderedList createUnorderedList(boolean tight) {
        return new UnorderedListImpl(this, tight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefinitionList createDefinitionList(boolean tight) {
        return new DefinitionListImpl(this, tight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListItem createListItem() {
        return new ListItemImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Paragraph createParagraph() {
        return new ParagraphImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Term createTerm() {
        return new TermImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Text createText(String content) {
        requireNonNull(content, "content must not be null");
        return new TextImpl(this, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThematicBreak createThematicBreak() {
        return new ThematicBreakImpl(this);
    }

    /**
     * Implementation of {@link Admonition}.
     *
     * @author leadpony
     */
    private static class AdmonitionImpl extends AbstractContainerNode implements Admonition {

        private String type;
        private String title;

        AdmonitionImpl(NodeFactory factory, String type, String title) {
            super(factory);
            this.type = type;
            this.title = title;
        }

        @Override
        public String type() {
            return type;
        }

        @Override
        public String title() {
            return title;
        }
    }

    /**
     * Implementation of {@link BlockQuote}.
     *
     * @author leadpony
     */
    private static class BlockQuoteImpl extends AbstractContainerNode implements BlockQuote {

        BlockQuoteImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link CodeBlock}.
     *
     * @author leadpony
     */
    private static class CodeBlockImpl extends AbstractCodeNode implements CodeBlock {

        CodeBlockImpl(NodeFactory factory, String content, String language) {
            super(factory, content, language);
        }
    }

    /**
     * Implementation of {@link CodeSpan}.
     *
     * @author leadpony
     */
    private static class CodeSpanImpl extends AbstractCodeNode implements CodeSpan {

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

        DefinitionListImpl(NodeFactory factory, boolean tight) {
            super(factory, tight);
        }
    }

    /**
     * Implementation of {@link Definition}.
     *
     * @author leadpony
     */
    private static class DefinitionImpl extends AbstractContainerNode implements Definition {

        DefinitionImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link Document}.
     *
     * @author leadpony
     */
    private static class DocumentImpl extends AbstractContainerNode implements Document {

        DocumentImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link Emphasis}.
     *
     * @author leadpony
     */
    private static class EmphasisImpl extends AbstractContainerNode implements Emphasis {

        private final int strength;

        EmphasisImpl(NodeFactory factory, int strength) {
            super(factory);
            this.strength = strength;
        }

        @Override
        public int strength() {
            return strength;
        }
    }

    /**
     * Implementation of {@link HardLineBreak}.
     *
     * @author leadpony
     */
    private static class HardLineBreakImpl extends AbstractNode implements HardLineBreak {

        HardLineBreakImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link Heading}.
     *
     * @author leadpony
     */
    private static class HeadingImpl extends AbstractContainerNode implements Heading {

        private int level;

        HeadingImpl(NodeFactory factory, int level) {
            super(factory);
            this.level = level;
        }

        @Override
        public int level() {
            return level;
        }
    }

    /**
     * Implementation of {@link HtmlBlock}.
     *
     * @author leadpony
     */
    private static class HtmlBlockImpl extends AbstractHtmlNode implements HtmlBlock {

        HtmlBlockImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }

    /**
     * Implementation of {@link HtmlInline}.
     *
     * @author leadpony
     */
    private static class HtmlInlineImpl extends AbstractHtmlNode implements HtmlInline {

        HtmlInlineImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }

    /**
     * Implementation of {@link Image}.
     *
     * @author leadpony
     */
    private static class ImageImpl extends AbstractContainerNode implements Image {

        private final String location;
        private final String title;

        ImageImpl(NodeFactory factory, String location, String title) {
            super(factory);
            this.location = location;
            this.title = title;
        }

        @Override
        public String location() {
            return location;
        }

        @Override
        public String title() {
            return title;
        }
    }

    /**
     * Implementation of {@link Link}.
     *
     * @author leadpony
     */
    private static class LinkImpl extends AbstractContainerNode implements Link {

        private final String destination;
        private final String title;

        LinkImpl(NodeFactory factory, String destination, String title) {
            super(factory);
            this.destination = destination;
            this.title = title;
        }

        @Override
        public String destination() {
            return destination;
        }

        @Override
        public String title() {
            return title;
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
    private static class ListItemImpl extends AbstractContainerNode implements ListItem {

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

        private final int startNumber;

        OrderedListImpl(NodeFactory factory, boolean tight, int startNumber) {
            super(factory, tight);
            this.startNumber = startNumber;
        }

        @Override
        public int startNumber() {
            return startNumber;
        }
    }

    /**
     * Implementation of {@link Paragraph}.
     *
     * @author leadpony
     */
    private static class ParagraphImpl extends AbstractContainerNode implements Paragraph {

        ParagraphImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link Term}.
     *
     * @author leadpony
     */
    private static class TermImpl extends AbstractContainerNode implements Term {

        TermImpl(NodeFactory factory) {
            super(factory);
        }
    }

    /**
     * Implementation of {@link Text}.
     *
     * @author leadpony
     */
    private static class TextImpl extends AbstractCharSequenceNode implements Text {

        TextImpl(NodeFactory factory, String content) {
            super(factory, content);
        }
    }

    /**
     * Implementation of {@link ThematicBreak}.
     *
     * @author leadpony
     */
    private static class ThematicBreakImpl extends AbstractNode implements ThematicBreak {

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

        UnorderedListImpl(NodeFactory factory, boolean tight) {
            super(factory, tight);
        }
    }
}
