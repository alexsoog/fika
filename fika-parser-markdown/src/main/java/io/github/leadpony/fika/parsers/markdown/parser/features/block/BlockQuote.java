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
package io.github.leadpony.fika.parsers.markdown.parser.features.block;

import io.github.leadpony.fika.core.parser.BasicFeature;
import io.github.leadpony.fika.core.parser.Feature;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherRegistry;
import io.github.leadpony.fika.parsers.markdown.block.matchers.BlockQuoteMatcher;
import io.github.leadpony.fika.parsers.markdown.inline.InlineHandlerRegistry;
import io.github.leadpony.fika.parsers.markdown.parser.FeatureProvider;

/**
 * @author leadpony
 */
public class BlockQuote implements FeatureProvider {

    @Override
    public Feature feature() {
        return BasicFeature.BLOCK_QUOTE;
    }

    @Override
    public void install(BlockMatcherRegistry blockRegistry, InlineHandlerRegistry inlineRegistry) {
        blockRegistry.installBlockMatcher(new BlockQuoteMatcher());
    }
}
