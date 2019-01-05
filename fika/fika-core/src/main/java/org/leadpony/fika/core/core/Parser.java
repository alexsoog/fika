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
package org.leadpony.fika.core.core;

import java.io.Closeable;

import org.leadpony.fika.core.model.Document;

/**
 * A parser for parsing the source text.
 *
 * <p>
 * Note that methods provided by this interface are not safe for use by multiple
 * concurrent threads.
 * </p>
 *
 * @author leadpony
 */
public interface Parser extends Closeable {

    /**
     * Closes this parser and frees any resources associated with the parser. This
     * method closes the underlying input source.
     *
     * @throws ParsingException if an I/O error occurs.
     */
    @Override
    public void close();

    /**
     * Parses the input source and returns the parsed document.
     *
     * @return the parsed document, never be {@code null}.
     * @throws ParsingException      if an error occurs while parsing.
     * @throws IllegalStateException if {@link #parse()} or {@link #close()} method
     *                               is already called.
     */
    Document parse();
}
