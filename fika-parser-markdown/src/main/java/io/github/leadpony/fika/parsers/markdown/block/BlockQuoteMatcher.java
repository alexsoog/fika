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
package io.github.leadpony.fika.parsers.markdown.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.nodes.BlockQuote;
import io.github.leadpony.fika.core.parser.helper.nodes.SimpleBlockQuote;

/**
 * @author leadpony
 */
public class BlockQuoteMatcher extends ContainerBlockMatcher {
    
    private static final Pattern BLOCK_QUOTE_MARKER = Pattern.compile("^\\u0020{0,3}>\\u0020?");

    static Factory factory() {
        return Factory.instance;
    }
    
    private BlockQuoteMatcher() {
    }
   
    @Override
    public Status match(Content content) {
        Matcher m = BLOCK_QUOTE_MARKER.matcher(content);
        if (m.find()) {
            int skip = m.group(0).length();
            matchNext(content.subContent(skip));
            return Status.CONTINUED;
        } else {
            return matchLazyContinuationLine(content);
        }
    }

    @Override
    public BlockQuote close() {
        super.close();
        return new SimpleBlockQuote(this.children); 
    }

    static class Factory implements BlockMatcher.Factory {
        
        private static final int PRECEDENCE = 1;
        private static final Factory instance = new Factory();
        
        @Override
        public int precedence() {
            return PRECEDENCE;
        }

        @Override
        public BlockMatcher newMatcher(Content content) {
            if (BLOCK_QUOTE_MARKER.matcher(content).find()) {
                return new BlockQuoteMatcher();
            } else {
                return null;
            }
        }
    }
}

