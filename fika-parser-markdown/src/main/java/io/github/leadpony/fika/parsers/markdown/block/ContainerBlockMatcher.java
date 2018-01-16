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

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Matcher for container blocks such as documents, block quotes, and lists.
 * 
 * @author leadpony
 */
public abstract class ContainerBlockMatcher extends AbstractBlockMatcher {
    
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
    public Result match(InputSequence input) {
        return findAndInvokeChildMatcher(input);
    }

    @Override
    public final Block close() {
        if (hasChildMatcher()) {
            closeCurrentChildMatcher();
        }
        Block block = buildBlock();
        block.appendChildren(childNodes());
        return block;
    }
    
    protected Result matchLazyContinuationLine(InputSequence input) {
        BlockMatcher last = lastMatcher();
        return last.continueLazily(input);
    }
    
    protected Result findAndInvokeChildMatcher(InputSequence input) {
        if (!hasChildMatcher()) {
            if (findChildMatcher(input) == null) {
                return Result.NOT_MATCHED;
            }
        }
        return invokeChildMatcherAndRetry(input);
    }
    
    protected Result invokeChildMatcherAndRetry(InputSequence input) {
        Result result = invokeChildMatcher(input);
        if (result == Result.NOT_MATCHED) {
            if (findChildMatcher(input) == null) {
                return Result.NOT_MATCHED;
            }
            result = invokeChildMatcher(input);
        }
        return result;
    }
    
    protected Result invokeChildMatcher(InputSequence input) {
        BlockMatcher child = childMatcher();
        assert(child != null);
        if (child.isInterruptible()) {
            BlockMatcher interrupter = child.interrupt(input, MatcherMode.NORMAL);
            if (interrupter != null) {
                openChildMatcher(interrupter);
                child = interrupter;
            }
        }
        Result result = callMatcherDirect(child, input);
        if (result == Result.COMPLETED || result == Result.NOT_MATCHED) {
            closeCurrentChildMatcher();
        }
        return result;
    }
    
    protected BlockMatcher findChildMatcher(InputSequence input) {
        BlockMatcher matched = context().finder().findMatcher(input);
        if (matched != null) {
            openChildMatcher(matched);
        }
        return matched;
    }
    
    private Result callMatcherDirect(BlockMatcher matcher, InputSequence input) {
        return matcher.match(input);
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
