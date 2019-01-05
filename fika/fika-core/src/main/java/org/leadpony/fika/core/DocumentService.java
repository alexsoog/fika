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

package org.leadpony.fika.core;

import static java.util.Objects.requireNonNull;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import org.leadpony.fika.core.spi.DocumentServiceProvider;

/**
 * The facade interface of the API for creating {@link DocumentReader} and
 * {@link DocumentWriter}.
 *
 * <p>
 * All the methods in this class are safe for use by multiple concurrent
 * threads. For most use-cases, only one instance of {@code DocumentService} is
 * required within the application.
 * </p>
 *
 * @author leadpony
 */
public interface DocumentService {

    /**
     * Returns the document service suited for the specified media type.
     *
     * @param mediaType the media type representing the document format, cannot be
     *                  {@code null}.
     * @return found service instance.
     * @throws NullPointerException     if the specified {@code mediaType} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if there is no document service for the
     *                                  specified {@code mediaType}.
     */
    static DocumentService forType(String mediaType) {
        requireNonNull(mediaType, "mediaType must not be null.");
        DocumentServiceProvider provider = DocumentServiceProvider.provider(mediaType);
        return provider.createService(mediaType);
    }

    /**
     * Creates a document reader.
     *
     * @param reader the reader from which the document will be read.
     * @return newly created instance of {@code DocumentReader}.
     * @throws NullPointerException          if the specified {@code reader} is
     *                                       {@code null}.
     * @throws UnsupportedOperationException if this service does not support
     *                                       reading operation.
     */
    default DocumentReader createReader(Reader reader) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a document reader which will read the document from the specified
     * text.
     *
     * @param text the source text from which the document will be read.
     * @return newly created instance of {@code DocumentReader}.
     * @throws NullPointerException          if the specified {@code text} is
     *                                       {@code null}.
     * @throws UnsupportedOperationException if this service does not support
     *                                       reading operation.
     */
    default DocumentReader createReader(String text) {
        requireNonNull(text, "text must not be null.");
        return createReader(new StringReader(text));
    }

    /**
     * Creates a document writer.
     *
     * @param writer the writer to which the document will be written.
     * @return newly created instance of {@code DocumentWriter}.
     * @throws NullPointerException          if the specified {@code writer} is
     *                                       {@code null}.
     * @throws UnsupportedOperationException if this service does not support
     *                                       writing operation.
     */
    default DocumentWriter createWriter(Writer writer) {
        return createWriterBuilder(writer).build();
    }

    /**
     * Creates a builder of a document writer.
     *
     * @param writer the writer to which the document will be written.
     * @return newly created instance of {@code DocumentWriterBuilder}.
     * @throws NullPointerException          if the specified {@code writer} is
     *                                       {@code null}.
     * @throws UnsupportedOperationException if this service does not support
     *                                       writing operation.
     */
    default DocumentWriterBuilder createWriterBuilder(Writer writer) {
        throw new UnsupportedOperationException();
    }
}
