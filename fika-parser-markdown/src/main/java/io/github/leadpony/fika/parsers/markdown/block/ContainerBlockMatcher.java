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
 * Matcher for container blocks such as documents, block quotes, and lists.
 * 
 * @author leadpony
 */
abstract class ContainerBlockMatcher extends AbstractBlockMatcher {
    
    protected BlockMatcher next;
    protected final List<Node> children = new ArrayList<>();
    
    protected ContainerBlockMatcher() {
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }
    
    @Override
    public BlockMatcher next() {
        return next;
    }
    
    @Override
    public Status match(Content content) {
        matchNext(content);
        return Status.CONTINUED;
    }

    @Override
    public Node close() {
        if (hasNext()) {
            closeNext();
        }
        return null;
    }
    
    protected Status matchNext(Content content) {
        if (hasNext()) {
            return forwardTo(next(), content);
        } else {
            BlockMatcher matcher = context().match(content);
            if (matcher != null) {
                openNext(matcher);
                return forwardFirstTo(matcher, content);
            }
            return Status.CONTINUED;
        }
    }
    
    protected Status matchLazyContinuationLine(Content content) {
        BlockMatcher last = last();
        if (last.canContinue(content)) {
            forwardTo(last, content);
            return Status.CONTINUED;
        } else {
            return Status.NOT_MATCHED;
        }
    }
    
    private Status forwardTo(BlockMatcher next, Content content) {
        BlockMatcher interrupter = next().interrupt(content);
        if (interrupter != null) {
            closeNext();
            openNext(interrupter);
            return forwardFirstTo(interrupter, content);
        }
        BlockMatcher.Status status = next().match(content);
        switch (status) {
        case COMPLETED:
            closeNext();
            break;
        case NOT_MATCHED:
            closeNext();
            BlockMatcher matcher = context().match(content);
            if (matcher != null) {
                openNext(matcher);
                return forwardFirstTo(matcher, content);
            }
            break;
        default:
            break;
        }
        return status;
    }
    
    private Status forwardFirstTo(BlockMatcher next, Content content) {
        Status status = next.match(content);
        if (status == Status.COMPLETED) {
            closeNext();
        }
        return status;
    }
    
    private void openNext(BlockMatcher next) {
        this.next = next;
        if (this.next != null) {
            this.next.bind(context());
        }
    }
    
    private void closeNext() {
        Node node = this.next.close();
        if (node != null) {
            this.children.add(node);
        }
        this.next = null;
    }
}
