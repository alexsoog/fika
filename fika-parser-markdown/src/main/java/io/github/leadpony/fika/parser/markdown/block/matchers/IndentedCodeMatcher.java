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

import io.github.leadpony.fika.parser.markdown.block.BlockBuilder;
import io.github.leadpony.fika.parser.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parser.markdown.block.MatcherType;
import io.github.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * @author leadpony
 */
public class IndentedCodeMatcher implements BlockMatcher {

    @Override
    public MatcherType matcherType() {
        return BasicMatcherType.INDENTED_CODE;
    }
 
    @Override
    public BlockBuilder newBuilder(InputSequence input) {
        if (!input.hasLeadingSpaces(IndentedCodeBuilder.INDENT_SIZE)) {
            return null;
        }
        return new IndentedCodeBuilder();
    }
}
