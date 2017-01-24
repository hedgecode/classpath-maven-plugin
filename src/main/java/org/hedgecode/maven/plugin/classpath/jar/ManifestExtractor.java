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

import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import org.hedgecode.maven.plugin.classpath.util.FileStringUtils;

/**
 * Information extractor (groupid, artifactid, version) from MANIFEST file.
 *
 * @author Dmitry Samoshin aka gotty
 */
public class ManifestExtractor {

    private static final Pattern GROUPID_PATTERN = Pattern.compile("^[a-zA-Z_-]+(\\.[a-zA-Z_-]+)+$");
    private static final Pattern VERSION_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)+(-SNAPSHOT)?$");

    private static final String[] GROUPID_KEYS = {
            "Implementation-Title",
            "Extension-Name",
            "Implementation-Vendor-Id",
            "Specification-Vendor-Id"
    };

    private static final String[] VERSION_KEYS = {
            "Version",
            "Implementation-Version",
            "Specification-Version"
    };


    public static ClasspathArtifactVO extract(Manifest manifest) {
        ClasspathArtifactVO result = new ClasspathArtifactVO();

        if (manifest != null) {
            Attributes attr = manifest.getMainAttributes();

            for (String key : GROUPID_KEYS) {
                String groupid = attr.getValue(key);
                if (groupid != null && !groupid.isEmpty()) {
                    if (GROUPID_PATTERN.matcher(
                            FileStringUtils.replacePath(groupid)
                    ).matches()) {
                        result.setGroupId(groupid);
                        break;
                    }
                }
            }

            for (String key : VERSION_KEYS) {
                String version = attr.getValue(key);
                if (version != null && !version.isEmpty()) {
                    if (VERSION_PATTERN.matcher(version).matches()) {
                        result.setVersion(version);
                        break;
                    }
                }
            }
        }

        return result;
    }

}
