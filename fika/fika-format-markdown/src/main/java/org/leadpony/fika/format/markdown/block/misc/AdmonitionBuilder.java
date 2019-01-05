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
package org.leadpony.fika.format.markdown.block.misc;

import static org.leadpony.fika.format.markdown.common.Strings.capitalize;

import org.leadpony.fika.format.markdown.block.BlockType;
import org.leadpony.fika.format.markdown.block.ContainerBlockBuilder;
import org.leadpony.fika.format.markdown.block.commonmark.BasicBlockType;
import org.leadpony.fika.format.markdown.common.InputSequence;
import org.leadpony.fika.core.model.Block;

/**
 * Block-styled side content to the documentation,
 * such as summaries, notes, hints or warnings.
 *
 * @author leadpony
 */
class AdmonitionBuilder extends ContainerBlockBuilder {

    static final int INDENT_SIZE = 4;

    private final String type;
    private final String title;

    AdmonitionBuilder(String type, String title) {
        this.type = type.toLowerCase();
        this.title = (title != null) ? title : capitalize(this.type);
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.ADMONITION;
    }

    @Override
    public Result processLine(InputSequence input) {
        if (lineCount() == 0) {
            return Result.CONTINUED;
        }
        final int spaces = input.countLeadingSpaces(0, 4);
        if (spaces >= 4) {
            findAndInvokeChildBuilder(input.subSequence(spaces));
            return Result.CONTINUED;
        } else if (input.isBlank()) {
            findAndInvokeChildBuilder(input);
            return Result.CONTINUED;
        } else {
            return tryLazyContinuation(input);
        }
    }

    @Override
    protected Block buildBlock() {
        return getNodeFactory().createAdmonition(this.type, this.title);
    }
}
