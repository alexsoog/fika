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
package io.github.leadpony.fika.parsers.markdown.parser;

import java.io.Reader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.leadpony.fika.core.model.NodeFactory;
import io.github.leadpony.fika.core.parser.Parser;
import io.github.leadpony.fika.core.parser.ParserFactory;
import io.github.leadpony.fika.core.parser.ParserFactoryBuilder;
import io.github.leadpony.fika.core.parser.support.model.DefaultNodeFactory;

/**
 * Factory of markdown parsers.
 * 
 * @author leadpony
 */
class MarkdownParserFactory implements ParserFactory {
    
    private final ProviderRegistry providers;
    private final Set<FeatureProvider> featureSet;
    private final NodeFactory nodeFactory;
    
    MarkdownParserFactory(Builder builder) {
        this.providers = builder.providers;
        this.featureSet = builder.activeFeatureSet;
        this.nodeFactory = new DefaultNodeFactory();
    }

    @Override
    public Parser newParser(Reader reader) {
        if (reader == null) {
            throw new NullPointerException("reader must not be null");
        }
        return new MarkdownParser(reader, this.nodeFactory, this.providers, this.featureSet);
    }
    
    /**
     * Builder of {@link MarkdownParserFactory}.
     * 
     * @author leadpony
     */
    static class Builder implements ParserFactoryBuilder {
        
        private final Map<String, FeatureProvider> featureMap;
        private final Set<FeatureProvider> activeFeatureSet;
        private ProviderRegistry providers;
        
        Builder(Map<String, FeatureProvider> featureMap) {
            this.featureMap = featureMap;
            this.activeFeatureSet = defaultizeFeatureSet(featureMap);
            this.providers = ProviderRegistry.getDefault();
        }

        @Override
        public ParserFactoryBuilder withFeature(String feature) {
            FeatureProvider found = featureMap.get(feature);
            if (found != null) {
                return this;
            }
            activeFeatureSet.add(found);
            return this;
        }

        @Override
        public ParserFactoryBuilder withoutFeature(String feature) {
            FeatureProvider found = featureMap.get(feature);
            if (found != null) {
                return this;
            }
            activeFeatureSet.remove(found);
            return this;
        }

        @Override
        public ParserFactory build() {
            return new MarkdownParserFactory(this);
        }
        
        protected Set<FeatureProvider> defaultizeFeatureSet(Map<String, FeatureProvider> featureMap) {
            Set<FeatureProvider> featureSet = new HashSet<>();
            featureSet.addAll(featureMap.values());
            return featureSet;
        }
    }
}
