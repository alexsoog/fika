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
package org.leadpony.fika.publication.builder.spi;

import java.util.HashSet;
import java.util.Set;

import org.leadpony.fika.publication.builder.PublicationBuilder;
import org.leadpony.fika.publication.builder.SiteBuilder;
import org.leadpony.fika.publication.project.Project;

/**
 * @author leadpony
 */
class DefaultPublicationBuilderFactory implements PublicationBuilderFactory {
    
    @SuppressWarnings("serial")
    private static final Set<String> SUPPORTED_TYPES = new HashSet<String>() {{
        add("site");
    }};
    
    @Override
    public boolean supports(String type) {
        return SUPPORTED_TYPES.contains(type);
    }

    @Override
    public PublicationBuilder newBuilder(String type, Project project) {
        switch (type) {
        case "site":
            return new SiteBuilder(project);
        }
        return null;
    }
}
