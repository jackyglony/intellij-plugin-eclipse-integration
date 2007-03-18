package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.EclipseClasspathEntry;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain.IdeaLibrary;

import java.util.List;

/**
 * User: piotrga
 * Date: 2007-03-17
 * Time: 01:28:32
 */
public interface LibraryManager {
    void removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(String libraryName);

    IdeaLibrary createOrRefreshLibraryWithJars(String libraryName, List<EclipseClasspathEntry> jars, String jarsBasePath);
}
