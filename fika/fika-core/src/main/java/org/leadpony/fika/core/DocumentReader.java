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

import java.io.Closeable;

import org.leadpony.fika.core.model.Document;

/**
 * A reader interface for reading a document.
 *
 * <p>
 * Note that methods defined in this interface are NOT safe for use by multiple
 * concurrent threads.
 * </p>
 *
 * @author leadpony
 */
public interface DocumentReader extends Closeable {

    /**
     * Reads a document from the input source.
     *
     * @return the document node read.
     * @throws IllegalStateException if {@link #read()} or {@link #close()} method
     *                               is already called.
     * @throws DocumentException if an I/O error occurs while reading.
     */
    Document read();

    /**
     * Closes this reader and releases any resources associated with it.
     *
     * @throws DocumentException if an I/O error occurs while closing.
     */
    @Override
    void close();
}
