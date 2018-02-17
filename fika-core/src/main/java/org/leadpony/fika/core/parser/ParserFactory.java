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
package org.leadpony.fika.core.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;

/**
 * Factory of {@link Parser} instances.
 * 
 * @author leadpony
 */
public interface ParserFactory {
    
    /**
     * Creates an instance of this type.
     * 
     * @param language the markup language to parse.
     * @return newly created instance of this type.
     * @throws NullPointerException if specified language is {@code null}.
     */
    static ParserFactory newInstance(MarkupLanguage language) {
        Objects.requireNonNull(language, "language must not be null");
        return newInstance(language.mediaType(), language.variant());
    }

    /**
     * Creates an instance of this type.
     * 
     * @param mediaType the media type of the source to parse.
     * @return newly created instance of this type.
     * @throws NullPointerException if one of parameters is {@code null}.
     */
    static ParserFactory newInstance(String mediaType) {
        return newInstance(mediaType, "");
    }

    /**
     * Creates an instance of this type.
     * 
     * @param mediaType the media type of the source to parse.
     * @param variant the qualifier representing the media type variant.
     * @return newly created instance of this type.
     * @throws NullPointerException if one of parameters is {@code null}.
     */
    static ParserFactory newInstance(String mediaType, String variant) {
        return builder(mediaType, variant).build();
    }
    
    /**
     * Creates an instance the builder of this type.
     * 
     * @param language the markup language to parse.
     * @return newly created instance of the builder.
     * @throws NullPointerException if specified language is {@code null}.
     */
    static ParserFactoryBuilder builder(MarkupLanguage language) {
        Objects.requireNonNull(language, "language must not be null");
        return builder(language.mediaType(), language.variant());
    }
    
    /**
     * Creates an instance the builder of this type.
     * 
     * @param mediaType the media type of the source to parse.
     * @param variant the qualifier representing the media type variant.
     * @return newly created instance of the builder.
     * @throws NullPointerException if one of parameters is {@code null}.
     */
    static ParserFactoryBuilder builder(String mediaType, String variant) {
        Objects.requireNonNull(mediaType, "mediaType must not be null");
        Objects.requireNonNull(variant, "variant must not be null");
        ParserService service = ParserService.findService(mediaType, variant);
        if (service != null) {
            return service.newBuilder(mediaType, variant);
        }
        return null;
    }
    
    /**
     * Creates a parser instance to parse the text given as a string.
     * 
     * @param text the string containing the text to parse.
     * @return newly created instance of {@link Parser}.
     * @throws NullPointerException if specified text is {@code null}.
     */
    default Parser newParser(String text) {
        Objects.requireNonNull(text, "text must not be null");
        return newParser(new StringReader(text));
    }

    /**
     * Creates a parser instance to parse the text which will be 
     * read by the specified reader.
     * 
     * @param reader the reader which will read the text to parse.
     * @return newly created instance of {@link Parser}.
     * @throws NullPointerException if specified reader is {@code null}.
     */
    Parser newParser(Reader reader);
}
