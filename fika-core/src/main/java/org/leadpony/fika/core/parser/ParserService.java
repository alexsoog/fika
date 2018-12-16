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
package org.leadpony.fika.core.parser;

import java.util.Iterator;
import java.util.Objects;
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
     * @param mediaType the media type of the source to parse, cannot be {@code null}.
     * @param variant the variant of the media type, cannot be {@code null}.
     * @return the service found, or {@code null} if not found.
     */
    static ParserService findService(String mediaType, String variant) {
        Objects.requireNonNull(mediaType, "media must not be null");
        Objects.requireNonNull(mediaType, "variant must not be null");
        Iterator<ParserService> it = loaders.get().iterator();
        while (it.hasNext()) {
            ParserService service = it.next();
            if (service.supports(mediaType, variant)) {
                return service;
            }
        }
        return null;
    }
    
    private static ServiceLoader<ParserService> loader() {
        return ServiceLoader.load(ParserService.class);
    }
   
    /**
     * Checks if this service supports specified media type.
     * 
     * @param mediaType the media type of the source to parse, cannot be {@code null}.
     * @param variant the variant of the media type, cannot be {@code null}.
     * @return {@code true} if this service supports the specified media type, otherwise {@code false}.
     * @throws NullPointerException if one of given parameters is {@code null}.
     */
    public abstract boolean supports(String mediaType, String variant);

    /**
     * Creates new instance of builder of parser factory.
     * 
     * @param mediaType the media type of the source to parse, cannot be {@code null}.
     * @param variant the variant of the media type, cannot be {@code null}.
     * @return newly created builder.
     * @throws NullPointerException if one of given parameters is {@code null}.
     */
    public abstract ParserFactoryBuilder newBuilder(String mediaType, String variant);
}
