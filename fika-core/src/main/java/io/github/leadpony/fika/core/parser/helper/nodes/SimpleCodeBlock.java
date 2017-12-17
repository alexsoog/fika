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
package io.github.leadpony.fika.core.parser.helper.nodes;

import io.github.leadpony.fika.core.nodes.CodeBlock;

/**
 * @author leadpony
 */
public class SimpleCodeBlock implements CodeBlock {

    private final String content;
    private final String language;
    
    public SimpleCodeBlock(String content, String language) {
        this.content = content;
        this.language = language;
    }

    @Override
    public String content() {
        return content;
    }
    
    @Override
    public String language() {
        return language;
    }
    
    @Override
    public String toString() {
        return content;
    }
}
