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

import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.core.model.Text;
import org.leadpony.fika.parser.markdown.block.AbstractBlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * @author leadpony
 *
 */
class SetextHeadingBuilder extends AbstractBlockBuilder {
    
    private final int level;
    private final String title;
    
    SetextHeadingBuilder(int level, String title) {
        this.level = level;
        this.title = title;
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.SETEXT_HEADING;
    }

    @Override
    public Result append(InputSequence input) {
        return Result.COMPLETED;
    }

    @Override
    protected Block buildBlock() {
        Block heading = getNodeFactory().newHeading(this.level);
        Text text = getNodeFactory().newText(this.title);
        context().addInline(text);
        heading.appendChild(text);
        return heading;
    }
}
