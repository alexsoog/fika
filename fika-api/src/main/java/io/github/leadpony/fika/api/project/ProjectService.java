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
package io.github.leadpony.fika.api.project;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * The service provider interface for projects.
 * 
 * @author leadpony
 */
public abstract class ProjectService {
    
    private static final ThreadLocal<ServiceLoader<ProjectService>> loaders = 
            ThreadLocal.withInitial(ProjectService::loader);
    
    /**
     * Finds the instance of this type.
     * 
     * @return the found instance of this type.
     */
    static ProjectService findService() {
        Iterator<ProjectService> it = loaders.get().iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    private static ServiceLoader<ProjectService> loader() {
        return ServiceLoader.load(ProjectService.class);
    }

    public abstract Project loadProjectFrom(Path path) throws IOException;
}
