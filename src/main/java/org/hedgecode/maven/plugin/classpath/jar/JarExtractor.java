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
package org.hedgecode.maven.plugin.classpath.jar;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;

import org.hedgecode.maven.plugin.classpath.util.FileStringUtils;

/**
 * Information extractor (groupid, artifactid, version) from JAR file.
 *
 * @author Dmitry Samoshin aka gotty
 */
public class JarExtractor {

    private static final Pattern NAME_PATTERN = Pattern.compile("^(.+)-?([0-9.]*)(-SNAPSHOT)?\\.jar$");

    private static final String JAVA_BIN_CLASS_EXT = "class";

    private static final String DEF_ARTIFACTID = "artifact";
    private static final String DEF_GROUPID = "org.hedgecode.maven.plugins";
    private static final String DEF_VERSION = "1.0.0";


    public static ClasspathArtifactVO extract(File file) throws MojoExecutionException
    {
        ClasspathArtifactVO result;

        try (JarFile jarFile = new JarFile(file))
        {
            Manifest manifest = jarFile.getManifest();

            if (manifest != null) {
                result = ManifestExtractor.extract(manifest);
            } else {
                result = new ClasspathArtifactVO();
            }

            Matcher matcher = NAME_PATTERN.matcher(file.getName());
            if (matcher.find()) {
                result.setArtifactId(matcher.group(1));
                if (result.getVersion() == null || result.getVersion().isEmpty())
                    result.setVersion(matcher.group(2));
            }

            if (result.getGroupId() == null || result.getGroupId().isEmpty()) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements())
                {
                    JarEntry jarEntry = entries.nextElement();
                    if (FileStringUtils.isExtType(jarEntry.getName(), JAVA_BIN_CLASS_EXT)) {
                        String groupId = FileStringUtils.getJavaPath(jarEntry.getName());
                        if (groupId != null) {
                            result.setGroupId(groupId);
                            break;
                        }
                    }
                }
            }
            assignDefArtifactValues(result);
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Error while opening JAR file '" + file + "': ", e
            );
        }

        return result;
    }

    private static void assignDefArtifactValues(ClasspathArtifactVO artifact) {
        if (artifact.getGroupId() == null || artifact.getGroupId().isEmpty()) {
            artifact.setGroupId(DEF_GROUPID);
        }

        if (artifact.getArtifactId() == null || artifact.getArtifactId().isEmpty()) {
            artifact.setArtifactId(DEF_ARTIFACTID + new Random().nextInt(100));
        }

        if (artifact.getVersion() == null || artifact.getVersion().isEmpty()) {
            artifact.setVersion(DEF_VERSION);
        }
    }

}
