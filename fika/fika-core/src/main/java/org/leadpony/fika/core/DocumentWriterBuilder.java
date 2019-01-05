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

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

/**
 * A builder interface for building an instance of {@link DocumentWriter}.
 *
 * @author leadpony
 */
public interface DocumentWriterBuilder {

    /**
     * Specifies a property of the writer to build.
     *
     * @param name  the name of the property, cannot be {@code null}.
     * @param value the value of the property, cannot be {@code null}..
     * @return this builder.
     * @throws NullPointerException if the specified {@code name} or {@code value}
     *                              is {@code null}.
     */
    DocumentWriterBuilder withProperty(String name, Object value);

    default DocumentWriterBuilder witCharset(Charset value) {
        return withProperty(DocumentWriter.CHARSET, value);
    }

    default DocumentWriterBuilder withFragmentOnly(boolean value) {
        return withProperty(DocumentWriter.FRAGMENT_ONLY, value);
    }

    default DocumentWriterBuilder withLanguage(Locale value) {
        return withProperty(DocumentWriter.LANGUAGE, value);
    }

    default DocumentWriterBuilder withStylesheets(List<URI> value) {
        return withProperty(DocumentWriter.STYLESHEETS, value);
    }

    default DocumentWriterBuilder withTitle(String value) {
        return withProperty(DocumentWriter.TITLE, value);
    }

    /**
     * Creates an instance of {@code DocumentWriter}.
     *
     * @return newly created instance of {@code DocumentWriter}.
     */
    DocumentWriter build();
}
