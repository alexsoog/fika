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
package org.leadpony.fika.core.model;

import java.util.Collection;

/**
 * The base type of all kind of nodes in the abstract syntax tree.
 *
 * @author leadpony
 */
public interface Node {

    /**
     * Returns the text content of this node and its descendants.
     *
     * @return the text content of this node and its descendants.
     */
    String textContent();

    /**
     * Returns the instance of the factory which created this node.
     *
     * @return the instance of the factory, never be {@code null}.
     */
    NodeFactory factory();

    /**
     * Accepts the visitor to this node.
     *
     * @param visitor the visitor to accept, cannot be {@code null}.
     */
    void accept(Visitor visitor);

    /**
     * Checks whether this node has any children or not.
     *
     * @return {@code true} if this node has any children, {@code false} otherwise.
     */
    boolean hasChildNodes();

    /**
     * Returns all children of this node.
     *
     * @return all children of this node.
     */
    Iterable<Node> getChildNodes();

    /**
     * Returns the parent of this node.
     *
     * @return the parent of this node. if a node has just been created and not yet
     *         added to the tree, or if it has been removed from the tree, this is
     *         {@code null}.
     */
    Node getParentNode();

    /**
     * Returns the first child of this node.
     *
     * @return the first child of this node. If there is no such node, this returns
     *         {@code null}.
     */
    Node getFirstChild();

    /**
     * Returns the last child of this node.
     *
     * @return the last child of this node. If there is no such node, this returns
     *         {@code null}.
     */
    Node getLastChild();

    /**
     * Returns the node immediately preceding this node.
     *
     * @return the node immediately preceding this node.If there is no such node,
     *         this returns {@code null}.
     */
    Node getPreviousSibling();

    /**
     * Returns the node immediately following this node.
     *
     * @return the node immediately following this node. If there is no such node,
     *         this returns {@code null}.
     */
    Node getNextSibling();

    /**
     * Appends a child node to the last.
     *
     * @param child the child node to append.
     * @return the added node.
     * @throws UnsupportedOperationException if this node cannot have any children.
     * @throws NullPointerException          if the specified {@code child} is
     *                                       {@code null}.
     */
    default Node appendChild(Node child) {
        throw new UnsupportedOperationException();
    }

    /**
     * Appends child nodes to the last.
     *
     * @param children the child nodes to append.
     * @throws UnsupportedOperationException if this node cannot have any children.
     * @throws NullPointerException          if the specified {@code children} is
     *                                       {@code null}.
     */
    default void appendChildren(Collection<Node> children) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the specified child node from the list of children, and returns it.
     *
     * @param child the child node to remove.
     * @return the removed node.
     * @throws UnsupportedOperationException if this node cannot have any children.
     * @throws NullPointerException          if the specified {@code child} is
     *                                       {@code null}.
     * @throws IllegalArgumentException      if the specified {@code child} is not
     *                                       the child of this node.
     */
    default Node removeChild(Node child) {
        throw new UnsupportedOperationException();
    }

    /**
     * Replaces the old child node with the new node.
     *
     * @param newChild the node to add.
     * @param oldChild the node to remove.
     * @return the replaced old node.
     * @throws UnsupportedOperationException if this node cannot have any children.
     * @throws NullPointerException          if the specified {@code newChild} or
     *                                       {@code oldNode} is {@code null}.
     * @throws IllegalArgumentException      if the specified {@code oldChild} is
     *                                       not the child of this node.
     */
    default Node replaceChild(Node newChild, Node oldChild) {
        throw new UnsupportedOperationException();
    }

    /**
     * Inserts the new child node immediately before the existing child node.
     *
     * @param newChild  the child node to insert.
     * @param nextChild the node before which the new node will be inserted.
     * @return the inserted node.
     * @throws UnsupportedOperationException if this node cannot have any children.
     * @throws NullPointerException          if the specified {@code newChild} or
     *                                       {@code nextChild} is {@code null}.
     * @throws IllegalArgumentException      if the specified {@code nextChild} is
     *                                       not the child of this node.
     */
    default Node insertChildBefore(Node newChild, Node nextChild) {
        throw new UnsupportedOperationException();
    }

    /**
     * Inserts the new child node immediately after the existing child node.
     *
     * @param newChild      the child node to insert.
     * @param previousChild the node after which the new node will be inserted.
     * @return the inserted node.
     * @throws UnsupportedOperationException if this node cannot have any children.
     * @throws NullPointerException          if the specified {@code newChild} or
     *                                       {@code previousChild} is {@code null}.
     * @throws IllegalArgumentException      if the specified {@code previousChild} is
     *                                       not the child of this node.
     */
    default Node insertChildAfter(Node newChild, Node previousChild) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes this node from its parent node.
     * <p>
     * If this node is already removed from the tree, this method does nothing.
     * </p>
     */
    void unlink();
}
