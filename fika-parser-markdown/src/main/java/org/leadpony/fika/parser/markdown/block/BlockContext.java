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
package org.leadpony.fika.parser.markdown.block;

import org.leadpony.fika.parser.markdown.common.LinkDefinitionMap;
import org.leadpony.fika.parser.model.NodeFactory;
import org.leadpony.fika.parser.model.Text;

/**
 * The block processing context.
 *
 * @author leadpony
 */
public interface BlockContext {

    /**
     * Return the current line number.
     * 
     * @return current line number, starting from one.
     */
    int lineNo();
    
    /**
     * Returns the instance of node factory.
     * 
     * @return the instance of node factory.
     */
    NodeFactory getNodeFactory();
    
    /**
     * Returns the instance of block builder finder.
     * 
     * @return the instance of block builder finder.
     */
    BlockBuilderFinder finder();

    /**
     * Returns the link reference definitions.
     * 
     * @return the link reference definitions.
     */
    LinkDefinitionMap getLinkDefinitionMap();
  
    /**
     * Adds a text as an inline node.
     * 
     * @param text the text to add.
     */
    void addInline(Text text);
}
