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

import io.github.leadpony.fika.core.nodes.Document;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleDocument;

/**
 * @author leadpony
 */
class DocumentMatcher extends ContainerBlockMatcher {
    
    DocumentMatcher() {
    }
   
    @Override
    public BlockType blockType() {
        return BasicBlockType.DOCUMENT;
    }
    
    @Override
    public Result match(Content content) {
        super.match(content);
        return Result.CONTINUED;
    }
    
    @Override
    protected Document buildNode() {
        return new SimpleDocument(); 
    }
}
