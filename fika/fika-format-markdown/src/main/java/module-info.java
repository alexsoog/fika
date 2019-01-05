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

/**
 * Defines the Markdown format module.
 */
module org.leadpony.fika.format.markdown {
    requires org.leadpony.fika.core;
    requires org.leadpony.fika.format.base;

    uses org.leadpony.fika.format.markdown.parser.FeatureProvider;

    provides org.leadpony.fika.core.spi.ParserProvider
        with org.leadpony.fika.format.markdown.parser.MarkdownParserProvider;

    provides org.leadpony.fika.format.markdown.parser.FeatureProvider
        with org.leadpony.fika.format.markdown.parser.features.block.Admonition,
             org.leadpony.fika.format.markdown.parser.features.block.BlockQuote,
             org.leadpony.fika.format.markdown.parser.features.block.CodeBlock,
             org.leadpony.fika.format.markdown.parser.features.block.DefinitionList,
             org.leadpony.fika.format.markdown.parser.features.block.Heading,
             org.leadpony.fika.format.markdown.parser.features.block.HtmlBlock,
             org.leadpony.fika.format.markdown.parser.features.block.LinkDefinition,
             org.leadpony.fika.format.markdown.parser.features.block.List,
             org.leadpony.fika.format.markdown.parser.features.block.Paragraph,
             org.leadpony.fika.format.markdown.parser.features.block.ThematicBreak,
             org.leadpony.fika.format.markdown.parser.features.inline.Autolink,
             org.leadpony.fika.format.markdown.parser.features.inline.CodeSpan,
             org.leadpony.fika.format.markdown.parser.features.inline.Emphasis,
             org.leadpony.fika.format.markdown.parser.features.inline.HardLineBreak,
             org.leadpony.fika.format.markdown.parser.features.inline.Image,
             org.leadpony.fika.format.markdown.parser.features.inline.InlineHtml,
             org.leadpony.fika.format.markdown.parser.features.inline.Link;
}