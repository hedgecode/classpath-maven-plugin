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
package org.hedgecode.maven.plugin.classpath;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import org.hedgecode.maven.plugin.classpath.util.DirFinder;
import org.hedgecode.maven.plugin.classpath.util.FileFinder;
import org.hedgecode.maven.plugin.classpath.util.FileFinderException;
import org.hedgecode.maven.plugin.classpath.util.WithSubDirFinder;

/**
 * Base class that collects set of classpath JAR files by patterns.
 *
 * @author Dmitry Samoshin aka gotty
 */
public abstract class AbstractClasspathMojo extends AbstractMojo
{
    @Parameter( property = "path", required = false )
    private String path;

    @Parameter( property = "paths", required = false )
    private String[] paths;

    @Parameter( defaultValue = "true",  property = "isMask", required = true )
    private boolean isMask;

    @Parameter( defaultValue = "false",  property = "withSubDir", required = true )
    private boolean withSubDir;

    @Parameter( defaultValue = "false", property = "errorIsEmptyPath", required = true )
    private boolean errorIsEmptyPath;

    @Parameter( defaultValue = "${basedir}", readonly = true, required = true )
    private File projectBasedir;


    protected abstract void setClasspath(Set<File> classpathFiles) throws MojoExecutionException;

    public void execute() throws MojoExecutionException
    {
        List<String> pathPatterns = new ArrayList<>();

        if (path != null && !path.isEmpty())
            pathPatterns.add(path);

        if (paths != null)
            for (String path : paths) {
                pathPatterns.add(path);
            }

        if (getLog().isDebugEnabled())
            getLog().debug("Patterns: " + pathPatterns);

        Set<File> cpFiles = new LinkedHashSet<>();
        for (String pathPattern : pathPatterns) {
            cpFiles.addAll(
                    parsePattern(pathPattern)
            );
        }

        if (getLog().isDebugEnabled())
            getLog().debug("Classpath JARs: " + cpFiles);

        setClasspath(cpFiles);
    }

    private Set<File> parsePattern(String pathPattern) throws MojoExecutionException {
        Set<File> result = new LinkedHashSet<>();
        if (!isMask) {
            File file;
            try {
                file = DirFinder.getFile(pathPattern, projectBasedir);
            } catch (FileFinderException e) {
                if (errorIsEmptyPath) {
                    getLog().error(e.getMessage());
                    throw new MojoExecutionException(e.getMessage());
                } else {
                    getLog().warn(e.getMessage());
                    return result;
                }
            }
            result.add(file);
        } else {
            FileFinder fileFinder = withSubDir
                    ? new WithSubDirFinder(projectBasedir)
                    : new DirFinder(projectBasedir);
            Set<File> files;
            try {
                files = fileFinder.find(pathPattern);
            } catch (FileFinderException e) {
                if (errorIsEmptyPath) {
                    getLog().error(e.getMessage());
                    throw new MojoExecutionException(e.getMessage());
                } else {
                    getLog().warn(e.getMessage());
                    return result;
                }
            }
            result.addAll(files);
        }

        return result;
    }

}
