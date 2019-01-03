/*
 * Copyright 2017-2019 the Fika authors.
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
    private int numberOfCompletedChildren;
    
    protected ContainerBlockBuilder() {
    }

    @Override
    public void bind(BlockContext context) {
        super.bind(context);
        if (this.childBuilder != null) {
            this.childBuilder.bind(context);
        }
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
    public void build(List<Node> nodes) {
        if (hasChildBuilder()) {
            closeChildBuilder(this.childBuilder);
        }
        Block newBlock = buildBlock();
        if (newBlock != null) {
            newBlock.appendChildren(collectChildNodes());
            nodes.add(newBlock);
        }
    }
    
    public void openChildBuilder(BlockBuilder childBuilder) {
        Objects.requireNonNull(childBuilder, "childBuilder must not be null");
        if (this.childBuilder != null) {
            closeChildBuilder(this.childBuilder);
        }
        this.childBuilder = childBuilder;
    }
    
    public void closeChildBuilder(BlockBuilder childBuilder) {
        Objects.requireNonNull(childBuilder, "childBuilder must not be null");
        buildChild(childBuilder, this.childNodes);
        this.childBuilder = null;
        this.numberOfCompletedChildren++;
    }
    
    public void resetChildBuilder() {
        this.childBuilder = null;
    }
    
    /**
     * Returns the number of completed children.
     * 
     * @return the number of completed children.
     */
    public int countCompletedChildren() {
        return this.numberOfCompletedChildren;
    }
    
    public boolean hasCompletedChildren() {
        return countCompletedChildren() > 0;
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
            if (findAndOpenChildBuilder(input) == null) {
                return Result.NOT_MATCHED;
            }
        }
        return invokeChildBuilderAndRetry(input);
    }
    
    protected Result invokeChildBuilderAndRetry(InputSequence input) {
        Result result = invokeChildBuilder(input);
        if (result == Result.NOT_MATCHED) {
            if (findAndOpenChildBuilder(input) == null) {
                return Result.NOT_MATCHED;
            }
            result = invokeChildBuilder(input);
        }
        return result;
    }
    
    protected Result invokeChildBuilder(InputSequence input) {
        BlockBuilder child = childBuilder();
        assert(child != null);
        int count = 2;
        Result result;
        do {
            result = child.appendLine(input);
            if (result == Result.INTERRUPTED) {
                BlockBuilder successor = child.successor();
                closeChildBuilder(child);
                openChildBuilder(successor);
                child = successor;
            } else if (result == Result.REPLACED) {
                BlockBuilder successor = child.successor();
                replaceChildBuilder(child, successor);
                child = successor;
            } else if (result == Result.COMPLETED || result == Result.NOT_MATCHED) {
                closeChildBuilder(child);
                break;
            } else {
                break;
            }
        } while (count-- > 0);
        return result;
    }
    
    protected BlockBuilder findAndOpenChildBuilder(InputSequence input) {
        BlockBuilder found = findChildBuilder(input);
        if (found != null) {
            openChildBuilder(found);
        }
        return found;
    }

    protected BlockBuilder findChildBuilder(InputSequence input) {
        return context().finder().findBuilder(input);
    }
    
    protected void replaceChildBuilder(BlockBuilder oldBuilder, BlockBuilder newBuilder) {
        this.childBuilder = newBuilder;
    }
    
    protected void buildChild(BlockBuilder builder, List<Node> nodes) {
        builder.build(nodes);
    }
    
    protected List<Node> collectChildNodes() {
        return childNodes;
    }
}
