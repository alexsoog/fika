/*
 * Copyright 2017-2019 the Fika authors.
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
package org.leadpony.fika.format.markdown.block.commonmark;

import org.leadpony.fika.format.markdown.block.BlockBuilder;
import org.leadpony.fika.format.markdown.block.BlockMatcher;
import org.leadpony.fika.format.markdown.block.BlockType;
import org.leadpony.fika.format.markdown.common.InputSequence;

/**
 * Matcher implementation for indented code.
 * 
 * @author leadpony
 */
public class IndentedCodeMatcher implements BlockMatcher {

    @Override
    public BlockType blockType() {
        return BasicBlockType.INDENTED_CODE;
    }
 
    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        if (!input.hasLeadingSpaces(IndentedCodeBuilder.INDENT_SIZE)) {
            return null;
        }
        return new IndentedCodeBuilder();
    }
}
