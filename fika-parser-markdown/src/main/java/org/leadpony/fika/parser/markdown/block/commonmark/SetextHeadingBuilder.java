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

import org.leadpony.fika.parser.markdown.block.AbstractBlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;
import org.leadpony.fika.parser.model.Block;
import org.leadpony.fika.parser.model.Text;

/**
 * Buidler of setext heading.
 * 
 * @author leadpony
 */
class SetextHeadingBuilder extends AbstractBlockBuilder {
    
    private final ParagraphBuilder replacedBuilder;
    private final int level;
    
    SetextHeadingBuilder(ParagraphBuilder replaced, int level) {
        this.replacedBuilder = replaced;
        this.level = level;
    }
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.SETEXT_HEADING;
    }
    
    @Override
    public int firstLineNo() {
        return replacedBuilder.firstLineNo();
    }

    @Override
    public Result processLine(InputSequence input) {
        return Result.COMPLETED;
    }

    @Override
    protected Block buildBlock() {
        String title = this.replacedBuilder.getContent();
        Text text = getNodeFactory().newText(title);
        Block heading = getNodeFactory().newHeading(this.level);
        context().addInline(text);
        heading.appendChild(text);
        return heading;
    }
}
