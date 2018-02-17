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
package io.github.leadpony.fika.parser.markdown.block.matchers;

import static io.github.leadpony.fika.parser.markdown.common.Strings.capitalize;

import io.github.leadpony.fika.core.model.Admonition;
import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.parser.markdown.block.ContainerBlockBuilder;
import io.github.leadpony.fika.parser.markdown.block.MatcherType;
import io.github.leadpony.fika.parser.markdown.common.InputSequence;

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
    public MatcherType matcherType() {
        return BasicMatcherType.ADMONITION;
    }

    @Override
    public Result match(InputSequence input) {
        if (lineNo() <= 1) {
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
            return matchLazyContinuationLine(input);
        }
    }
    
    @Override
    protected Block buildBlock() {
        Admonition block = getNodeFactory().newAdmonition(this.type);
        block.setTitle(this.title);
        return block;
    }
}
