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
package io.github.leadpony.fika.parsers.markdown.block.matchers;

import io.github.leadpony.fika.core.model.Heading;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.block.AbstractBlockBuilder;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockTrait;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Builder of ATX headings.
 * 
 * @author leadpony
 */
class AtxHeadingBuilder extends AbstractBlockBuilder {

    private final int level;
    private final String title;
  
    AtxHeadingBuilder(int level, String title) {
        this.level = level;
        this.title = title;
    }

    @Override
    public BlockTrait blockTrait() {
        return BlockType.ATX_HEADING;
    }
    
    @Override
    public Result match(InputSequence input) {
        return Result.COMPLETED;
    }
    
    @Override
    protected Heading buildBlock() {
        Heading block = getNodeFactory().newHeading(this.level);
        Text text = getNodeFactory().newText(this.title);
        block.appendChild(text);
        context().addInline(text);
        return block;
    }
}
