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

import java.util.List;

import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.NodeFactory;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Skeletal implementation of {@link BlockBuilder}.
 * 
 * @author leadpony
 */
public abstract class AbstractBlockBuilder implements BlockBuilder {

    private BlockContext context;
    private int firstLineNo;
    private BlockBuilder successor;
    
    protected AbstractBlockBuilder() {
    }

    @Override
    public void bind(BlockContext context) {
        if (this.context != null) {
            return;
        }
        this.context = context;
        this.firstLineNo = context.lineNo();
    }
    
    @Override
    public BlockContext context() {
        return context;
    }
    
    @Override
    public int firstLineNo() {
        return firstLineNo;
    }
    
    @Override
    public int lineCount() {
        return context().lineNo() - firstLineNo();
    }
    
    @Override
    public Result appendLine(InputSequence input) {
        if (lineCount() > 0) {
            BlockBuilder successor = replace(input, BuilderMode.NORMAL);
            if (successor != null) {
                setSuccessor(successor);
                return Result.REPLACED;
            } else if (isInterruptible()) {
                successor = interrupt(input, BuilderMode.NORMAL);
                if (successor != null) {
                    setSuccessor(successor);
                    return Result.INTERRUPTED;
                }
            }
        }
        Result result = processLine(input);
        postprocessLine(input);
        return result;
    }
    
    @Override
    public BlockBuilder successor() {
        return successor;
    }
    
    @Override
    public void build(List<Node> nodes) {
        Node node = buildBlock();
        if (node != null) {
            nodes.add(node);
        }
    }

    /**
     * Checks if this builder is interruptible.
     * 
     * @return {@code true} if interruptible.
     */
    protected boolean isInterruptible() {
        return false;
    }
    
    protected void setSuccessor(BlockBuilder successor) {
        this.successor = successor;
    }
    
    protected Result processLine(InputSequence input) {
        return Result.NOT_MATCHED;
    }
    
    protected void postprocessLine(InputSequence input) {
    }
    
    /**
     * Tries interrupting this builder.
     * 
     * @param input current content.
     * @param mode current mode of this builder.
     * @return the interrupting builder if found, or {@code null} if not found.
     */
    protected BlockBuilder interrupt(InputSequence input, BuilderMode mode) {
        return context().finder().findInterruptingBuilder(input, this, mode);
    }
    
    protected BlockBuilder replace(InputSequence input, BuilderMode mode) {
        return context().finder().findReplacingBuilder(input, this, mode);
    }

    protected NodeFactory getNodeFactory() {
        return context.getNodeFactory();
    }
    
    protected abstract Block buildBlock();
}
