/*
 * Copyright (c) 2015. Developed by Hedgecode.
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
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * Maven artifact with system scope.
 *
 * @author Dmitry Samoshin aka gotty
 * @see org.apache.maven.artifact.Artifact
 */
public abstract class SystemClasspathArtifact extends DefaultArtifact {

    private static final String CLASSPATH_SCOPE = "system";

    protected SystemClasspathArtifact(
            File systemFile, String groupId, String artifactId, VersionRange versionRange, String type, ArtifactHandler artifactHandler
    ) {
        super(
                groupId,
                artifactId,
                versionRange,
                CLASSPATH_SCOPE,
                type,
                null,
                artifactHandler
        );
        setFile(systemFile);
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + getGroupId().hashCode();
        result = 37 * result + getArtifactId().hashCode();
        result = 37 * result + getType().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (!(object instanceof Artifact))
            return false;

        return equalsWithoutVersion((Artifact) object);
    }

    protected boolean equalsWithoutVersion(Artifact artifact) {
        if (artifact.getGroupId().equals(getGroupId())
                && artifact.getArtifactId().equals(getArtifactId())
                && artifact.getType().equals(getType()))
            return true;

        return false;
    }

}
