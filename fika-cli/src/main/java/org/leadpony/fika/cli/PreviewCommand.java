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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.JavaUtilLog;
import org.eclipse.jetty.util.log.Log;
import org.leadpony.fika.publication.project.Project;

/**
 * @author leadpony
 */
public class PreviewCommand extends AbstractProjectCommand {

    @Override
    public void execute(Project project) throws Exception {
        runServer();
    }
    
    private void runServer() throws Exception {
        Log.setLog(new JavaUtilLog());
        Server server = new Server();
        server.start();
        server.join();
    }
}
