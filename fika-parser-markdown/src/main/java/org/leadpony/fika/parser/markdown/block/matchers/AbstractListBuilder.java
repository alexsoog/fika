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

import java.util.List;

import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.Paragraph;
import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.ContainerBlockBuilder;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * @author leadpony
 */
abstract class AbstractListBuilder extends ContainerBlockBuilder {

    private int childCount;
    private boolean loose;
    private int lastBlankLineNo;
    
    protected AbstractListBuilder() {
        this.childCount = 0;
        this.loose = false;
        this.lastBlankLineNo = -1;
    }
    
    boolean isLoose() {
        return loose;
    }

    @Override
    public Result processLine(InputSequence input) {
        if (input.isBlank()) {
            this.lastBlankLineNo = lineCount() + 1;
        }
        return Result.CONTINUED;
    }
    
    @Override
    public void openChildBuilder(BlockBuilder childBuilder) {
        if (this.childCount > 0 && lineCount() == this.lastBlankLineNo) {
            this.loose = true;
        }
        super.openChildBuilder(childBuilder);
        this.childCount++;
    }
    
    @Override
    public void closeChildBuilder(BlockBuilder childBuilder) {
        if (childBuilder instanceof AbstractListItemBuilder) {
            AbstractListItemBuilder itemBuilder = (AbstractListItemBuilder)childBuilder;
            if (itemBuilder.isLoose()) {
                this.loose = true;
            }
        }
        super.closeChildBuilder(childBuilder);
    }
    
    @Override
    protected List<Node> childNodes() {
        return buildChildNodes();
    }
    
    private List<Node> buildChildNodes() {
        List<Node> items = super.childNodes();
        if (!isLoose()) {
            for (Node item: items) {
                tightenListItem(item);
            }
        }
        return items;
    }
    
    private void tightenListItem(Node item) {
        Node child = item.firstChildNode();
        while (child != null) {
            if (child instanceof Paragraph) {
                Node text = child.firstChildNode();
                item.replaceChild(text, child);
                child = text;
            }
            child = child.nextNode();
        }
    }
}
