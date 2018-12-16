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
package org.leadpony.fika.core.parser;

/**
 * Builder of parser factory.
 * 
 * @author leadpony
 */
public interface ParserFactoryBuilder {

    /**
     * Provides the factory with the specified feature.
     * 
     * @param feature the name of the feature.
     * @return this builder.
     */
    ParserFactoryBuilder withFeature(String feature);

    /**
     * Provides the factory with the specified feature.
     * 
     * @param feature the feature.
     * @return this builder.
     */
    default ParserFactoryBuilder withFeature(Feature feature) {
        return withFeature(feature.name());
    }
    
    ParserFactoryBuilder withoutFeature(String feature);

    default ParserFactoryBuilder withoutFeature(Feature feature) {
        return withoutFeature(feature.name());
    }
    
    /**
     * Builds an instance of {@link ParserFactory}.
     * 
     * @return newly created instance of {@link ParserFactory}.
     */
    ParserFactory build();
}
