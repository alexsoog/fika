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

import java.util.ServiceLoader;

import org.leadpony.fika.core.DocumentService;

/**
 * A service-provider interface which will provide {@link DocumentService}.
 *
 * @author leadpony
 */
public interface DocumentServiceProvider {

    /**
     * Finds and returns the provider for the specified media type.
     *
     * @param mediaType the media type of the source to parse, cannot be
     *                  {@code null}.
     * @return found provider.
     * @throws NullPointerException     if the specified {@code mediaType} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if provider for {@code mediaType} does not
     *                                  exist.
     */
    static DocumentServiceProvider provider(String mediaType) {
        ServiceLoader<DocumentServiceProvider> loader = ServiceLoader.load(DocumentServiceProvider.class);
        for (DocumentServiceProvider provider : loader) {
            if (provider.supports(mediaType)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("There is no service provider for \"" + mediaType + "\".");
    }

    /**
     * Checks whether this provider supports the specified media type or not.
     *
     * @param mediaType the media type of the source to parse, cannot be
     *                  {@code null}.
     * @return {@code true} if this provider supports the specified media type,
     *         {@code false} otherwise.
     * @throws NullPointerException if the specified {@code mediaType} is
     *                              {@code null}.
     */
    boolean supports(String mediaType);

    /**
     * Creates a new instance of document service best suited to the specified media
     * type.
     *
     * @param mediaType the media type of the source to parse, cannot be
     *                  {@code null}.
     * @return newly created instance of {@code DocumentService}.
     * @throws NullPointerException     if the specified {@code mediaType} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if this provider does not support the
     *                                  specified {@code mediaType}.
     */
    DocumentService createService(String mediaType);
}
