package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseClasspathFile;

/**
 * User: piotrga
 * Date: 2006-12-04
 * Time: 21:41:50
 */
public interface DependencySynchronizer {
    void stopTracingChanges(String fileName);

    void traceChanges(EclipseClasspathFile eclipseClasspathFile);

    boolean isFileTraced(String fileName);
}
