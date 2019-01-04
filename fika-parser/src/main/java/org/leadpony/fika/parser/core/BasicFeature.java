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

/**
 * Defines various kinds of features provided by markup languages.
 *
 * @author leadpony
 */
public enum BasicFeature implements Feature {
    /** Admonition. */
    ADMONITION,
    /** Autolink */
    AUTO_LINK,
    /** Block quote. */
    BLOCK_QUOTE,
    /** Code block */
    CODE_BLOCK,
    /** Code span. */
    CODE_SPAN,
    /** Definition list. */
    DEFINITION_LIST,
    /** Emphasis. */
    EMPHASIS,
    /** Hard line break. */
    HARD_LINE_BREAK,
    /** Heading. */
    HEADING,
    /** HTML block */
    HTML_BLOCK,
    /** Image. */
    IMAGE,
    /** Inline HTML. */
    INLINE_HTML,
    /** Link */
    LINK,
    /** Link reference definition. */
    LINK_DEFINITION,
    /** List. */
    LIST,
    /** Paragraph. */
    PARAGRAPH,
    /** Soft line break. */
    SOFT_LINE_BREAK,
    /** Thematic break. */
    THEMATIC_BREAK
    ;
}
