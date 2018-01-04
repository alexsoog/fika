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
package io.github.leadpony.fika.parsers.markdown.inline;

import io.github.leadpony.fika.core.model.NodeFactory;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

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
        
        InlineAppender getAppender();
        
        DelimiterStack getDelimiterStack();
        
        DelimiterProcessor getDelimiterProcessor();
    }
}
