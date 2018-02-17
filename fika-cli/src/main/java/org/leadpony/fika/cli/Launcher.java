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

package org.leadpony.fika.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 */
public class Launcher {
    
    private static final Logger log = Logger.getLogger(Launcher.class.getName());
    
    public static void main(String[] args) throws Exception {
        new Launcher().launch(args);
    }

    private void launch(String[] args) throws Exception {
        configureLogger();
        if (args.length >= 1) {
            executeCommand(args);
        } else {
            help();
        }
        log.info("Completed.");
    }
    
    private static void configureLogger() throws IOException {
        LogManager logManager = LogManager.getLogManager();
        try (InputStream input = Launcher.class.getResourceAsStream("logging.properties")) {
            logManager.readConfiguration(input);
        }
    }

    private static void executeCommand(String[] args) throws Exception {
        Command command = createCommand(args[0]);
        List<String> options = Arrays.asList(args).subList(1, args.length);
        command.setOptions(options);
        command.execute();
    }

    private static Command createCommand(String name) {
        switch (name) {
        case "build":
            return new BuildCommand();
        case "preview":
            return new PreviewCommand();
        default:
            return null;
        }
    }
    
    private static void help() {
    }
}
