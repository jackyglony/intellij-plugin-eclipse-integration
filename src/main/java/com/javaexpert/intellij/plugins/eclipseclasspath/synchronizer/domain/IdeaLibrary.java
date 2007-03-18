package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import com.intellij.openapi.roots.libraries.Library;
import com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.EclipseClasspathEntry;

import java.util.List;

/**
 * User: piotrga
 * Date: 2007-03-18
 * Time: 12:51:21
 */
public interface IdeaLibrary {
    Library nativeLib();

    void repopulateEntries(List<EclipseClasspathEntry> libs, String libsBaseDir);
}
