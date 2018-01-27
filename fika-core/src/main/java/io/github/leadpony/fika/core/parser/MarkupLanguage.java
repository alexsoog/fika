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
package io.github.leadpony.fika.core.parser;

/**
 * Markup language.
 * 
 * @author leadpony
 */
public enum MarkupLanguage {
    /**
     * Markdown (CommonMark).
     */
    MARKDOWN("text/markdown")
    ;

    private final String mediaType;
    private final String variant;
    
    private MarkupLanguage(String mediaType) {
        this(mediaType, "");
    }
    
    private MarkupLanguage(String mediaType, String variant) {
        this.mediaType = mediaType;
        this.variant = variant;
    }
    
    /**
     * Returns the media type of this markup language.
     * 
     * @return the media type.
     */
    public String mediaType() {
        return mediaType;
    }
    
    /**
     * Return the variant identifier of this markup language.
     * 
     * @return the identifier of the variant.
     */
    public String variant() {
        return variant;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(mediaType());
        String variant = variant();
        if (!variant.isEmpty()) {
            builder.append(";variant=").append(variant);
        }
        return builder.toString();
    }
}
