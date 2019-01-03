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
package org.leadpony.fika.parser.renderer;

import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Locale;

import org.leadpony.fika.parser.model.Node;

/**
 * HTML renderer interface.
 * 
 * @author leadpony
 */
public interface HtmlRenderer extends Renderer {
    
    /**
     * Returns a builder of HTML renderer.
     * 
     * @return new instance of builder.
     */
    static Builder builder() {
        return DefaultHtmlRenderer.builder();
    }
    
    void render(Node node, Writer writer);
    
    /**
     * Rendering options.
     * 
     * @author leadpony
     */
    public static enum Option {
        HTML_FRAGMENT
    }

    /**
     * Builder of HTML renderer.
     * 
     * @author leadpony
     */
    public static interface Builder {
        
        Builder withOption(Option option);

        Builder withoutOption(Option option);
        
        Builder withCharset(Charset charset);
        
        Builder withLanguage(Locale language);
        
        Builder withTitle(String title);
        
        Builder withStylesheet(URI stylesheet);
        
        default Builder withStylesheets(Iterable<URI> stylesheets) {
            if (stylesheets == null) {
                throw new NullPointerException("stylesheets must not be null.");
            }
            for (URI stylehsheet: stylesheets) {
                withStylesheet(stylehsheet);
            }
            return this;
        }

        Builder withFormatter(XmlFormatter formatter);

        /**
         * Builds an instance of HtmlRenderer.
         * 
         * @return newly created instance of HtmlRenderer.
         */
        HtmlRenderer build();
    }
}
