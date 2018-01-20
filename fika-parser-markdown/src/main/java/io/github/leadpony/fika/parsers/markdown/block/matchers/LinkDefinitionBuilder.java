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

import java.util.function.Consumer;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.parsers.markdown.block.MatcherType;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;
import io.github.leadpony.fika.parsers.markdown.common.LinkDefinition;

/**
 * @author leadpony
 */
class LinkDefinitionBuilder extends AbstractParagraphBuilder implements Consumer<LinkDefinition> {
    
    private final LinkDefinitionRecognizer recognizer = new LinkDefinitionRecognizer(this);
   
    @Override
    public MatcherType matcherType() {
        return BasicMatcherType.LINK_DEFINITION;
    }

    @Override
    public Result match(InputSequence input) {
        if (lineNo() > 1 && input.isBlank()) {
            return Result.COMPLETED;
        }
        appendLine(input);
        if (recognizer.acceptLine(input)) {
            return Result.COMPLETED;
        }
        return Result.CONTINUED;
    }
    
    @Override
    public boolean isInterruptible() {
        return lineNo() > 1;
    }
   
    @Override
    protected Block buildBlock() {
        int linesConsumed = recognizer.flush();
        return buildParagraph(linesConsumed);
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
