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
package io.github.leadpony.fika.parsers.markdown.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.inline.InlineHandler;
import io.github.leadpony.fika.parsers.markdown.inline.handlers.BackslashEscapeHandler;
import io.github.leadpony.fika.parsers.markdown.inline.handlers.CharacterReferenceHandler;

/**
 * @author leadpony
 */
public class ParserBuilder {
    
    private final List<BlockMatcher> matchers = new ArrayList<>(); 
    private final List<InlineHandler> handlers = new ArrayList<>(); 
    private final Set<Class<?>> classes = new HashSet<>();
    
    ParserBuilder() {
        handlers.add(new BackslashEscapeHandler());
        handlers.add(new CharacterReferenceHandler());
    }
    
    public void add(BlockMatcher matcher) {
        this.matchers.add(matcher);
        this.classes.add(matcher.getClass());
    }

    public void add(InlineHandler handler) {
        this.handlers.add(handler);
        this.classes.add(handler.getClass());
    }
    
    public boolean hasAdded(Class<?> clazz) {
        return classes.contains(clazz);
    }

    List<BlockMatcher> matchers() {
        return matchers;
    }

    List<InlineHandler> handlers() {
        return handlers;
    }
}
