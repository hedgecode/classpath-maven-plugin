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

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;

import org.hedgecode.maven.plugin.classpath.jar.ClasspathArtifactVO;
import org.hedgecode.maven.plugin.classpath.jar.JarExtractor;

/**
 * Maven artifact with system scope for JAR libraries.
 *
 * @author Dmitry Samoshin aka gotty
 * @see org.apache.maven.artifact.Artifact
 */
public class JarClasspathArtifact extends SystemClasspathArtifact {

    public static final String TYPE = "jar";

    protected JarClasspathArtifact(
            File jarFile, String groupId, String artifactId, VersionRange versionRange
    ) {
        super(
                jarFile,
                groupId,
                artifactId,
                versionRange,
                TYPE,
                new ClasspathArtifactHandler(TYPE)
        );
    }

    public static final class Factory {

        private Factory() {
        }

        public static Artifact create(File jarFile) throws MojoExecutionException {
            ClasspathArtifactVO artifactVO = JarExtractor.extract(jarFile);

            return new JarClasspathArtifact(
                    jarFile,
                    artifactVO.getGroupId(),
                    artifactVO.getArtifactId(),
                    VersionRange.createFromVersion(artifactVO.getVersion())
            );
        }

    }
}
