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
package io.github.leadpony.fika.publication.builders;

import io.github.leadpony.fika.publication.project.Project;
import io.github.leadpony.fika.publication.view.ViewResolver;
import io.github.leadpony.fika.publication.view.ViewService;

/**
 * @author leadpony
 */
public abstract class AbstractHtmlBuilder extends AbstractPublicationBuilder {
    
    private ViewResolver viewResolver;

    protected AbstractHtmlBuilder(Project project) {
        super(project);
    }
    
    @Override
    protected void initialize() throws Exception {
        super.initialize();
        this.viewResolver = createViewResolver();
    }
    
    protected ViewResolver createViewResolver() {
        ViewService service = ViewService.findService("freemarker");
        ViewResolver.Builder builder = service.newViewResolverBuilder();
        builder.withTemplateDirectory(project().templateDirectory());
        return builder.build();
    }
    
    protected ViewResolver viewResolver() {
        return viewResolver;
    }
}
