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

import java.util.Collections;

import org.leadpony.fika.parser.model.Node;
import org.leadpony.fika.parser.model.NodeFactory;

/**
 * Skeletal implementation of {@link Node}.
 *
 * @author leadpony
 */
abstract class AbstractNode implements Node {

    private final NodeFactory factory;
    /**
     * The parent of this node.
     */
    protected AbstractNode parent;
    /**
     * The node immediately preceding this node.
     */
    protected AbstractNode previousSibling;
    /**
     * The node immediately following this node.
     */
    protected AbstractNode nextSibling;

    /**
     * Constructs this node.
     *
     * @param factory the factory which created this node.
     */
    protected AbstractNode(NodeFactory factory) {
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String textContent() {
        StringBuilder builder = new StringBuilder();
        buildTextContent(builder);
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeFactory factory() {
        return factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildNodes() {
        return getFirstChild() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Node> getChildNodes() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getParentNode() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getFirstChild() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getLastChild() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getPreviousSibling() {
        return previousSibling;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getNextSibling() {
        return nextSibling;
    }

    @Override
    public void unlink() {
        Node parent = getParentNode();
        if (parent != null) {
            parent.removeChild(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "";
    }

    /**
     * Validates the specified node and returns the node as a {@code AbstractNode}.
     *
     * @param node the node to validate, cannot be {@code null}.
     * @param name the name of the node, cannot be {@code null}.
     * @return the validated node.
     * @throws NullPointerException     if the specified {@code node} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if the specified {@code node} is not
     *                                  instantiated by the factory of this node.
     */
    protected AbstractNode validateNode(Node node, String name) {
        if (node == null) {
            throw new NullPointerException(name + " must not be null.");
        } else if (node.factory() != factory()) {
            throw new IllegalArgumentException(name + " was instantiated by other factory.");
        }
        return (AbstractNode) node;
    }

    /**
     * Builds text content of this node.
     *
     * @param builder the string builder.
     */
    protected void buildTextContent(StringBuilder builder) {
    }
}
