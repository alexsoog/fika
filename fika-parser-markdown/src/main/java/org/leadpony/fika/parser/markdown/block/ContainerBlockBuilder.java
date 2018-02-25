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
import java.util.Objects;

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
    public final Block build() {
        if (hasChildBuilder()) {
            closeChildBuilder(this.childBuilder);
        }
        Block block = buildBlock();
        if (block != null) {
            block.appendChildren(childNodes());
        }
        return block;
    }
    
    @Override
    protected Result processLine(InputSequence input) {
        return findAndInvokeChildBuilder(input);
    }

    protected Result tryLazyContinuation(InputSequence input) {
        BlockBuilder last = lastBuilder();
        return last.appendLazyLine(input);
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
        Result result = child.appendLine(input);
        if (result == Result.COMPLETED || result == Result.NOT_MATCHED) {
            closeChildBuilder(child);
        } else if (result == Result.INTERRUPTED) {
            closeChildBuilder(child);
            child = child.successor();
            openChildBuilder(child);
            result = child.appendLine(input);
            if (result == Result.COMPLETED || result == Result.NOT_MATCHED) {
                closeChildBuilder(child);
            }
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
    
    protected void openChildBuilder(BlockBuilder childBuilder) {
        Objects.requireNonNull(childBuilder, "childBuilder must not be null");
        if (this.childBuilder != null) {
            closeChildBuilder(this.childBuilder);
        }
        this.childBuilder = childBuilder;
    }
    
    protected void closeChildBuilder(BlockBuilder childBuilder) {
        Objects.requireNonNull(childBuilder, "childBuilder must not be null");
        if (!childBuilder.isCanceled()) {
            Node node = childBuilder.build();
            if (node != null) {
                this.childNodes.add(node);
            }
        }
        this.childBuilder = null;
    }
    
    protected List<Node> childNodes() {
        return childNodes;
    }
}
