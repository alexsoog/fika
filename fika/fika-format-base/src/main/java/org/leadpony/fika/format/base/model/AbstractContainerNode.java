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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.NodeFactory;

/**
 * Skeletal implementation of the node containing child nodes.
 *
 * @author leadpony
 */
abstract class AbstractContainerNode extends AbstractNode {

    private AbstractNode firstChildNode;
    private AbstractNode lastChildNode;

    protected AbstractContainerNode(NodeFactory factory) {
        super(factory);
    }

    @Override
    public Iterable<Node> getChildNodes() {
        return ()->new ChildNodeIterator(getFirstChild());
    }

    @Override
    public Node getFirstChild() {
        return firstChildNode;
    }

    @Override
    public Node getLastChild() {
        return lastChildNode;
    }

    @Override
    public Node appendChild(Node child) {
        return appendChild(validateNode(child, "child"));
    }

    @Override
    public void appendChildren(Collection<Node> children) {
        requireNonNull(children, "children");
        for (Node child : children) {
            appendChild(child);
        }
    }

    @Override
    public Node removeChild(Node child) {
        return removeChild(validateNode(child, "child"));
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) {
        return replaceChild(
                validateNode(newChild, "newChild"),
                validateNode(oldChild, "oldChild")
        );
    }

    @Override
    public Node insertChildBefore(Node newChild, Node nextChild) {
        return insertChildBefore(
                validateNode(newChild, "newChild"),
                validateNode(nextChild, "nextChild")
        );
    }

    @Override
    public Node insertChildAfter(Node newChild, Node previousChild) {
        return insertChildAfter(
                validateNode(newChild, "newChild"),
                validateNode(previousChild, "previousChild")
        );
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Node child: getChildNodes()) {
            b.append(child.toString());
        }
        return b.toString();
    }

    @Override
    protected void buildTextContent(StringBuilder builder) {
        for (Node child: getChildNodes()) {
            ((AbstractNode)child).buildTextContent(builder);
        }
    }

    private AbstractNode appendChild(AbstractNode child) {
        child.unlink();
        final AbstractNode lastChild = lastChildNode;
        child.previousSibling = lastChild;
        child.nextSibling = null;
        child.parent = this;
        if (lastChild != null) {
            lastChild.nextSibling = child;
        }
        lastChildNode = child;
        if (firstChildNode == null) {
            firstChildNode = child;
        }
        return child;
    }

    private AbstractNode removeChild(AbstractNode child) {
        if (child.getParentNode() != this) {
            throw new IllegalArgumentException();
        }
        final AbstractNode previous = child.previousSibling;
        final AbstractNode next = child.nextSibling;
        if (firstChildNode == child) {
            firstChildNode = next;
        }
        if (lastChildNode == child) {
            lastChildNode = previous;
        }
        if (previous != null) {
            previous.nextSibling = next;
        }
        if (next != null) {
            next.previousSibling = previous;
        }
        return resetNode(child);
    }

    private AbstractNode replaceChild(AbstractNode newChild, AbstractNode oldChild) {
        if (oldChild.getParentNode() != this) {
            throw new IllegalArgumentException();
        }
        newChild.unlink();
        final AbstractNode previous = oldChild.previousSibling;
        final AbstractNode next = oldChild.nextSibling;
        if (previous != null) {
            previous.nextSibling = newChild;
            newChild.previousSibling = previous;
        }
        if (next != null) {
            next.previousSibling = newChild;
            newChild.nextSibling = next;
        }
        newChild.parent = this;
        if (firstChildNode == oldChild) {
            firstChildNode = newChild;
        }
        if (lastChildNode == oldChild) {
            lastChildNode = newChild;
        }
        return resetNode(oldChild);
    }

    private AbstractNode insertChildBefore(AbstractNode newChild, AbstractNode nextChild) {
        if (!hasChild(nextChild)) {
            throw new IllegalArgumentException();
        }
        newChild.unlink();
        final AbstractNode previousChild = nextChild.previousSibling;
        if (previousChild != null) {
            previousSibling.nextSibling = newChild;
        } else {
            firstChildNode = newChild;
        }
        nextChild.previousSibling = newChild;
        newChild.parent = this;
        newChild.previousSibling = previousChild;
        newChild.nextSibling = nextChild;
        return newChild;
    }

    private AbstractNode insertChildAfter(AbstractNode newChild, AbstractNode previousChild) {
        if (!hasChild(previousChild)) {
            throw new IllegalArgumentException();
        }
        newChild.unlink();
        final AbstractNode nextChild = previousChild.nextSibling;
        if (nextChild != null) {
            nextChild.previousSibling = newChild;
        } else {
            lastChildNode = newChild;
        }
        previousChild.nextSibling = newChild;
        newChild.parent = this;
        newChild.previousSibling = previousChild;
        newChild.nextSibling = nextChild;
        return newChild;
    }

    private boolean hasChild(AbstractNode child) {
        return child.getParentNode() == this;
    }

    private AbstractNode resetNode(AbstractNode node) {
        node.previousSibling = null;
        node.nextSibling = null;
        node.parent = null;
        return node;
    }

    private static class ChildNodeIterator implements Iterator<Node> {

        Node currentNode;

        ChildNodeIterator(Node firstChildNode) {
            this.currentNode = firstChildNode;
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public Node next() {
            if (currentNode == null) {
                throw new NoSuchElementException();
            }
            Node next = currentNode;
            this.currentNode = next.getNextSibling();
            return next;
        }
    }
}
