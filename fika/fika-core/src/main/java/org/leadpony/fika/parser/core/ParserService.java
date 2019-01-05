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
package org.leadpony.fika.parser.core;

import static java.util.Objects.requireNonNull;

import java.io.Reader;

import org.leadpony.fika.parser.spi.ParserProvider;

/**
 * The facade interface of this API which produces {@link Parser},
 * {@link ParserFactory} and {@link ParserFactoryBuilder}.
 *
 * <p>
 * All the methods in this class are safe for use by multiple concurrent
 * threads. For most use-cases, only one instance of {@code ParserService} is
 * required within the application.
 * </p>
 *
 * @author leadpony
 */
public interface ParserService {

    /**
     * Returns the parser service for the specified media type.
     *
     * @param mediaType the media type of the source to parse, cannot be
     *                  {@code null}.
     * @return found service instance.
     * @throws NullPointerException if the specified {@code mediaType} is
     *                              {@code null}.
     */
    static ParserService get(String mediaType) {
        return get(mediaType, "");
    }

    /**
     * Returns the service instance for the specified media type and variant.
     *
     * @param mediaType the media type of the source to parse, cannot be
     *                  {@code null}.
     * @param variant   the qualifier representing the media type variant, cannot be
     *                  {@code null}.
     * @return found service instance.
     * @throws NullPointerException     if any of parameters is {@code null}.
     * @throws IllegalArgumentException if the service for the specified parameters
     *                                  is not found.
     */
    static ParserService get(String mediaType, String variant) {
        requireNonNull(mediaType, "mediaType");
        requireNonNull(variant, "variant");
        return ParserProvider.provider(mediaType, variant).createService(mediaType, variant);
    }

    /**
     * Creates a new instance of {@code ParserFactoryBuilder}.
     *
     * @return newly created instance of {@code ParserFactoryBuilder}.
     */
    ParserFactoryBuilder createParserFactoryBuilder();

    /**
     * Creates a new instance of {@code ParserFactory} with default configuration.
     *
     * @return newly created instance of {@code ParserFactory}.
     */
    default ParserFactory createParserFactory() {
        return createParserFactoryBuilder().build();
    }

    /**
     * Creates a parser instance for parsing the text which will be read by the
     * specified reader.
     *
     * @param reader the reader which will read the text to parse, cannot be
     *               {@code null}.
     * @return newly created instance of {@code Parser}.
     * @throws NullPointerException if the specified {@code reader} is {@code null}.
     */
    default Parser createParser(Reader reader) {
        return createParserFactory().createParser(reader);
    }

    /**
     * Creates a parser instance for parsing the text specified as a string.
     *
     * @param text the string containing the text to parse, cannot be {@code null}
     * @return newly created instance of {@code Parser}.
     * @throws NullPointerException if the specified {@code text} is {@code null}.
     */
    default Parser createParser(String text) {
        return createParserFactory().createParser(text);
    }
}
