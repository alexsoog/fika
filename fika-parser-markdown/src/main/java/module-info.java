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
 * Defines the Markdown parser module.
 */
module org.leadpony.fika.parser.markdown {
    requires org.leadpony.fika.parser;

    uses org.leadpony.fika.parser.markdown.parser.FeatureProvider;

    provides org.leadpony.fika.parser.spi.ParserFactoryProvider
        with org.leadpony.fika.parser.markdown.parser.MarkdownParserFactoryProvider;

    provides org.leadpony.fika.parser.markdown.parser.FeatureProvider
        with org.leadpony.fika.parser.markdown.parser.features.block.Admonition,
             org.leadpony.fika.parser.markdown.parser.features.block.BlockQuote,
             org.leadpony.fika.parser.markdown.parser.features.block.CodeBlock,
             org.leadpony.fika.parser.markdown.parser.features.block.DefinitionList,
             org.leadpony.fika.parser.markdown.parser.features.block.Heading,
             org.leadpony.fika.parser.markdown.parser.features.block.HtmlBlock,
             org.leadpony.fika.parser.markdown.parser.features.block.LinkDefinition,
             org.leadpony.fika.parser.markdown.parser.features.block.List,
             org.leadpony.fika.parser.markdown.parser.features.block.Paragraph,
             org.leadpony.fika.parser.markdown.parser.features.block.ThematicBreak,
             org.leadpony.fika.parser.markdown.parser.features.inline.Autolink,
             org.leadpony.fika.parser.markdown.parser.features.inline.CodeSpan,
             org.leadpony.fika.parser.markdown.parser.features.inline.Emphasis,
             org.leadpony.fika.parser.markdown.parser.features.inline.HardLineBreak,
             org.leadpony.fika.parser.markdown.parser.features.inline.Image,
             org.leadpony.fika.parser.markdown.parser.features.inline.InlineHtml,
             org.leadpony.fika.parser.markdown.parser.features.inline.Link;
}