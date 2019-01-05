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
 * A builder for building a parser factory.
 *
 * @author leadpony
 */
public interface ParserFactoryBuilder {

    /**
     * Specifies the feature to add to the factory.
     *
     * @param feature the name of the feature, cannot be {@code null}.
     * @return this builder.
     * @throws NullPointerException if the specified {@code feature} is {@code null}.
     */
    ParserFactoryBuilder withFeature(String feature);

    default ParserFactoryBuilder withFeature(Feature feature) {
        return withFeature(feature.name());
    }

    /**
     * Specifies the feature to remove from the factory.
     *
     * @param feature the name of the feature, cannot be {@code null}.
     * @return this builder.
     * @throws NullPointerException if the specified {@code feature} is {@code null}.
     */
    ParserFactoryBuilder withoutFeature(String feature);

    default ParserFactoryBuilder withoutFeature(Feature feature) {
        return withoutFeature(feature.name());
    }

    /**
     * Builds an instance of parser factory.
     *
     * @return newly created instance of parser factory.
     */
    ParserFactory build();
}
