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
package org.leadpony.fika.core.model;

/**
 * Factory for creating various kinds of nodes.
 * 
 * @author leadpony
 */
public interface NodeFactory {
    
    /**
     * Creates a new node of {@link Admonition} type.
     * 
     * @param type the type qualifier of the admonition, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if given {@code type} is null.
     */
    Admonition newAdmonition(String type);

    /**
     * Creates a new node of {@link BlockQuote} type.
     * 
     * @return newly created node.
     */
    BlockQuote newBlockQuote();
    
    /**
     * Creates a new node of {@link CodeBlock} type.
     * 
     * @param content the text content of the node, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if given {@code content} is null.
     */
    CodeBlock newCodeBlock(String content);

    /**
     * Creates a new node of {@link CodeSpan} type.
     * 
     * @param content the text content of the node, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if given {@code content} is null.
     */
    CodeSpan newCodeSpan(String content);

    /**
     * Creates a new node of {@link Definition} type.
     * 
     * @return newly created node.
     */
    Definition newDefinition();
    
    /**
     * Creates a new node of {@link Document} type.
     * 
     * @return newly created node.
     */
    Document newDocument();

    /**
     * Creates a new node of {@link Emphasis} type.
     * 
     * @param strength the strength level of the emphasis, from 1 to 2.
     * @return newly created node.
     * @throws IllegalArgumentException if given {@code strength} if out of range.
     */
    Emphasis newEmphasis(int strength);

    /**
     * Creates a new node of {@link HardLineBreak} type.
     * 
     * @return newly created node.
     */
    HardLineBreak newHardLineBreak();

    /**
     * Creates a new node of {@link Heading} type.
     * 
     * @param level the level of the heading, from 1 to 6.
     * @return newly created node.
     * @throws IllegalArgumentException if given {@code level} if out of range.
     */
    Heading newHeading(int level);

    /**
     * Creates a new node of {@link HtmlBlock} type.
     * 
     * @param content the text content of the node, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if given {@code content} is null.
     */
    HtmlBlock newHtmlBlock(String content);
    
    /**
     * Creates a new node of {@link HtmlInline} type.
     * 
     * @param content the text content of the node, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if given {@code content} is null.
     */
    HtmlInline newHtmlInline(String content);
    
    /**
     * Creates a new node of {@link Image} type.
     * 
     * @return newly created node.
     */
    Image newImage();

    /**
     * Creates a new node of {@link Link} type.
     * 
     * @return newly created node.
     */
    Link newLink();

    /**
     * Creates a new node of {@link ListBlock} type.
     * 
     * @param type the type of the list, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if given {@code type} is null.
     */
    ListBlock newLiskBlock(ListType type);

    /**
     * Creates a new node of {@link ListItem} type.
     * 
     * @return newly created node.
     */
    ListItem newListItem();

    /**
     * Creates a new node of {@link Paragraph} type.
     * 
     * @return newly created node.
     */
    Paragraph newParagraph();

    /**
     * Creates a new node of {@link Term} type.
     * 
     * @return newly created node.
     */
    Term newTerm();

    /**
     * Creates a new node of {@link Text} type.
     * 
     * @param content the text content of the node, cannot be {@code null}.
     * @return newly created node.
     * @throws NullPointerException if given {@code content} is null.
     */
    Text newText(String content);
    
    /**
     * Creates a new node of {@link ThematicBreak} type.
     * 
     * @return newly created node.
     */
    ThematicBreak newThematicBreak();
}
