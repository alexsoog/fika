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
package org.leadpony.fika.parser.model;

/**
 * A factory for creating various kinds of nodes in the abstract syntax tree.
 *
 * @author leadpony
 */
public interface NodeFactory {

    /**
     * Creates a new node of {@link Admonition} type.
     *
     * @param type  the type qualifier of the admonition, cannot be {@code null}.
     * @param title the title of the admonition, can be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if the specified {@code type} is {@code null}.
     */
    Admonition createAdmonition(String type, String title);

    /**
     * Creates a new node of {@link BlockQuote} type.
     *
     * @return newly created node.
     */
    BlockQuote createBlockQuote();

    /**
     * Creates a new node of {@link CodeBlock} type.
     *
     * @param content  the text content of the node, cannot be {@code null}.
     * @param language the programming language of the code, can be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if the specified {@code content} is
     *                              {@code null}.
     */
    CodeBlock createCodeBlock(String content, String language);

    /**
     * Creates a new node of {@link CodeSpan} type.
     *
     * @param content the text content of the node, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if the specified {@code content} is
     *                              {@code null}.
     */
    CodeSpan createCodeSpan(String content);

    /**
     * Creates a new node of {@link Definition} type.
     *
     * @return newly created node.
     */
    Definition createDefinition();

    /**
     * Creates a new node of {@link Document} type.
     *
     * @return newly created node.
     */
    Document createDocument();

    /**
     * Creates a new node of {@link Emphasis} type.
     *
     * @param strength the strength level of the emphasis, from 1 to 2.
     * @return newly created node.
     * @throws IllegalArgumentException if the specified {@code strength} is out of
     *                                  range.
     */
    Emphasis createEmphasis(int strength);

    /**
     * Creates a new node of {@link HardLineBreak} type.
     *
     * @return newly created node.
     */
    HardLineBreak createHardLineBreak();

    /**
     * Creates a new node of {@link Heading} type.
     *
     * @param level the level of the heading, from 1 to 6.
     * @return newly created node.
     * @throws IllegalArgumentException if the specified {@code level} is out of
     *                                  range.
     */
    Heading createHeading(int level);

    /**
     * Creates a new node of {@link HtmlBlock} type.
     *
     * @param content the text content of the node, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if the specified {@code content} is
     *                              {@code null}.
     */
    HtmlBlock createHtmlBlock(String content);

    /**
     * Creates a new node of {@link HtmlInline} type.
     *
     * @param content the text content of the node, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if the specified {@code content} is
     *                              {@code null}.
     */
    HtmlInline createHtmlInline(String content);

    /**
     * Creates a new node of {@link Image} type.
     *
     * @param location the location of the image, cannot be {@code null}.
     * @param title    the title of the image, can be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if the specified {@code location} is
     *                              {@code null}.
     */
    Image createImage(String location, String title);

    /**
     * Creates a new node of {@link Link} type.
     *
     * @param destination the destination of the link, cannot be {@code null}.
     * @param title       the title of the link, can be {@code null}..
     * @return newly created node.
     * @throws NullPointerException if the specified {@code destination} is
     *                              {@code null}.
     */
    Link createLink(String destination, String title);

    /**
     * Creates a new node of {@link OrderedList} type.
     *
     * @param tight       {@code true} if the list is tight, {@code false}
     *                    otherwise.
     * @param startNumber the starting number of the list.
     * @return newly created node.
     */
    OrderedList createOrderedList(boolean tight, int startNumber);

    /**
     * Creates a new node of {@link UnorderedList} type.
     *
     * @param tight {@code true} if the list is tight, {@code false} otherwise.
     * @return newly created node.
     */
    UnorderedList createUnorderedList(boolean tight);

    /**
     * Creates a new node of {@link DefinitionList} type.
     *
     * @param tight {@code true} if the list is tight, {@code false} otherwise.
     * @return newly created node.
     */
    DefinitionList createDefinitionList(boolean tight);

    /**
     * Creates a new node of {@link ListItem} type.
     *
     * @return newly created node.
     */
    ListItem createListItem();

    /**
     * Creates a new node of {@link Paragraph} type.
     *
     * @return newly created node.
     */
    Paragraph createParagraph();

    /**
     * Creates a new node of {@link Term} type.
     *
     * @return newly created node.
     */
    Term createTerm();

    /**
     * Creates a new node of {@link Text} type.
     *
     * @param content the text content of the node, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if the specified {@code content} is
     *                              {@code null}.
     */
    Text createText(String content);

    /**
     * Creates a new node of {@link ThematicBreak} type.
     *
     * @return newly created node.
     */
    ThematicBreak createThematicBreak();
}
