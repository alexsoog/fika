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
package io.github.leadpony.fika.core.parser.support.nodes;

import java.util.Collections;
import java.util.List;

import io.github.leadpony.fika.core.nodes.Node;

/**
 * @author leadpony
 */
abstract class AbstractNode implements Node {
    
    private Node parentNode;
    
    @Override
    public boolean hasChildNodes() {
        return false;
    }
    
    @Override
    public List<Node> childNodes() {
        return Collections.emptyList();
    }
    
    @Override
    public Node getParentNode() {
        return parentNode;
    }
    
    @Override
    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }
}
