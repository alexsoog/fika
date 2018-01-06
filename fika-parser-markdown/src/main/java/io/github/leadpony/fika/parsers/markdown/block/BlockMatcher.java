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
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * @author leadpony
 */
public interface BlockMatcher {
    
    enum Result {
        NOT_MATCHED,
        CONTINUED,
        COMPLETED
    }
    
    BlockType blockType();
    
    /**
     * Binds the context to this matcher.
     * 
     * @param context the matching context.
     */
    void bind(Context context);
    
    default boolean hasChildMatcher() {
        return false;
    }
    
    default BlockMatcher childMatcher() {
        return null;
    }
    
    default BlockMatcher lastMatcher() {
        BlockMatcher matcher = this;
        while (matcher.hasChildMatcher()) {
            matcher = matcher.childMatcher();
        }
        return matcher;
    }
    
    int lineNo();

    Result match(InputSequence input);

    default boolean isInterruptible() {
        return false;
    }
    
    default BlockMatcher interrupt(InputSequence content) {
        return null;
    }
    
    default Result continueLazily(InputSequence content) {
        return Result.NOT_MATCHED;
    }
    
    /**
     * Closes this matcher.
     * 
     * @return the block found by this matcher.
     */
    Block close();
    
    /**
     * Context for block matchers.
     */
    interface Context {
        
        /**
         * Return the current line number.
         * 
         * @return current line number, starting from one.
         */
        int lineNo();
        
        BlockMatcher findMatcher(InputSequence content);

        BlockMatcher findInterruptingMatcher(InputSequence content, BlockMatcher current);
        
        NodeFactory nodeFactory();
        
        void addInline(Text text);
    }
}
