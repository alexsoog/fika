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
package org.leadpony.fika.parser.markdown.inline.commonmark;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.leadpony.fika.parser.markdown.common.InputSequence;
import org.leadpony.fika.parser.markdown.common.UrlEncoder;
import org.leadpony.fika.parser.markdown.inline.AbstractInlineHandler;
import org.leadpony.fika.parser.markdown.inline.HandlerType;
import org.leadpony.fika.parser.model.Link;
import org.leadpony.fika.parser.model.Text;

/**
 * Handler for Autolinks.
 * 
 * @author leadpony
 */
public class AutolinkHandler extends AbstractInlineHandler {

    private static final char TRIGGER_LETTER = '<';
    
    private static final Pattern URL_PATTERN = Pattern.compile(
            "<[a-zA-Z][a-zA-Z0-9+.-]{1,31}:[^\\s\\p{Cntrl}<>]*>"
            );
    /*
     * An email autolink consists of <, followed by an email address, 
     * followed by >. 
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "<[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" + 
            "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*>"
            );
    
    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }

    @Override
    public HandlerType handlerType() {
        return BasicHandlerType.AUTOLINK;
    }
    
    @Override
    public int handleContent(InputSequence input) {
        int end = 0;
        Matcher m = URL_PATTERN.matcher(input);
        if (m.lookingAt()) {
            end = m.end();
            getAppender().appendNode(buildUriAutoLink(input.substring(1, end - 1)));
            return end;
        } else {
            m = EMAIL_PATTERN.matcher(input);
            if (m.lookingAt()) {
                end = m.end();
                getAppender().appendNode(buildEmailAutoLink(input.substring(1, end - 1)));
            }
        }
        return end;
    }
    
    private Link buildUriAutoLink(String str) {
        return buildAutoLink(str, str);
    }
    
    private Link buildEmailAutoLink(String str) {
        String url = "mailto:" + str;
        return buildAutoLink(url, str);
    }

    private Link buildAutoLink(String url, String label) {
        Link newNode = getNodeFactory().newLink();
        UrlEncoder encoder = new UrlEncoder();
        newNode.setDestination(encoder.encode(url));
        Text text = getNodeFactory().newText(label);
        newNode.appendChild(text);
        return newNode;
    }
}
