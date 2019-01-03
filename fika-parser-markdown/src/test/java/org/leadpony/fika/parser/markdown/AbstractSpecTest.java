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
package org.leadpony.fika.parser.markdown;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.leadpony.fika.parser.core.MarkupLanguage;
import org.leadpony.fika.parser.core.Parser;
import org.leadpony.fika.parser.core.ParserFactory;
import org.leadpony.fika.parser.model.Document;
import org.leadpony.fika.parser.renderer.HtmlRenderer;

/**
 * @author leadpony
 */
public abstract class AbstractSpecTest {
   
    private static final ParserFactory factory = 
            ParserFactory.newInstance(MarkupLanguage.MARKDOWN);

    protected AbstractSpecTest() {
    }
    
    @ParameterizedTest
    @MethodSource("provideFixtures")
    public void test(Fixture fixture) {
        //Assumptions.assumeTrue(fixture.index() == 51);
        Parser parser = getParserFactory().newParser(fixture.source());
        Document doc = parser.parse();
        HtmlRenderer renderer = HtmlRenderer.builder()
                .withOption(HtmlRenderer.Option.HTML_FRAGMENT)
                .build();
        String actual = renderer.render(doc);
        String expected = fixture.expected();
        HtmlAssert.assertThat(actual).isEqualTo(expected);
    }
    
    protected ParserFactory getParserFactory() {
        return factory;
    }
}
