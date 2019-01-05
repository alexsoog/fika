/*
 * Copyright 2017-2019 the Fika authors.
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
package org.leadpony.fika.format.markdown.block;

import org.leadpony.fika.format.markdown.common.InputSequence;
import org.leadpony.fika.core.model.Document;

/**
 * Builder of document node.
 * 
 * @author leadpony
 */
public class DocumentBuilder extends ContainerBlockBuilder {
    
    @Override
    public BlockType blockType() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Result processLine(InputSequence input) {
        super.processLine(input);
        return Result.CONTINUED;
    }
    
    @Override
    protected Document buildBlock() {
        return getNodeFactory().createDocument();
    }
}