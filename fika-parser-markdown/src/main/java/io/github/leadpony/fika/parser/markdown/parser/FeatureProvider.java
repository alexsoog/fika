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
package io.github.leadpony.fika.parser.markdown.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import io.github.leadpony.fika.core.parser.Feature;

/**
 * Feature provider interface.
 * 
 * @author leadpony
 */
public interface FeatureProvider {
    
    /**
     * Returns the feature provided by this provider.
     * 
     * @return the feature provided by this provider.
     */
    Feature feature();
    
    /**
     * Provides the parser with this feature.
     *  
     * @param builder
     */
    void provide(ParserBuilder builder);

    /**
     * Returns all detected feature providers as a map.
     *  
     * @return the map containing all feature providers.
     */
    static Map<String, FeatureProvider> features() {
        ServiceLoader<FeatureProvider> loader = ServiceLoader.load(FeatureProvider.class);
        Map<String, FeatureProvider> featureMap = new HashMap<>();
        for (FeatureProvider provider: loader) {
            featureMap.put(provider.feature().name(), provider);
        }
        return featureMap;
    }
}
