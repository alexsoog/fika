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

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.parsers.markdown.block.BlockBuilder;
import io.github.leadpony.fika.parsers.markdown.block.MatcherType;
import io.github.leadpony.fika.parsers.markdown.block.BuilderMode;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Builder of paragraphs.
 * 
 * @author leadpony
 */
class ParagraphBuilder extends AbstractParagraphBuilder {
    
    private boolean canceled;
    
    ParagraphBuilder() {
        this.canceled = false;
    }
  
    @Override
    public MatcherType matcherType() {
        return BasicMatcherType.PARAGRAPH;
    }
    
    @Override
    public Result match(InputSequence input) {
        if (lineNo() <= 1) {
            appendLine(input);
            return Result.CONTINUED;
        } else if (input.isBlank()) {
            return Result.COMPLETED;
        } else {
            appendLine(input);
            return Result.CONTINUED;
        }
    }

    @Override
    public boolean isInterruptible() {
        return lineNo() > 1;
    }
    
    @Override
    public BlockBuilder interrupt(InputSequence input, BuilderMode mode) {
        BlockBuilder interrupter = super.interrupt(input, mode);
        if (interrupter instanceof SetextHeadingBuilder) {
            this.canceled = true;
        }
        return interrupter;
    }

    @Override
    public Result continueLazily(InputSequence input) {
        BlockBuilder interrupter = interrupt(input, BuilderMode.LAZY_CONTINUATION);
        if (interrupter != null || input.isBlank()) {
            return Result.NOT_MATCHED;
        }
        return match(input);
    }
    
    @Override
    protected Block buildBlock() {
        if (canceled) {
            return null;
        }
        return buildParagraph(0);
    }
}    
