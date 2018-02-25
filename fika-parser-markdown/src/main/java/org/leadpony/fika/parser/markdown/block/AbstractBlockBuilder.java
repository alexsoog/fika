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

import org.leadpony.fika.core.model.Block;
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
    private boolean canceled;
    private BlockBuilder successor;
    
    protected AbstractBlockBuilder() {
        this.canceled = false;
    }

    @Override
    public void bind(BlockContext context, int firstLineNo) {
        this.context = context;
        this.firstLineNo = firstLineNo;
    }
    
    @Override
    public BlockContext context() {
        return context;
    }
    
    @Override
    public int lineNo() {
        return context().lineNo() - firstLineNo + 1;
    }
    
    @Override
    public void cancel() {
        this.canceled = true;
    }
    
    @Override
    public boolean isCanceled() {
        return canceled;
    }
   
    @Override
    public Result appendLine(InputSequence input) {
        if (isInterruptible()) {
            BlockBuilder interrupter = interrupt(input, BuilderMode.NORMAL);
            if (interrupter != null) {
                setSuccessor(interrupter);
                return Result.INTERRUPTED;
            }
        }
        return processLine(input);
    }
    
    @Override
    public BlockBuilder successor() {
        return successor;
    }
    
    @Override
    public Block build() {
        return buildBlock();
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
    
    /**
     * Interrupts this builder.
     * 
     * @param input new content.
     * @param mode current mode of this builder.
     * @return the interrupting builder if found.
     */
    protected BlockBuilder interrupt(InputSequence input, BuilderMode mode) {
        if (isInterruptible()) {
            return context().finder().findInterruptingBuilder(input, this, mode);
        } else {
            return null;
        }
    }

    protected NodeFactory getNodeFactory() {
        return context.getNodeFactory();
    }
    
    protected abstract Block buildBlock();
}
