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
package io.github.leadpony.fika.core.parser;

import java.io.Reader;
import java.io.StringReader;

/**
 * @author leadpony
 *
 */
public interface ParserFactory {
    
    static ParserFactory newInstance(String mediaType) {
        ParserFactoryService service = ParserFactoryService.findService(mediaType);
        if (service != null) {
            return service.newFactory(mediaType);
        }
        return null;
    }
    
    default Parser newParser(String text) {
        return newParser(new StringReader(text));
    }

    Parser newParser(Reader reader);
}
