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

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * SPI (Service Provider Interface) which will provide the parsing service.
 * 
 * @author leadpony
 */
public abstract class ParserService {

    private static final ThreadLocal<ServiceLoader<ParserService>> loaders = 
            ThreadLocal.withInitial(ParserService::loader);

    /**
     * Searches for the appropriate service.
     * 
     * @param language the markup language to parse, cannot be {@code null}.
     * @return the service found, or {@code null} if not found.
     */
    static ParserService findService(MarkupLanguage language) {
        Iterator<ParserService> it = loaders.get().iterator();
        while (it.hasNext()) {
            ParserService service = it.next();
            if (service.supports(language)) {
                return service;
            }
        }
        return null;
    }
    
    private static ServiceLoader<ParserService> loader() {
        return ServiceLoader.load(ParserService.class);
    }
   
    /**
     * Checks if this service supports specified markup language.
     * 
     * @param language the markup language to check, must not be {@code null}.
     * @return {@code true} if this service supports the specified language, otherwise {@code false}.
     */
    public abstract boolean supports(MarkupLanguage language);

    /**
     * Creates new instance of builder of parser factory.
     * 
     * @param language the markup language to parse.
     * @return newly created builder.
     */
    public abstract ParserFactoryBuilder newBuilder(MarkupLanguage language);
}