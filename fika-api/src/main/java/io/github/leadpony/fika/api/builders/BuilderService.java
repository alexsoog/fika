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
package io.github.leadpony.fika.api.builders;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author leadpony
 */
public abstract class BuilderService {
   
    private static final ThreadLocal<ServiceLoader<BuilderService>> loaders = 
            ThreadLocal.withInitial(BuilderService::loader);
    
    static BuilderService findService(String type) {
        Iterator<BuilderService> it = loaders.get().iterator();
        while (it.hasNext()) {
            BuilderService service = it.next();
            if (service.supports(type)) {
                return service;
            }
        }
        return null;
    }

    private static ServiceLoader<BuilderService> loader() {
        return ServiceLoader.load(BuilderService.class);
    }
    
    public abstract boolean supports(String type);
    
    public abstract Builder newBuilder(String type);
}
