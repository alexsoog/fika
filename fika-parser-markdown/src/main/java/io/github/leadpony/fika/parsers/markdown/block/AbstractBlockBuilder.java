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

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.NodeFactory;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Skeletal implementation of {@link BlockBuilder}.
 * 
 * @author leadpony
 */
public abstract class AbstractBlockBuilder implements BlockBuilder {

    private Context context;
    private int firstLineNo;
    
    @Override
    public void bind(Context context) {
        this.context = context;
        this.firstLineNo = context.lineNo();
    }
    
    @Override
    public int lineNo() {
        return context().lineNo() - firstLineNo + 1;
    }
    
    @Override
    public Result match(InputSequence input) {
        return Result.NOT_MATCHED;
    }
    
    @Override
    public BlockBuilder interrupt(InputSequence input, BuilderMode mode) {
        if (isInterruptible()) {
            return context().finder().findInterruptingBuilder(input, this, mode);
        } else {
            return null;
        }
    }
    
    @Override
    public Block close() {
        return buildBlock();
    }

    protected Context context() {
        return context;
    }
    
    protected NodeFactory getNodeFactory() {
        return context.getNodeFactory();
    }
    
    protected abstract Block buildBlock();
}
