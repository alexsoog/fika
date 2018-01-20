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

import java.util.Map;

import io.github.leadpony.fika.core.parser.MarkupLanguage;
import io.github.leadpony.fika.core.parser.ParserFactoryBuilder;
import io.github.leadpony.fika.core.parser.ParserService;

/**
 * The implementation of {@link ParserService}.
 * 
 * @author leadpony
 */
public class MarkdownParserService extends ParserService {
    
    private final Map<String, FeatureProvider> featureMap;
    
    public MarkdownParserService() {
        this.featureMap = FeatureProvider.features();
    }

    @Override
    public boolean supports(MarkupLanguage language) {
        return MarkupLanguage.MARKDOWN.equals(language);
    }

    @Override
    public ParserFactoryBuilder newBuilder(MarkupLanguage language) {
        if (!supports(language)) {
            return null;
        }
        return new MarkdownParserFactory.Builder(featureMap);
    }
}
