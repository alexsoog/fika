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
package org.leadpony.fika.publication.view.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.leadpony.fika.publication.view.AbstractViewResolverBuilder;
import org.leadpony.fika.publication.view.View;
import org.leadpony.fika.publication.view.ViewException;
import org.leadpony.fika.publication.view.ViewResolver;
import org.leadpony.fika.publication.view.ViewService;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * @author leadpony
 */
public class FreeMarkerViewService implements ViewService {
    
    private static final String TYPE = "freemarker";
    private static final String PACKAGE_PATH = 
            "/" +  FreeMarkerViewService.class.getPackage().getName().replace(".", "/");
    
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
    
    private static class FreeMarkerViewResolverBuilder extends AbstractViewResolverBuilder {

        @Override
        public ViewResolver build() {
            return new FreeMarkerViewResolver(configure());
        }
        
        private Configuration configure() {
            try {
                return buildConfiguration();
            } catch (IOException | TemplateModelException e) {
                throw new ViewException(e);
            }
        }
        
        private Configuration buildConfiguration() throws IOException, TemplateModelException {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
            cfg.setTemplateLoader(createLoader());
            cfg.setOutputFormat(HTMLOutputFormat.INSTANCE);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setSharedVaribles(this.sharedValues);
            cfg.addAutoImport("fika", "/fika.ftl");
            return cfg;
        }
        
        private TemplateLoader createLoader() throws IOException {
            TemplateLoader[] loaders = { 
                    fileTemplateLoader(), 
                    classTemplateLoader() 
                    };
            return new MultiTemplateLoader(loaders);
        }
        
        private TemplateLoader fileTemplateLoader() throws IOException {
            return new FileTemplateLoader(this.templateDirectory.toFile());
        }
        
        private TemplateLoader classTemplateLoader() {
            Class<?> clazz = FreeMarkerViewService.class;
            return new ClassTemplateLoader(clazz, PACKAGE_PATH);
        }
    }

    private static class FreeMarkerView implements View {
        
        private final Template template;
        
        private FreeMarkerView(Template template) {
            this.template = template;
        }

        @Override
        public void render(Map<String, Object> context, Writer writer) {
            try {
                this.template.process(context, writer);
            } catch (TemplateException | IOException e) {
                throw new ViewException(e);
            }
        }
    }
}
