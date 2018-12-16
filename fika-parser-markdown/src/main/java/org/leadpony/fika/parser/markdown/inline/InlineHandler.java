/*
 * Copyright 2017-2018 the Fika authors.
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
package org.leadpony.fika.parser.markdown.inline;

import org.leadpony.fika.core.model.NodeFactory;
import org.leadpony.fika.parser.markdown.common.InputSequence;
import org.leadpony.fika.parser.markdown.common.LinkDefinitionMap;

/**
 * The interface for handling inline nodes. 
 * 
 * @author leadpony
 */
public interface InlineHandler {
    
    /**
     * Returns the letters which will trigger this handler.
     * 
     * @return triggering letters of this handler.
     */
    char[] triggerLetters();

    /**
     * Returns the type of this handler.
     * 
     * @return the type of this handler.
     */
    HandlerType handlerType();
    
    /**
     * Returns the precedence of this handler.
     * 
     * @return the precedence of this handler.
     */
    default int precedence() {
        return handlerType().precedence();
    }
    
    void bind(Context context);

    default int handleContent(InputSequence input) {
        return 0;
    }
    
    default int handleContent(String input, int currentIndex) {
        return handleContent(InputSequence.withOffset(input, currentIndex));
    }
    
    default InlineHandler or(InlineHandler other) {
        return new CompositeInlineHandler(this, other);
    }
    
    /**
     * Shared context for inline handlers.
     * 
     * @author leadpony
     */
    interface Context {
        
        /**
         * Returns the factory for creating inline nodes.
         * 
         * @return the instance of {@link NodeFactory}.
         */
        NodeFactory getNodeFactory();
        
        /**
         * Return the original input string.
         * 
         * @return the original input string.
         */
        String input();

        InlineAppender getAppender();

        DelimiterStack getDelimiterStack();
        
        DelimiterProcessor getDelimiterProcessor();
        
        LinkDefinitionMap getLinkDefinitionMap();
    }
}
