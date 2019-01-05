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
 * A visitor of the nodes in the abstract syntax tree.
 *
 * @author leadpony
 */
public interface Visitor {

    /**
     * Visits a node of {@link Admonition} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Admonition node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link BlockQuote} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(BlockQuote node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link CodeBlock} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(CodeBlock node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link CodeSpan} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(CodeSpan node) {
    }

    /**
     * Visits a node of {@link Definition} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Definition node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link DefinitionList} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(DefinitionList node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link Document} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Document node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link Emphasis} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Emphasis node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link HardLineBreak} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(HardLineBreak node) {
    }

    /**
     * Visits a node of {@link Heading} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Heading node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link HtmlBlock} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(HtmlBlock node) {
    }

    /**
     * Visits a node of {@link HtmlInline} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(HtmlInline node) {
    }

    /**
     * Visits a node of {@link Image} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Image node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link Link} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Link node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link ListItem} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(ListItem node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link OrderedList} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(OrderedList node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link Paragraph} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Paragraph node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link Term} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Term node) {
        visitChildren(node);
    }

    /**
     * Visits a node of {@link Text} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(Text node) {
    }

    /**
     * Visits a node of {@link ThematicBreak} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(ThematicBreak node) {
    }

    /**
     * Visits a node of {@link UnorderedList} type.
     *
     * @param node the node to visit, cannot be {@code null}.
     */
    default void visit(UnorderedList node) {
        visitChildren(node);
    }

    /**
     * Visits children of the specified node.
     *
     * @param node the node who owns children to visit, cannot be {@code null}.
     */
    default void visitChildren(Node node) {
        for (Node child : node.getChildNodes()) {
            child.accept(this);
        }
    }
}
