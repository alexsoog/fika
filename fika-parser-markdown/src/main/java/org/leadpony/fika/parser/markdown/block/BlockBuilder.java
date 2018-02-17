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
import org.leadpony.fika.core.model.Text;
import org.leadpony.fika.parser.markdown.common.InputSequence;
import org.leadpony.fika.parser.markdown.common.LinkDefinitionMap;

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
        COMPLETED
    }
    
    /**
     * Returns the type of the block matcher.
     * 
     * @return the type of the block matcher.
     */
    MatcherType matcherType();
    
    /**
     * Binds the context to this builder.
     * 
     * @param context the builder context.
     */
    void bind(Context context);
    
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
     * Return the current line number of this builder.
     * 
     * @return the current line number.
     */
    int lineNo();

    Result match(InputSequence input);

    /**
     * Checks if this builder is interruptible.
     * 
     * @return {@code true} if interruptible.
     */
    default boolean isInterruptible() {
        return false;
    }
    
    /**
     * Interrupts this builder.
     * 
     * @param input new content.
     * @param mode current mode of this builder.
     * @return the interrupting builder if found.
     */
    default BlockBuilder interrupt(InputSequence input, BuilderMode mode) {
        return null;
    }
    
    default Result continueLazily(InputSequence input) {
        return Result.NOT_MATCHED;
    }
    
    /**
     * Closes this builder.
     * 
     * @return the block built by this builder.
     */
    Block close();
    
    /**
     * Context shared by block builders.
     */
    interface Context {
        
        /**
         * Return the current line number.
         * 
         * @return current line number, starting from one.
         */
        int lineNo();
        
        NodeFactory getNodeFactory();
        
        BlockBuilderFinder finder();
        
        LinkDefinitionMap getLinkDefinitionMap();
        
        void addInline(Text text);
    }
}
