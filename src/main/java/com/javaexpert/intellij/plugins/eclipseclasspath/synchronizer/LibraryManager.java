package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.roots.libraries.Library;
import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseClasspathEntry;

import java.util.List;

/**
 * User: piotrga
 * Date: 2007-03-17
 * Time: 01:28:32
 */
public interface LibraryManager {
    void removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(String libraryName);

    Library createOrRefreshLibraryWithJars(List<EclipseClasspathEntry> jars, String libraryName, String jarsBasePath);
}
