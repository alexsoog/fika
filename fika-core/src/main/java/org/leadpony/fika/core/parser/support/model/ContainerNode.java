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
package org.leadpony.fika.core.parser.support.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.NodeFactory;

/**
 * Skeletal implementation of the node containing child nodes.
 * 
 * @author leadpony
 */
public abstract class ContainerNode extends BaseNode {

    private BaseNode firstChildNode;
    private BaseNode lastChildNode;
    
    protected ContainerNode(NodeFactory factory) {
        super(factory);
    }
    
    @Override
    public Iterable<Node> childNodes() {
        return ()->new ChildNodeIterator(firstChildNode());
    }
    
    @Override
    public Node firstChildNode() {
        return firstChildNode;
    }
    
    @Override
    public Node lastChildNode() {
        return lastChildNode;
    }

    @Override
    public Node appendChild(Node child) {
        return appendChild(validateNode(child, "child"));
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
        for (Node child: childNodes()) {
            b.append(child.toString());
        }
        return b.toString();
    }

    @Override
    protected void buildTextContent(StringBuilder builder) {
        for (Node child: childNodes()) {
            ((BaseNode)child).buildTextContent(builder);
        }
    }
    
    private BaseNode appendChild(BaseNode child) {
        child.unlink();
        final BaseNode lastChild = lastChildNode;
        child.previousNode = lastChild;
        child.nextNode = null;
        child.parentNode = this;
        if (lastChild != null) {
            lastChild.nextNode = child;
        }
        lastChildNode = child;
        if (firstChildNode == null) {
            firstChildNode = child;
        }
        return child;
    }
    
    private BaseNode removeChild(BaseNode child) {
        if (child.parentNode() != this) {
            throw new IllegalArgumentException();
        }
        final BaseNode previous = child.previousNode;
        final BaseNode next = child.nextNode;
        if (firstChildNode == child) {
            firstChildNode = next;
        }
        if (lastChildNode == child) {
            lastChildNode = previous;
        }
        if (previous != null) {
            previous.nextNode = next;
        }
        if (next != null) {
            next.previousNode = previous;
        }
        return resetNode(child);
    }
    
    private BaseNode replaceChild(BaseNode newChild, BaseNode oldChild) {
        if (oldChild.parentNode() != this) {
            throw new IllegalArgumentException();
        }
        newChild.unlink();
        final BaseNode previous = oldChild.previousNode;
        final BaseNode next = oldChild.nextNode;
        if (previous != null) {
            previous.nextNode = newChild;
            newChild.previousNode = previous;
        }
        if (next != null) {
            next.previousNode = newChild;
            newChild.nextNode = next;
        }
        newChild.parentNode = this;
        if (firstChildNode == oldChild) {
            firstChildNode = newChild;
        }
        if (lastChildNode == oldChild) {
            lastChildNode = newChild;
        }
        return resetNode(oldChild);
    }
    
    private BaseNode insertChildBefore(BaseNode newChild, BaseNode nextChild) {
        if (!hasChild(nextChild)) {
            throw new IllegalArgumentException();
        }
        newChild.unlink();
        final BaseNode previousChild = nextChild.previousNode;
        if (previousChild != null) {
            previousNode.nextNode = newChild;
        } else {
            firstChildNode = newChild;
        }
        nextChild.previousNode = newChild;
        newChild.parentNode = this;
        newChild.previousNode = previousChild;
        newChild.nextNode = nextChild;
        return newChild;
    }
    
    private BaseNode insertChildAfter(BaseNode newChild, BaseNode previousChild) {
        if (!hasChild(previousChild)) {
            throw new IllegalArgumentException();
        }
        newChild.unlink();
        final BaseNode nextChild = previousChild.nextNode;
        if (nextChild != null) {
            nextChild.previousNode = newChild;
        } else {
            lastChildNode = newChild;
        }
        previousChild.nextNode = newChild;
        newChild.parentNode = this;
        newChild.previousNode = previousChild;
        newChild.nextNode = nextChild;
        return newChild;
    }
    
    private boolean hasChild(BaseNode child) {
        return child.parentNode() == this;
    }

    private BaseNode resetNode(BaseNode node) {
        node.previousNode = null;
        node.nextNode = null;
        node.parentNode = null;
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
            this.currentNode = next.nextNode();
            return next;
        }
    }
}
