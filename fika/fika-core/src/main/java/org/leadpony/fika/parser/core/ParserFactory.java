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

import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;

/**
 * A factory for producing parser instances.
 *
 * <p>
 * All the methods in this interface are safe for use by multiple concurrent
 * threads.
 * </p>
 *
 * @author leadpony
 */
public interface ParserFactory {

    /**
     * Creates a parser instance for parsing the text which will be read by the
     * specified reader.
     *
     * @param reader the reader which will read the text to parse, cannot be
     *               {@code null}.
     * @return newly created instance of parser.
     * @throws NullPointerException if the specified {@code reader} is {@code null}.
     */
    Parser createParser(Reader reader);

    /**
     * Creates a parser instance for parsing the text specified as a string.
     *
     * @param text the string containing the text to parse, cannot be {@code null}
     * @return newly created instance of parser.
     * @throws NullPointerException if the specified {@code text} is {@code null}.
     */
    default Parser createParser(String text) {
        Objects.requireNonNull(text, "text must not be null.");
        return createParser(new StringReader(text));
    }
}
