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
package org.leadpony.fika.parser.markdown.block.matchers;

import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockMatcher;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Matcher implementation for link reference definition.
 * 
 * @author leadpony
 */
public class LinkDefinitionMatcher implements BlockMatcher {

    @Override
    public BlockType blockType() {
        return BasicBlockType.LINK_DEFINITION;
    }

    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        int spaces = input.countLeadingSpaces(0,  3);
        if (input.length() > spaces && input.charAt(spaces) == '[') {
            return new LinkDefinitionBuilder();
        } else {
            return null;
        }
    }
}
