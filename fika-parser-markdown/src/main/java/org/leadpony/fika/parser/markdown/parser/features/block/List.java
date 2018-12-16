/*
 * Copyright 2017-2018 the Fika authors.
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
package org.leadpony.fika.parser.markdown.parser.features.block;

import org.leadpony.fika.core.parser.BasicFeature;
import org.leadpony.fika.core.parser.Feature;
import org.leadpony.fika.parser.markdown.block.commonmark.BulletListMatcher;
import org.leadpony.fika.parser.markdown.block.commonmark.OrderedListMatcher;
import org.leadpony.fika.parser.markdown.parser.FeatureProvider;
import org.leadpony.fika.parser.markdown.parser.ParserBuilder;

/**
 * @author leadpony
 */
public class List implements FeatureProvider {

    @Override
    public Feature feature() {
        return BasicFeature.LIST;
    }

    @Override
    public void provide(ParserBuilder builder) {
        builder.add(new BulletListMatcher());
        builder.add(new OrderedListMatcher());
    }
}
