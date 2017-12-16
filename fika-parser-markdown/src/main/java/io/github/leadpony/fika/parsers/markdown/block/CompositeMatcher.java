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
package io.github.leadpony.fika.parsers.markdown.block;

import java.util.ArrayList;
import java.util.List;

import io.github.leadpony.fika.core.nodes.Node;

/**
 * @author leadpony
 */
abstract class CompositeMatcher extends AbstractBlockMatcher {
    
    protected BlockMatcher next;
    protected final List<Node> children = new ArrayList<>();
    
    protected CompositeMatcher() {
    }

    boolean hasNext() {
        return this.next != null;
    }
    
    BlockMatcher next() {
        return next;
    }
    
    @Override
    public boolean match(Content content) {
        if (hasNext()) {
            return matchAndForward(content);
        } else {
            return matchLast(content);
        }
    }
    
    @Override
    public Node close() {
        if (hasNext()) {
            finishNext();
        }
        return null;
    }
    
    protected boolean matchAndForward(Content content) {
        return forward(content);
    }
    
    protected boolean forward(Content content) {
        BlockMatcher interrupter = next().interrupt(content);
        if (interrupter != null) {
            finishNext();
            startNext(interrupter);
        }
        if (next().match(content)) {
            return true;
        } else {
            finishNext();
            return false;
        }
    }
    
    protected boolean matchLast(Content content) {
        return false;
    }

    protected void startNext(BlockMatcher next) {
        this.next = next;
        if (this.next != null) {
            this.next.bind(context());
        }
    }
    
    protected void finishNext() {
        Node node = this.next.close();
        if (node != null) {
            this.children.add(node);
        }
        this.next = null;
    }
}
