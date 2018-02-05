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
package io.github.leadpony.fika.publication.view;

import java.util.ServiceLoader;

/**
 * @author leadpony
 */
public interface ViewService {
    
    static ViewService findService(String type) {
        if (type == null) {
            throw new NullPointerException("type must not be null.");
        }
        ServiceLoader<ViewService> loader = ServiceLoader.load(ViewService.class);
        for (ViewService service: loader) {
            if (service.canProvideViewOf(type)) {
                return service;
            }
        }
        return null;
    }
    
    boolean canProvideViewOf(String type);
    
    ViewResolver.Builder newViewResolverBuilder();
}
