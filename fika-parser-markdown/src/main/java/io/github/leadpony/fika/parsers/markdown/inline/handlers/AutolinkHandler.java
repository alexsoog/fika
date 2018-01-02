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
package io.github.leadpony.fika.parsers.markdown.inline.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.Link;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;
import io.github.leadpony.fika.parsers.markdown.common.Urls;
import io.github.leadpony.fika.parsers.markdown.inline.AbstractInlineHandler;

/**
 * Handler for Autolinks.
 * 
 * @author leadpony
 */
class AutolinkHandler extends AbstractInlineHandler {

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
    public int handleContent(InputSequence input) {
        int end = 0;
        Matcher m = URL_PATTERN.matcher(input);
        if (m.lookingAt()) {
            end = m.end();
            appendNode(buildUriAutoLink(input.substring(1, end - 1)));
            return end;
        } else {
            m = EMAIL_PATTERN.matcher(input);
            if (m.lookingAt()) {
                end = m.end();
                appendNode(buildEmailAutoLink(input.substring(1, end - 1)));
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
        Link newNode = nodeFactory().newLink();
        newNode.setDestination(Urls.encode(url));
        Text text = nodeFactory().newText();
        text.setContent(label);
        newNode.appendChild(text);
        return newNode;
    }
}
