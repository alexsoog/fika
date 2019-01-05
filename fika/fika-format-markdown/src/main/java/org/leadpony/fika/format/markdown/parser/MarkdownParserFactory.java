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
package org.leadpony.fika.format.markdown.parser;

import static java.util.Objects.requireNonNull;

import java.io.Reader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.leadpony.fika.parser.core.Feature;
import org.leadpony.fika.parser.core.Parser;
import org.leadpony.fika.parser.core.ParserFactory;
import org.leadpony.fika.parser.core.ParserFactoryBuilder;
import org.leadpony.fika.parser.model.NodeFactory;
import org.leadpony.fika.format.base.model.DefaultNodeFactory;

/**
 * Factory of markdown parsers.
 *
 * @author leadpony
 */
class MarkdownParserFactory implements ParserFactory {

    private final Set<FeatureProvider> featureSet;
    private final NodeFactory nodeFactory;

    MarkdownParserFactory(Builder builder) {
        this.featureSet = builder.activeFeatureSet;
        this.nodeFactory = new DefaultNodeFactory();
    }

    @Override
    public Parser createParser(Reader reader) {
        requireNonNull(reader, "reader must not be null.");
        return new MarkdownParser(reader, this.nodeFactory, this.featureSet);
    }

    /**
     * Builder of {@link MarkdownParserFactory}.
     *
     * @author leadpony
     */
    static class Builder implements ParserFactoryBuilder {

        private final Map<String, FeatureProvider> availableFeatures;
        private final Set<FeatureProvider> activeFeatureSet;

        Builder(Map<String, FeatureProvider> availableFeatures, Set<Feature> defaultFeatureSet) {
            this.availableFeatures = availableFeatures;
            this.activeFeatureSet = defaultizeFeatureSet(availableFeatures, defaultFeatureSet);
        }

        @Override
        public ParserFactoryBuilder withFeature(String feature) {
            FeatureProvider found = availableFeatures.get(feature);
            if (found == null) {
                return this;
            }
            activeFeatureSet.add(found);
            return this;
        }

        @Override
        public ParserFactoryBuilder withoutFeature(String feature) {
            FeatureProvider found = availableFeatures.get(feature);
            if (found == null) {
                return this;
            }
            activeFeatureSet.remove(found);
            return this;
        }

        @Override
        public ParserFactory build() {
            return new MarkdownParserFactory(this);
        }

        protected Set<FeatureProvider> defaultizeFeatureSet(
                Map<String, FeatureProvider> availableFeature,
                Set<Feature> defaultSet) {
            Set<FeatureProvider> featureSet = new HashSet<>();
            for (Feature feature : defaultSet) {
                featureSet.add(availableFeature.get(feature.name()));
            }
            return featureSet;
        }
    }
}
