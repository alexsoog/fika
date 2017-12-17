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

import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.parser.helper.nodes.Modifiable;

/**
 * @author leadpony
 */
public interface BlockMatcher {
    
    static final int DEFAULT_PRECEDENCE = Integer.MAX_VALUE;
    
    enum Status {
        NOT_MATCHED,
        CONTINUED,
        COMPLETED
    }
    
    /**
     * Binds the context to this matcher.
     * 
     * @param context the matching context.
     */
    void bind(Context context);
    
    default int precedence() {
        return DEFAULT_PRECEDENCE;
    }

    default boolean isInterruptible() {
        return false;
    }
    
    default BlockMatcher interrupt(Content content) {
        return null;
    }
    
    Status match(Content content);
    
    /**
     * Closes this matcher.
     * 
     * @return the node found by this matcher.
     */
    Node close();
    
    interface Context {
        
        BlockMatcher match(Content content);

        BlockMatcher match(Content content, int precedence);
        
        void addInline(Modifiable container, String content);
    }

    interface Factory {
        
        default int precedence() {
            return DEFAULT_PRECEDENCE;
        }
        
        BlockMatcher newMatcher(Content content);
    }
}
