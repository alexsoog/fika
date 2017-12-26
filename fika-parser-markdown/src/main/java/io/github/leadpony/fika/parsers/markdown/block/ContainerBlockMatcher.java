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
    
    private BlockMatcher childMatcher;
    private final List<Node> childNodes = new ArrayList<>();
    
    protected ContainerBlockMatcher() {
    }

    @Override
    public boolean hasChildMatcher() {
        return this.childMatcher != null;
    }
    
    @Override
    public BlockMatcher childMatcher() {
        return childMatcher;
    }
    
    @Override
    public Result match(Content content) {
        return findAndInvokeChildMatcher(content);
    }

    @Override
    public final Node close() {
        if (hasChildMatcher()) {
            closeCurrentChildMatcher();
        }
        Node node = buildNode();
        node.childNodes().addAll(childNodes());
        return node;
    }
    
    protected Result matchLazyContinuationLine(Content content) {
        BlockMatcher last = lastMatcher();
        return last.continueLazily(content);
    }
    
    protected Result findAndInvokeChildMatcher(Content content) {
        if (!hasChildMatcher()) {
            if (findChildMatcher(content) == null) {
                return Result.NOT_MATCHED;
            }
        }
        return invokeChildMatcherAndRetry(content);
    }
    
    protected Result invokeChildMatcherAndRetry(Content content) {
        Result result = invokeChildMatcher(content);
        if (result == Result.NOT_MATCHED) {
            if (findChildMatcher(content) == null) {
                return Result.NOT_MATCHED;
            }
            result = invokeChildMatcher(content);
        }
        return result;
    }
    
    protected Result invokeChildMatcher(Content content) {
        BlockMatcher child = childMatcher();
        assert(child != null);
        if (child.isInterruptible()) {
            BlockMatcher interrupter = child.interrupt(content);
            if (interrupter != null) {
                openChildMatcher(interrupter);
                child = interrupter;
            }
        }
        Result result = callMatcherDirect(child, content);
        if (result == Result.COMPLETED || result == Result.NOT_MATCHED) {
            closeCurrentChildMatcher();
        }
        return result;
    }
    
    protected BlockMatcher findChildMatcher(Content content) {
        BlockMatcher matched = context().findMatcher(content);
        if (matched != null) {
            openChildMatcher(matched);
        }
        return matched;
    }
    
    private Result callMatcherDirect(BlockMatcher matcher, Content content) {
        return matcher.match(content);
    }
    
    protected void openChildMatcher(BlockMatcher childMatcher) {
        if (childMatcher == null) {
            throw new NullPointerException();
        }
        closeCurrentChildMatcher();
        this.childMatcher = childMatcher;
        childMatcher.bind(context());
    }
    
    protected void closeChildMatcher(BlockMatcher childMatcher) {
        if (childMatcher == null) {
            throw new NullPointerException();
        }
        Node node = childMatcher.close();
        if (node != null) {
            this.childNodes.add(node);
        }
        this.childMatcher = null;
    }
    
    protected void closeCurrentChildMatcher() {
        if (this.childMatcher != null) {
            closeChildMatcher(this.childMatcher);
        }
    }
    
    protected List<Node> childNodes() {
        return childNodes;
    }
}
