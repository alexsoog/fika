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
import io.github.leadpony.fika.parsers.markdown.inline.handlers.CodeSpanHandler;
import io.github.leadpony.fika.parsers.markdown.parser.FeatureProvider;
import io.github.leadpony.fika.parsers.markdown.parser.ParserBuilder;

/**
 * @author leadpony
 */
public class CodeSpan implements FeatureProvider {

    @Override
    public Feature feature() {
        return BasicFeature.CODE_SPAN;
    }

    @Override
    public void provide(ParserBuilder builder) {
        builder.add(new CodeSpanHandler());
    }
}
