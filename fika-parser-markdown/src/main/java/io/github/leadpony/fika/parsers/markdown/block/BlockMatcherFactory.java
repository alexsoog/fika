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

import java.util.Collections;
import java.util.Set;

/**
 * Factory for producing block matchers.
 * 
 * @author leadpony
 */
public interface BlockMatcherFactory {

    BlockType blockType();
    
    default int precedence() {
        return blockType().precedence();
    }
    
    default Set<? extends BlockType> interruptible() {
        return Collections.emptySet();
    }
    
    /**
     * Creates a new block matcher for the given content.
     * 
     * @param content the content of the line.
     * 
     * @return new matcher if found.
     */
    BlockMatcher newMatcher(Content content);
    
    default BlockMatcher newInterrupter(Content content, BlockMatcher current) {
        return null;
    }
}
