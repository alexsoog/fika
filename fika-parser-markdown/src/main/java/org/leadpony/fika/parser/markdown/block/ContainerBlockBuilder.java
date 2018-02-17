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
package org.leadpony.fika.parser.markdown.block;

import java.util.ArrayList;
import java.util.List;

import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Builder of container blocks such as documents, block quotes, and lists.
 * 
 * @author leadpony
 */
public abstract class ContainerBlockBuilder extends AbstractBlockBuilder {
    
    private BlockBuilder childBuilder;
    private final List<Node> childNodes = new ArrayList<>();
    
    protected ContainerBlockBuilder() {
    }

    @Override
    public boolean hasChildBuilder() {
        return this.childBuilder != null;
    }
    
    @Override
    public BlockBuilder childBuilder() {
        return childBuilder;
    }
    
    @Override
    public Result match(InputSequence input) {
        return findAndInvokeChildBuilder(input);
    }

    @Override
    public final Block close() {
        if (hasChildBuilder()) {
            closeCurrentChildBuilder();
        }
        Block block = buildBlock();
        block.appendChildren(childNodes());
        return block;
    }
    
    protected Result matchLazyContinuationLine(InputSequence input) {
        BlockBuilder last = lastBuilder();
        return last.continueLazily(input);
    }
    
    protected Result findAndInvokeChildBuilder(InputSequence input) {
        if (!hasChildBuilder()) {
            if (findChildBuilder(input) == null) {
                return Result.NOT_MATCHED;
            }
        }
        return invokeChildBuilderAndRetry(input);
    }
    
    protected Result invokeChildBuilderAndRetry(InputSequence input) {
        Result result = invokeChildBuilder(input);
        if (result == Result.NOT_MATCHED) {
            if (findChildBuilder(input) == null) {
                return Result.NOT_MATCHED;
            }
            result = invokeChildBuilder(input);
        }
        return result;
    }
    
    protected Result invokeChildBuilder(InputSequence input) {
        BlockBuilder child = childBuilder();
        assert(child != null);
        if (child.isInterruptible()) {
            BlockBuilder interrupter = child.interrupt(input, BuilderMode.NORMAL);
            if (interrupter != null) {
                openChildBuilder(interrupter);
                child = interrupter;
            }
        }
        Result result = callBuilderDirect(child, input);
        if (result == Result.COMPLETED || result == Result.NOT_MATCHED) {
            closeCurrentChildBuilder();
        }
        return result;
    }
    
    protected BlockBuilder findChildBuilder(InputSequence input) {
        BlockBuilder matched = context().finder().findBuilder(input);
        if (matched != null) {
            openChildBuilder(matched);
        }
        return matched;
    }
    
    private Result callBuilderDirect(BlockBuilder matcher, InputSequence input) {
        return matcher.match(input);
    }
    
    protected void openChildBuilder(BlockBuilder childBuilder) {
        if (childBuilder == null) {
            throw new NullPointerException();
        }
        closeCurrentChildBuilder();
        this.childBuilder = childBuilder;
        childBuilder.bind(context());
    }
    
    protected void closeChildBuilder(BlockBuilder childBuilder) {
        if (childBuilder == null) {
            throw new NullPointerException();
        }
        Node node = childBuilder.close();
        if (node != null) {
            this.childNodes.add(node);
        }
        this.childBuilder = null;
    }
    
    protected void closeCurrentChildBuilder() {
        if (this.childBuilder != null) {
            closeChildBuilder(this.childBuilder);
        }
    }
    
    protected List<Node> childNodes() {
        return childNodes;
    }
}
