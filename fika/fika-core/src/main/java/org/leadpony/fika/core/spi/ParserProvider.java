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
package org.leadpony.fika.core.spi;

import static java.util.Objects.requireNonNull;

import java.util.ServiceLoader;

import org.leadpony.fika.core.core.ParserService;

/**
 * A service-provider interface which will provide the parser service.
 *
 * @author leadpony
 */
public interface ParserProvider {

    /**
     * Finds and returns the provider for the specified media type.
     *
     * @param mediaType the media type of the source to parse, cannot be
     *                  {@code null}.
     * @param variant   the qualifier representing the media type variant, cannot be
     *                  {@code null}.
     * @return found provider.
     * @throws NullPointerException     if any of the specified parameters is
     *                                  {@code null}.
     * @throws IllegalArgumentException if the requested provider is not found.
     */
    static ParserProvider provider(String mediaType, String variant) {
        requireNonNull(mediaType, "mediaType must not be null.");
        requireNonNull(variant, "variant must not be null.");
        ServiceLoader<ParserProvider> loader = ServiceLoader.load(ParserProvider.class);
        for (ParserProvider provider : loader) {
            if (provider.supports(mediaType, variant)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("ParserProvider was not found.");
    }

    /**
     * Checks whether this provider supports the specified media type or not.
     *
     * @param mediaType the media type of the source to parse, cannot be
     *                  {@code null}.
     * @param variant   the qualifier representing the media type variant, cannot be
     *                  {@code null}.
     * @return {@code true} if this provider supports the specified media type,
     *         otherwise {@code false}.
     * @throws NullPointerException if any of the specified parameters is
     *                              {@code null}.
     */
    boolean supports(String mediaType, String variant);

    /**
     * Creates a new instance of parser service best suited to the specified media
     * type.
     *
     * @param mediaType the media type of the source to parse, cannot be
     *                  {@code null}.
     * @param variant   the qualifier representing the media type variant, cannot be
     *                  {@code null}.
     * @return newly created instance of {@code ParserService}.
     * @throws NullPointerException     if any of the specified parameters is
     *                                  {@code null}.
     * @throws IllegalArgumentException if this provider does not support the
     *                                  specified media type.
     */
    ParserService createService(String mediaType, String variant);
}
