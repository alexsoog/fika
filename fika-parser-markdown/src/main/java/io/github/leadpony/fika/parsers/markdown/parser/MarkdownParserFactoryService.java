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

import io.github.leadpony.fika.core.parser.ParserFactory;
import io.github.leadpony.fika.core.parser.ParserFactoryService;

/**
 * @author leadpony
 *
 */
public class MarkdownParserFactoryService extends ParserFactoryService {
    
    private static final String TARGET_MEDIA_TYPE = "text/markdown";

    @Override
    public boolean supports(String mediaType) {
        return TARGET_MEDIA_TYPE.equals(mediaType);
    }

    @Override
    public ParserFactory newFactory(String mediaType) {
        ParserFactory factory = null;
        if (TARGET_MEDIA_TYPE.equals(mediaType)) {
            factory = new MarkdownParserFactory();
        }
        return factory;
    }
}
