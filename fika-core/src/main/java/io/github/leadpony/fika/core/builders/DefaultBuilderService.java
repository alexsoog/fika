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
package io.github.leadpony.fika.core.builders;

import java.util.HashSet;
import java.util.Set;

import io.github.leadpony.fika.api.builders.Builder;
import io.github.leadpony.fika.api.builders.BuilderService;

/**
 * The default implementation of {@link BuilderService}.
 * 
 * @author leadpony
 */
public class DefaultBuilderService extends BuilderService {

    @SuppressWarnings("serial")
    private static final Set<String> supportedTypes = new HashSet<String>() {{
        add("html");
    }};
    
    @Override
    public boolean supports(String type) {
        return supportedTypes.contains(type);
    }

    @Override
    public Builder newBuilder(String type) {
        Builder builder = null;
        switch (type) {
        case "html":
            builder = new HtmlBuilder();
            break;
        }
        return builder;
    }
}
