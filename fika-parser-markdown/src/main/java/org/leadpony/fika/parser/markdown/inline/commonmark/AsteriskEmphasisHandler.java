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
package org.leadpony.fika.parser.markdown.inline.commonmark;

import org.leadpony.fika.core.model.Text;
import org.leadpony.fika.parser.markdown.inline.Delimiter;
import org.leadpony.fika.parser.markdown.inline.HandlerType;

/**
 * @author leadpony
 *
 */
public class AsteriskEmphasisHandler extends AbstractEmphasisHandler {

    public AsteriskEmphasisHandler() {
        super('*');
    }
    
    @Override
    public HandlerType handlerType() {
        return BasicHandlerType.ASTERISK_EMPHASIS;
    }
    
    @Override
    protected Delimiter buildDelimiterRun(Text text, int preceding, int following) {
        boolean opener = calculateLeftFlanking(preceding, following);
        boolean closer = calculateRightFlanking(preceding, following);
        return createDelimiterRun(text, opener, closer);
    }
}
