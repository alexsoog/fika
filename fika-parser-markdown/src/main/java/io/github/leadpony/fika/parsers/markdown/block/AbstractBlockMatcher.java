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

/**
 * @author leadpony
 */
abstract class AbstractBlockMatcher implements BlockMatcher {

    private Context context;
    private int firstLineNo;
    
    @Override
    public void bind(Context context) {
        this.context = context;
        this.firstLineNo = context.lineNo();
    }
    
    @Override
    public int lineNo() {
        return context().lineNo() - firstLineNo + 1;
    }
    
    @Override
    public Status match(Content content) {
        return Status.NOT_MATCHED;
    }
    
    @Override
    public BlockMatcher interrupt(Content content) {
        if (isInterruptible() && lineNo() > 1) {
            return context().match(content, precedence());
        }
        return null;
    }

    protected Context context() {
        return context;
    }
}
