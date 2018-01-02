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
package io.github.leadpony.fika.core.model;

import java.util.Collection;

/**
 * @author leadpony
 */
public interface Node {
    
    default boolean hasChildNodes() {
        return firstChildNode() != null;
    }
    
    /**
     * Returns child nodes of this node.
     * 
     * @return child nodes of this node.
     */
    Iterable<Node> childNodes();
    
    Node parentNode();
    
    Node firstChildNode();
    
    Node lastChildNode();

    Node previousNode();
    
    Node nextNode();

    default Node appendChild(Node child) {
        throw new UnsupportedOperationException();
    }
    
    default void appendChildren(Collection<Node> children) {
        for (Node child: children) {
            appendChild(child);
        }
    }
    
    default Node removeChild(Node child) {
        throw new UnsupportedOperationException();
    }
    
    default Node replaceChild(Node newChild, Node oldChild) {
        throw new UnsupportedOperationException();
    }
    
    default Node insertChildBefore(Node newChild, Node nextChild) {
        throw new UnsupportedOperationException();
    }
    
    default Node insertChildAfter(Node newChild, Node previousChild) {
        throw new UnsupportedOperationException();
    }

    default void unlink() {
        Node parent = parentNode();
        if (parent != null) {
            parent.removeChild(this);
        }
    }
    
    void accept(Visitor visitor);

    NodeFactory factory();
}