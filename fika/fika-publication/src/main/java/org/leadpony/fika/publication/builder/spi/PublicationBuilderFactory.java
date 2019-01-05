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
package org.leadpony.fika.publication.builder.spi;

import java.util.ServiceLoader;

import org.leadpony.fika.publication.builder.PublicationBuilder;
import org.leadpony.fika.publication.project.Project;

/**
 * @author leadpony
 */
public interface PublicationBuilderFactory {
    
    static final PublicationBuilderFactory DEFAULT_FACTORY = new DefaultPublicationBuilderFactory();
    
    boolean supports(String type);

    PublicationBuilder newBuilder(String type, Project project);
    
    static PublicationBuilderFactory factoryFor(String type) {
        if (type == null) {
            throw new NullPointerException("type must not be null.");
        }
        ServiceLoader<PublicationBuilderFactory> loader = ServiceLoader.load(PublicationBuilderFactory.class);
        for (PublicationBuilderFactory service: loader) {
            if (service.supports(type)) {
                return service;
            }
        }
        if (DEFAULT_FACTORY.supports(type)) {
            return DEFAULT_FACTORY;
        }
        return null;
    }
}
