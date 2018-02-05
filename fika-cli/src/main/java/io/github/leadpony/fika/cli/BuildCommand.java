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

package io.github.leadpony.fika.cli;

import io.github.leadpony.fika.publication.builders.PublicationBuilder;
import io.github.leadpony.fika.publication.builders.spi.PublicationBuilderFactory;
import io.github.leadpony.fika.publication.project.Project;

/**
 *
 */
public class BuildCommand extends AbstractProjectCommand {
    
    private static final String DEFAULT_TYPE = "site";
    
    private String type;
    
    public BuildCommand() {
        this.type = DEFAULT_TYPE;
    }

    @Override
    public void execute(Project project) throws Exception {
        PublicationBuilderFactory factory = PublicationBuilderFactory.factoryFor(type);
        PublicationBuilder builder = factory.newBuilder(type, project);
        if (builder == null) {
            // TODO:
        }
        builder.build();
    }
}
