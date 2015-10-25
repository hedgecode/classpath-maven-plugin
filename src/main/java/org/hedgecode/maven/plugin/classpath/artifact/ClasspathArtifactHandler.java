/*
 * Copyright (c) 2015. Developed by HedgeCode.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hedgecode.maven.plugin.classpath.artifact;

import org.apache.maven.artifact.handler.DefaultArtifactHandler;

/**
 * Handler for maven artifacts that always is on classpath.
 *
 * @author Dmitry Samoshin aka gotty
 * @see {@link org.apache.maven.artifact.handler.ArtifactHandler}
 */
public class ClasspathArtifactHandler extends DefaultArtifactHandler {

    public ClasspathArtifactHandler(String type) {
        super(type);
    }

    @Override
    public boolean isAddedToClasspath() {
        return true;
    }

}
