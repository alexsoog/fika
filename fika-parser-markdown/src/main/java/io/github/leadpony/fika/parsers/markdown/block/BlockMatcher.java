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

import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Matcher for blocks.
 * 
 * @author leadpony
 */
public interface BlockMatcher {

    /**
     * Returns the type of this block matcher.
     * 
     * @return the type of this block matcher.
     */
    MatcherType matcherType();

    /**
     * Returns the precedence of this matcher.
     * 
     * @return the precedence of this matcher.
     */
    default int precedence() {
        return matcherType().precedence();
    }
    
    /**
     * Creates a new block builder for the given content.
     * 
     * @param input the content of the line.
     * 
     * @return new block builder if matched, or {@code null} if not matched.
     */
    BlockBuilder newBuilder(InputSequence input);
    
    default Set<? extends MatcherType> interruptible() {
        return Collections.emptySet();
    }
    
    default BlockBuilder newInterruptingBuilder(InputSequence input, BlockBuilder current, BuilderMode mode) {
        return null;
    }
}
