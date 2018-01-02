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
package io.github.leadpony.fika.core.parser.support.model;

import java.util.Collections;

import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.NodeFactory;

/**
 * @author leadpony
 */
public abstract class BaseNode implements Node {
    
    private final NodeFactory factory;
    protected BaseNode parentNode;
    protected BaseNode previousNode;
    protected BaseNode nextNode;
    
    protected BaseNode(NodeFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public Iterable<Node> childNodes() {
        return Collections.emptyList();
    }
    
    @Override
    public Node parentNode() {
        return parentNode;
    }
    
    @Override
    public Node firstChildNode() {
        return null;
    }
    
    @Override
    public Node lastChildNode() {
        return null;
    }
    
    @Override
    public Node previousNode() {
        return previousNode;
    }
    
    @Override
    public Node nextNode() {
        return nextNode;
    }

    @Override
    public NodeFactory factory() {
        return factory;
    }
    
    protected BaseNode validateNode(Node node, String name) {
        if (node == null) {
            throw new NullPointerException(name + " must not be null.");
        } else if (node.factory() != factory()) {
            throw new IllegalArgumentException(name + " was instantiated by other factory.");
        }
        return (BaseNode)node;
    }
}
