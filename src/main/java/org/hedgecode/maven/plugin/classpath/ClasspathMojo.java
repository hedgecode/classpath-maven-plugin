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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import org.hedgecode.maven.plugin.classpath.artifact.JarClasspathArtifact;
import org.hedgecode.maven.plugin.classpath.util.FileStringUtils;

/**
 * Generates a set of library artifacts based on the input search parameters
 * and overwrites project dependencies for further compilation.
 *
 * @author Dmitry Samoshin aka gotty
 */
@Mojo( name = "classpath", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class ClasspathMojo extends AbstractClasspathMojo {

    /**
     * Overwrites <code>dependencies</code> of your project
     * if <code>true</code> by adding the found libraries.
     */
    @Parameter( defaultValue = "true", property = "assignProjectClasspath", required = true )
    private boolean assignProjectClasspath;

    /**
     * Writes the resulting classpath with the found libraries
     * to the specified project variable if the parameter is not <code>null</code>.
     */
    @Parameter( property = "outputProperty", required = false )
    private String outputProperty;

    /**
     * Saves the resulting classpath with the found libraries
     * to the specified file if the parameter is not <code>null</code>.
     */
    @Parameter( property = "outputFile", required = false )
    private File outputFile;

    /**
     * Path separator for the current Operation System.<br />
     * This is used to generate classpath string of {@link #outputProperty}.
     */
    @Parameter( defaultValue = "${path.separator}", property = "pathSeparator", required = true )
    private String pathSeparator;

    /**
     * If <code>true</code> it will rewrite <code>dependencies</code>
     * with the found libraries in the case of matching artifacts,
     * otherwise (by default) matched <code>dependency</code> libraries remain unchanged.
     *
     * @since 0.2
     */
    @Parameter( defaultValue = "false", property = "overlapDependencyMatch", required = true )
    private boolean overlapDependencyMatch;

    /**
     * The current project instance.<br />
     * This is used for access and modify project dependencies and set project properties.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;


    @Override
    protected void setClasspath(Set<File> classpathFiles) throws MojoExecutionException {
        if (outputProperty != null) {
            setOutputProperty(
                    getClasspathString(classpathFiles)
            );
        }

        if (outputFile != null) {
            writeClasspathToFile(
                    getClasspathString(classpathFiles)
            );
        }

        if (assignProjectClasspath) {
            assignProjectClasspath(classpathFiles);
        }
    }

    private String getClasspathString(Set<File> classpath) {
        StringBuilder sb = new StringBuilder();
        Iterator<File> i = classpath.iterator();
        if (i.hasNext()) {
            sb.append(i.next().getAbsolutePath());
            while (i.hasNext()) {
                sb.append(pathSeparator).append(i.next().getAbsolutePath());
            }
        }
        return sb.toString();
    }

    private void assignProjectClasspath(Set<File> classpathFiles) throws MojoExecutionException {
        Set<Artifact> artifacts = new LinkedHashSet<>();
        for (File cpFile : classpathFiles) {
            if (FileStringUtils.isExtType(cpFile.getName(), JarClasspathArtifact.TYPE)) {
                Artifact artifact = JarClasspathArtifact.Factory.create(cpFile);
                artifacts.add(artifact);
                if (getLog().isDebugEnabled())
                    getLog().debug("Classpath artifact: " + artifact);
            }
        }
        assignDependencyArtifacts(artifacts);
    }

    private void assignDependencyArtifacts(Set<Artifact> artifacts) {
        if (!artifacts.isEmpty()) {
            Set<Artifact> dependencyArtifacts = project.getDependencyArtifacts();
            Set<Artifact> excludedArtifacts = new LinkedHashSet<>();
            for (Artifact artifact : artifacts) {
                for (Artifact dArtifact : dependencyArtifacts) {
                    if (artifact.equals(dArtifact)) {
                        excludedArtifacts.add(
                                overlapDependencyMatch ? dArtifact : artifact
                        );
                        break;
                    }
                }
            }
            if (!excludedArtifacts.isEmpty()) {
                if (overlapDependencyMatch)
                    dependencyArtifacts.removeAll(excludedArtifacts);
                else
                    artifacts.removeAll(excludedArtifacts);
            }
            dependencyArtifacts.addAll(artifacts);
            project.setDependencyArtifacts(dependencyArtifacts);
            if (getLog().isInfoEnabled())
                getLog().info("Added classpath artifacts: " + artifacts);
        } else {
            if (getLog().isInfoEnabled())
                getLog().info("Not found classpath artifacts to add");
        }
    }

    private void setOutputProperty(String cpString) throws MojoExecutionException {
        project.getProperties().setProperty(outputProperty, cpString);
        if (getLog().isInfoEnabled())
            getLog().info("${" + outputProperty + "} = " + cpString);
    }

    private void writeClasspathToFile(String cpString) throws MojoExecutionException {
        outputFile.getParentFile().mkdirs();
        Writer w = null;
        try {
            w = new BufferedWriter(new FileWriter(outputFile));
            w.write(cpString);
            if (getLog().isInfoEnabled())
                getLog().info("Wrote classpath to file '" + outputFile + "'.");
        } catch (IOException ex) {
            String errorMsg = "Error while writing classpath to file '" + outputFile + "': " + ex.toString();
            getLog().error(errorMsg);
            throw new MojoExecutionException(errorMsg, ex);
        } finally {
            if (w != null)
                try {
                    w.close();
                } catch (IOException ignored) {}
        }
    }

}
