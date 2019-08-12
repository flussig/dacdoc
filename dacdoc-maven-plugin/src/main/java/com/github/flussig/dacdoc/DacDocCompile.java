package com.github.flussig.dacdoc;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.flussig.dacdoc.check.Check;
import com.github.flussig.dacdoc.check.CheckMetadata;
import com.github.flussig.dacdoc.check.CheckRegistry;
import com.github.flussig.dacdoc.exception.DacDocException;
import com.github.flussig.dacdoc.text.Anchor;
import com.github.flussig.dacdoc.text.Reader;
import com.github.flussig.dacdoc.util.JavaClassFinder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Compile goal:
 * searches through README.md files in source directory and subdirectories,
 * replaces placeholders for DACDOC tests with green/red/orange/grey pics
 */
@Mojo(name = "compile")
public class DacDocCompile
    extends AbstractMojo
{
    @Parameter(readonly = true, defaultValue = "${project.basedir}")
    private File srcDirectory;

    @Parameter(defaultValue = "${project}")
    public MavenProject project;

    public void execute() throws MojoExecutionException
    {
        try {
            loadCustomChecks();

            transformDocumentationFiles();
        } catch(Exception e) {
            throw new MojoExecutionException("exception while executing dacdoc-maven-plugin compile goal " + e.getMessage());
        }
    }

    private void loadCustomChecks() {
        // load classes that extend DacDoc check
        JavaClassFinder classFinder = new JavaClassFinder(getClassLoader(this.project));
        String outputDir = project.getBuild().getOutputDirectory();
        String testOutputDir = project.getBuild().getTestOutputDirectory();

        List<Class<? extends Check>> outputClasses = classFinder.findAllMatchingTypes(outputDir, Check.class);
        List<Class<? extends Check>> outputTestClasses = classFinder.findAllMatchingTypes(testOutputDir, Check.class);

        getLog().info(String.format("outputClasses extending DacDoc check: %s", outputClasses));
        getLog().info(String.format("outputTestClasses extending DacDoc check: %s", outputTestClasses));

        Set<Class<? extends Check>> allUserCheckClasses = new HashSet<>();
        allUserCheckClasses.addAll(outputClasses);
        allUserCheckClasses.addAll(outputTestClasses);

        // put all check classes to registry
        for(Class<? extends Check> checkClass: allUserCheckClasses) {
            String checkName = Optional.ofNullable(checkClass.getAnnotation(CheckMetadata.class)).map(CheckMetadata::id).orElse(checkClass.getSimpleName());

            CheckRegistry.checkRegistry.put(checkName, checkClass);

            getLog().info(String.format("registered test class %s in check registry. test id: %s", checkClass.getName(), checkName));
        }
    }

    private void transformDocumentationFiles() throws IOException, DacDocException {
        File allSourceDir = srcDirectory;

        getLog().info( String.format("Build directory: %s", allSourceDir.getAbsolutePath()));

        // prepare source directory: create resource folder with images for check results (if not exists)
        prepareResourceDirectory(allSourceDir);

        // collect all readme files
        Set<File> readmeFiles = Reader.findMarkdownFiles(allSourceDir.toPath());

        getLog().info( String.format("Readme files: %s", readmeFiles));

        // parse and find all placeholders
        Map<File, Set<Anchor>> parsedAnchors = Reader.parseFiles(readmeFiles);

        // replace DACDOC placeholders with indicators of check results
        Path dacdocResources = Paths.get(allSourceDir.getAbsolutePath(), Constants.DACDOC_RESOURCES);
        getLog().info( String.format("DacDoc resource directory: %s", dacdocResources));

        Map<File, String> processedFiles = Reader.getTransformedFiles(parsedAnchors, dacdocResources);

        // add indicators of check results to each readme file
        for(Map.Entry<File, String> fileContent: processedFiles.entrySet()) {
            Files.write(fileContent.getKey().toPath(), fileContent.getValue().getBytes());
        }
    }

    // write necessary resources to dacdoc-resources directory
    private void prepareResourceDirectory(File baseDir) throws IOException {
        File destDacDocResourceDirectory = createDacDocResourceDir(baseDir);

        List<String> indicatorFileNames = Arrays.asList(Constants.GREY_IND, Constants.GREEN_IND, Constants.ORANGE_IND, Constants.RED_IND);

        for(String indicatorFileName: indicatorFileNames) {
            Path outPath = Paths.get(destDacDocResourceDirectory.getAbsolutePath(), indicatorFileName);

            try(InputStream stream = getClass().getClassLoader().getResource(indicatorFileName).openStream()) {
                byte[] resourceBytes = new byte[stream.available()];
                stream.read(resourceBytes);
                Files.write(outPath, resourceBytes);
                getLog().info( String.format("resource file written: ", outPath));
            } catch(Exception e) {
                getLog().error(String.format("resource file failed: ", outPath), e);
            }
        }
    }

    private File createDacDocResourceDir(File baseDir) {
        File destDacDocResourceDirectory = Paths.get(baseDir.getAbsolutePath(), Constants.DACDOC_RESOURCES).toFile();
        getLog().info( String.format("DacDoc resource directory: %s", destDacDocResourceDirectory.getAbsolutePath()));

        if(!destDacDocResourceDirectory.exists()) {
            destDacDocResourceDirectory.mkdir();
            getLog().info( String.format("DacDoc resource directory created: %s", destDacDocResourceDirectory.getAbsolutePath()));
        }
        return destDacDocResourceDirectory;
    }

    private ClassLoader getClassLoader(MavenProject project)
    {
        try
        {
            Set<String> classpathElements = new HashSet<>(project.getCompileClasspathElements());
            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            getLog().info(String.format("classpath elements: %s", classpathElements));

            Set<URL> urlsSet = classpathElements.stream()
                    .map(cpe -> {
                        try {
                            return Paths.get(cpe).toUri().toURL();
                        } catch(Exception e) {
                            getLog().error(String.format("couldn't use classpath %s. %s", cpe, e.getMessage()));
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .peek(url -> getLog().info(String.format("loaded classpath url: %s", url)))
                    .collect(Collectors.toSet());
            URL urls[] = new URL[classpathElements.size()];

            urlsSet.toArray(urls);
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        }
        catch ( Exception e )
        {
            getLog().error( "Couldn't get the classloader. " + e.getMessage());
            return this.getClass().getClassLoader();
        }
    }
}
