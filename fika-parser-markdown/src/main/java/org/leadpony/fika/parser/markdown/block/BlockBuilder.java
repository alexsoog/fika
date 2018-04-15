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

import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Block builder interface.
 * 
 * @author leadpony
 */
public interface BlockBuilder {
    
    /**
     * Building result.
     * 
     * @author leadpony
     */
    public enum Result {
        NOT_MATCHED,
        CONTINUED,
        COMPLETED,
        INTERRUPTED,
        REPLACED
    }
    
    /**
     * Returns the type of the block.
     * 
     * @return the type of the block.
     */
    BlockType blockType();
    
    /**
     * Binds the context to this builder.
     * 
     * @param context the builder context, never be {@code null}.
     */
    void bind(BlockContext context);
    
    /**
     * Returns the context bound to this builder.
     * 
     * @return the context bound to this builder.
     */
    BlockContext context();
    
    /**
     * Returns the first line number where this builder started.
     * 
     * @return the first line number of this builder, starting from one.
     */
    int firstLineNo();
    
    /**
     * Return the number of lines processed by this builder.
     * This method returns zero at the first line of the builder.
     * 
     * @return the number of lines processed.
     */
    int lineCount();
    
    /**
     * Appends a line to this builder.
     * 
     * @param input the line to append.
     * @return the result of the appending operation, never be {@code null}. 
     */
    Result appendLine(InputSequence input);

    /**
     * Appends a lazy continuation line to this builder.
     * 
     * @param input the line to append.
     * @return the result of the appending operation, never be {@code null}. 
     */
    default Result appendLazyLine(InputSequence input) {
        return Result.NOT_MATCHED;
    }
    
    /**
     * Checks if this builder has any children.
     * 
     * @return {@code true} if this builder has any children. {@code false} otherwise.
     */
    default boolean hasChildBuilder() {
        return false;
    }
    
    /**
     * Returns the child builder of this builder.
     * 
     * @return the child builder.
     */
    default BlockBuilder childBuilder() {
        return null;
    }
    
    /**
     * Returns the last descendant builder of this builder.
     * 
     * @return the last descendant builder.
     */
    default BlockBuilder lastBuilder() {
        BlockBuilder builder = this;
        while (builder.hasChildBuilder()) {
            builder = builder.childBuilder();
        }
        return builder;
    }
    
    /**
     * Returns the successor of this builder.
     * 
     * @return the successor of this builder, or {@code null}.
     */
    default BlockBuilder successor() {
        return null;
    }
    
    /**
     * Builds new blocks with this builder.
     * 
     * @param nodes the list of nodes to which new built nodes will be added, never be {@code null}.
     */
    void build(List<Node> nodes);
}
