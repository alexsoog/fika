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
package org.leadpony.fika.format.markdown.parser;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.leadpony.fika.parser.core.BasicFeature;
import org.leadpony.fika.parser.core.Feature;
import org.leadpony.fika.parser.core.ParserFactoryBuilder;
import org.leadpony.fika.parser.core.ParserService;

/**
 * The implementation of {@link ParserService}.
 *
 * @author leadpony
 */
class MarkdownParserService implements ParserService {

    private final Map<String, FeatureProvider> allFeatures;

    private static final Set<Feature> BASE_FEATURE_SET = Collections.unmodifiableSet(
            EnumSet.of(
                BasicFeature.AUTO_LINK,
                BasicFeature.BLOCK_QUOTE,
                BasicFeature.CODE_BLOCK,
                BasicFeature.CODE_SPAN,
                BasicFeature.EMPHASIS,
                BasicFeature.HARD_LINE_BREAK,
                BasicFeature.HEADING,
                BasicFeature.HTML_BLOCK,
                BasicFeature.IMAGE,
                BasicFeature.INLINE_HTML,
                BasicFeature.LINK,
                BasicFeature.LINK_DEFINITION,
                BasicFeature.LIST,
                BasicFeature.PARAGRAPH,
                BasicFeature.THEMATIC_BREAK
            ));

    MarkdownParserService() {
        this.allFeatures = FeatureProvider.features();
    }

    @Override
    public ParserFactoryBuilder createParserFactoryBuilder() {
        return new MarkdownParserFactory.Builder(this.allFeatures, BASE_FEATURE_SET);
    }
}
