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
package org.leadpony.fika.format.markdown.block;

import java.util.Collections;
import java.util.Set;

import org.leadpony.fika.format.markdown.common.InputSequence;

/**
 * Matcher for blocks.
 * 
 * @author leadpony
 */
public interface BlockMatcher {

    /**
     * Returns the type of the block to match.
     * 
     * @return the type of the block.
     */
    BlockType blockType();

    /**
     * Returns the precedence of this matcher.
     * 
     * @return the precedence of this matcher.
     */
    default int precedence() {
        return blockType().precedence();
    }
    
    /**
     * Creates a new block builder for the given content.
     * 
     * @param input the content of the line.
     * 
     * @return new block builder if matched, or {@code null} if not matched.
     */
    BlockBuilder newBuilder(InputSequence input);
    
    /**
     * Returns the block types which this matcher can interrupt.
     * 
     * @return the set of block types.
     */
    default Set<? extends BlockType> typesToInterrupt() {
        return Collections.emptySet();
    }
    
    /**
     * Creates a new interrupting block builder for the given input.
     * 
     * @param input the content of the line.
     * @param current the builder to be interrupted.
     * @param mode the current builder mode.
     * @return new block builder if matched, or {@code null} if not matched.
     */
    default BlockBuilder newInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        return null;
    }

    /**
     * Returns the block types which this matcher can replace.
     * 
     * @return the set of block types.
     */
    default Set<? extends BlockType> typesToReplace() {
        return Collections.emptySet();
    }
    
    /**
     * Creates a new replacing block builder for the given input.
     * 
     * @param input the content of the line.
     * @param current the builder to be replaced.
     * @param mode the current builder mode.
     * @return new block builder if matched, or {@code null} if not matched.
     */
    default BlockBuilder newReplacingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        return null;
    }
}
