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
public class InlineHandlerProviderRegistry {
    
    private static final InlineHandlerProviderRegistry instance = new InlineHandlerProviderRegistry();

    private final List<InlineHandlerProvider> providers;
    
    public static InlineHandlerProviderRegistry get() {
        return instance;
    }
    
    private InlineHandlerProviderRegistry() {
        this.providers = new ArrayList<>();
        loadProviders();
    }
    
    public List<InlineHandlerProvider> providers() {
        return providers;
    }
    
    private void loadProviders() {
        ServiceLoader<InlineHandlerProvider> loader = ServiceLoader.load(InlineHandlerProvider.class);
        Iterator<InlineHandlerProvider> it = loader.iterator();
        while (it.hasNext()) {
            add(it.next());
        }
    }

    private void add(InlineHandlerProvider provider) {
        this.providers.add(provider);
    }
}
