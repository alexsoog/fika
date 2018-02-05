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

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.github.leadpony.fika.core.model.Document;
import io.github.leadpony.fika.publication.view.ViewResolver.Builder;

/**
 * @author leadpony
 */
public class FreeMarkerViewService implements ViewService {
    
    private static final String TYPE = "freemarker";
    
    @Override
    public boolean canProvideViewOf(String type) {
        return TYPE.equals(type);
    }
    
    @Override
    public ViewResolver.Builder newViewResolverBuilder() {
        return new FreeMarkerViewResolverBuilder();
    }
    
    private static class FreeMarkerViewResolver implements ViewResolver {
        
        private final Configuration configuration;
        
        private FreeMarkerViewResolver(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public View resolveView(String templateName) {
            if (templateName == null) {
                throw new NullPointerException("templateName must not be null.");
            }
            Template template = lookUpTemplate(templateName);
            if (template != null) {
                return new FreeMarkerView(template);
            } else {
                return null;
            }
        }
        
        private Template lookUpTemplate(String name) {
            try {
                return this.configuration.getTemplate(name);
            } catch (IOException e) {
                throw new ViewException(e);
            }
        }
    }
    
    private static class FreeMarkerViewResolverBuilder implements ViewResolver.Builder {

        private Path templateDirectory;
        
        @Override
        public Builder withTemplateDirectory(Path path) {
            this.templateDirectory = path;
            return this;
        }

        @Override
        public ViewResolver build() {
            return new FreeMarkerViewResolver(configure());
        }
        
        private Configuration configure() {
            try {
                Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
                configuration.setDirectoryForTemplateLoading(templateDirectory.toFile());
                configuration.setOutputFormat(HTMLOutputFormat.INSTANCE);
                configuration.setDefaultEncoding("UTF-8");
                return configuration;
            } catch (IOException e) {
                throw new ViewException(e);
            }
        }
    }

    private static class FreeMarkerView extends AbstractTemplateView {
        
        private final Template template;
        
        private FreeMarkerView(Template template) {
            this.template = template;
        }

        @Override
        public void render(Document doc, Map<String, Object> model, Writer writer) {
            model.put("content", renderContent(doc));
            try {
                template.process(model, writer);
            } catch (TemplateException | IOException e) {
                throw new ViewException(e);
            }
        }
    }
}
