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
package org.leadpony.fika.parser.markdown.block.commonmark;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.leadpony.fika.parser.markdown.block.ContainerBlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;
import org.leadpony.fika.parser.model.BlockQuote;

/**
 * @author leadpony
 */
class BlockQuoteBuilder extends ContainerBlockBuilder {
    
    static final Pattern BLOCK_QUOTE_MARKER = Pattern.compile("^\\u0020{0,3}>\\u0020?");

    @Override
    public BlockType blockType() {
        return BasicBlockType.BLOCK_QUOTE;
    }
   
    @Override
    public Result processLine(InputSequence input) {
        Matcher m = BLOCK_QUOTE_MARKER.matcher(input);
        if (m.find()) {
            int skip = m.group(0).length();
            findAndInvokeChildBuilder(input.subSequence(skip));
            return Result.CONTINUED;
        } else {
            return tryLazyContinuation(input);
        }
    }

    @Override
    protected BlockQuote buildBlock() {
        return getNodeFactory().newBlockQuote();
    }
}
