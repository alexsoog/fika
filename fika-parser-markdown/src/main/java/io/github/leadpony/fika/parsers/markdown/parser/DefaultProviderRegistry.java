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
package io.github.leadpony.fika.parsers.markdown.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.BlockQuoteMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.FencedCodeMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.AtxHeadingMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.HtmlBlockMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.IndentedCodeMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.LinkDefinitionMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.ListMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.ParagraphMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.SetextHeadingMatcherProvider;
import io.github.leadpony.fika.parsers.markdown.block.matchers.ThematicBreakMatcherProvider;

/**
 * @author leadpony
 *
 */
class DefaultProviderRegistry implements ProviderRegistry {

    private final List<BlockMatcherProvider> blockProviders;
    
    private static final ProviderRegistry defaultRegistry = new DefaultProviderRegistry();
    
    static ProviderRegistry get() {
        return defaultRegistry;
    }
    
    private DefaultProviderRegistry() {
        this.blockProviders = populateBlockProviders();
    }

    @Override
    public Iterable<BlockMatcherProvider> blockMatcherProviders() {
        return blockProviders;
    }

    private List<BlockMatcherProvider> populateBlockProviders() {
        List<BlockMatcherProvider> providers = new ArrayList<>();

        providers.add(new AtxHeadingMatcherProvider());
        providers.add(new BlockQuoteMatcherProvider());
        providers.add(new FencedCodeMatcherProvider());
        providers.add(new HtmlBlockMatcherProvider());
        providers.add(new IndentedCodeMatcherProvider());
        providers.add(new LinkDefinitionMatcherProvider());
        providers.add(new ListMatcherProvider());
        providers.add(new ParagraphMatcherProvider());
        providers.add(new SetextHeadingMatcherProvider());
        providers.add(new ThematicBreakMatcherProvider());

        return Collections.unmodifiableList(providers);
    }
}
