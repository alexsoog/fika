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

import java.util.function.Consumer;

import org.leadpony.fika.format.markdown.block.AbstractBlockBuilder;
import org.leadpony.fika.format.markdown.block.BlockType;
import org.leadpony.fika.format.markdown.common.InputSequence;
import org.leadpony.fika.format.markdown.common.LinkDefinition;
import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.core.model.Paragraph;
import org.leadpony.fika.core.model.Text;

/**
 * @author leadpony
 */
class LinkDefinitionBuilder extends AbstractBlockBuilder implements Consumer<LinkDefinition> {
    
    private final ParagraphContentBuilder contentBuilder = new ParagraphContentBuilder();
    private final LinkDefinitionRecognizer recognizer = new LinkDefinitionRecognizer(this);
   
    @Override
    public BlockType blockType() {
        return BasicBlockType.LINK_DEFINITION;
    }

    @Override
    public Result processLine(InputSequence input) {
        if (lineCount() > 0 && input.isBlank()) {
            return Result.COMPLETED;
        }
        contentBuilder.addLine(input);
        if (recognizer.acceptLine(input)) {
            return Result.COMPLETED;
        }
        return Result.CONTINUED;
    }
    
    @Override
    public boolean isInterruptible() {
        return true;
    }
   
    @Override
    protected Block buildBlock() {
        int linesConsumed = recognizer.flush();
        String content = contentBuilder.toContent(linesConsumed);
        if (content.isEmpty()) {
            return null;
        }
        Text text = getNodeFactory().createText(content);
        context().addInline(text);
        Paragraph block = getNodeFactory().createParagraph();
        block.appendChild(text);
        return block;
    }

    /**
     * Processes the link definition found.
     * 
     * @param definition the link definition found.
     */
    @Override
    public void accept(LinkDefinition definition) {
        context().getLinkDefinitionMap()
            .put(definition.label(), definition);
    }
}
