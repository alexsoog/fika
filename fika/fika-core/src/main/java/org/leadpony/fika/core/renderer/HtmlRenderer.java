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
package org.leadpony.fika.core.renderer;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

/**
 * A renderer interface for rendering a node tree into HTML.
 *
 * @author leadpony
 */
public interface HtmlRenderer extends Renderer {

    /**
     * Returns a builder of a HTML renderer.
     *
     * @return newly created instance of builder.
     */
    static Builder builder() {
        return DefaultHtmlRenderer.builder();
    }

    /**
     * Defines rendering options.
     *
     * @author leadpony
     */
    public static enum Option {
        /**
         * Renders a part of HTML document without a header and a body element.
         */
        HTML_FRAGMENT
    }

    /**
     * A builder interface for building a HTML renderer.
     *
     * @author leadpony
     */
    public static interface Builder {

        /**
         * Specifies the rendering option to enable.
         *
         * @param option the rendering option to enable.
         * @return this builder.
         * @throws NullPointerException if the specified {@code option} is {@code null}.
         * @see HtmlRenderer.Option
         */
        Builder withOption(Option option);

        /**
         * Specifies the rendering option to disable.
         *
         * @param option the rendering option to disable.
         * @return this builder.
         * @throws NullPointerException if the specified {@code option} is {@code null}.
         * @see HtmlRenderer.Option
         */
        Builder withoutOption(Option option);

        /**
         * Specifies the character set of the HTML document.
         *
         * @param charset the character set of the HTML document.
         * @return this builder.
         * @throws NullPointerException if the specified {@code charset} is
         *                              {@code null}.
         */
        Builder withCharset(Charset charset);

        /**
         * Specifies the language of the HTML document.
         *
         * @param language the language of the HTML document.
         * @return this builder.
         * @throws NullPointerException if the specified {@code language} is
         *                              {@code null}.
         */
        Builder withLanguage(Locale language);

        /**
         * Specifies the title of the HTML document.
         *
         * @param title the title of the HTML document.
         * @return this builder.
         * @throws NullPointerException if the specified {@code title} is {@code null}.
         */
        Builder withTitle(String title);

        /**
         * Specifies a location of a stylesheet to apply.
         *
         * @param stylesheet the URI of the stylesheet.
         * @return this builder.
         * @throws NullPointerException if the specified {@code stylesheet} is
         *                              {@code null}.
         */
        Builder withStylesheet(URI stylesheet);

        /**
         * Specifies a list of stylesheets to apply.
         *
         * @param stylesheets the list of the styleseets.
         * @return this builder.
         * @throws NullPointerException if the specified {@code stylesheets} is
         *                              {@code null}.
         */
        Builder withStylesheets(List<URI> stylesheets);

        /**
         * Specifies the formatter to be used for formatting the rendered content.
         *
         * @param formatter the object for formatting the rendered content.
         * @return this builder.
         * @throws NullPointerException if the specified {@code formatter} is
         *                              {@code null}.
         */
        Builder withFormatter(XmlFormatter formatter);

        /**
         * Builds an instance of {@code HtmlRenderer}.
         *
         * @return newly created instance of {@code HtmlRenderer}.
         */
        HtmlRenderer build();
    }
}
