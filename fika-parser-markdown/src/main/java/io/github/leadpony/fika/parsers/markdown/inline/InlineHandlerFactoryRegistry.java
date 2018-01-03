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
package io.github.leadpony.fika.parsers.markdown.inline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author leadpony
 */
public class InlineHandlerFactoryRegistry {
    
    private static final InlineHandlerFactoryRegistry instance = new InlineHandlerFactoryRegistry();

    private final List<InlineHandlerFactory> factories;
    
    public static InlineHandlerFactoryRegistry get() {
        return instance;
    }
    
    private InlineHandlerFactoryRegistry() {
        this.factories = new ArrayList<>();
        loadDefaultFactories();
    }
    
    public List<InlineHandlerFactory> factories() {
        return factories;
    }
    
    private void loadDefaultFactories() {
        ServiceLoader<InlineHandlerFactory> loader = ServiceLoader.load(InlineHandlerFactory.class);
        Iterator<InlineHandlerFactory> it = loader.iterator();
        while (it.hasNext()) {
            add(it.next());
        }
    }

    private void add(InlineHandlerFactory factory) {
        this.factories.add(factory);
    }
}
