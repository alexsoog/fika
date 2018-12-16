/*
 * Copyright 2017-2018 the Fika authors.
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
package org.leadpony.fika.parser.markdown.parser;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.leadpony.fika.core.parser.BasicFeature;
import org.leadpony.fika.core.parser.Feature;
import org.leadpony.fika.core.parser.ParserFactoryBuilder;
import org.leadpony.fika.core.parser.ParserService;

/**
 * The implementation of {@link ParserService}.
 * 
 * @author leadpony
 */
public class MarkdownParserService extends ParserService {
    
    private static final String MEDIA_TYPE = "text/markdown";
 
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
    
    public MarkdownParserService() {
        this.allFeatures = FeatureProvider.features();
    }

    @Override
    public boolean supports(String mediaType, String variant) {
        Objects.requireNonNull(mediaType, "mediaType must not be null");
        Objects.requireNonNull(variant, "variant must not be null");
        return mediaType.equals(MEDIA_TYPE) && variant.isEmpty();
    }

    @Override
    public ParserFactoryBuilder newBuilder(String mediaType, String variant) {
        Objects.requireNonNull(mediaType, "mediaType must not be null");
        Objects.requireNonNull(variant, "variant must not be null");
        if (mediaType.equals(MEDIA_TYPE)) {
            if (variant.isEmpty()) {
                return new MarkdownParserFactory.Builder(this.allFeatures, BASE_FEATURE_SET);
            }
        }
        return null;
    }
}
