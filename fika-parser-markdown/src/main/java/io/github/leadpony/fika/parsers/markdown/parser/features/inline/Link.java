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
package io.github.leadpony.fika.parsers.markdown.parser.features.inline;

import io.github.leadpony.fika.core.parser.BasicFeature;
import io.github.leadpony.fika.core.parser.Feature;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.common.ComponentSet;
import io.github.leadpony.fika.parsers.markdown.inline.InlineHandler;
import io.github.leadpony.fika.parsers.markdown.inline.handlers.LinkCloserHandler;
import io.github.leadpony.fika.parsers.markdown.inline.handlers.LinkHandler;
import io.github.leadpony.fika.parsers.markdown.parser.FeatureProvider;

/**
 * @author leadpony
 */
public class Link implements FeatureProvider {

    @Override
    public Feature feature() {
        return BasicFeature.LINK;
    }

    @Override
    public void install(ComponentSet<BlockMatcher> matchers, ComponentSet<InlineHandler> handlers) {
        handlers.add(new LinkHandler());
        if (!handlers.containsTypeOf(LinkCloserHandler.class)) {
            handlers.add(new LinkCloserHandler());
        }
    }
}
