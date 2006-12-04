package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * User: piotrga
 * Date: 2006-12-04
 * Time: 21:41:50
 */
public interface DependencySynchronizer {
    void stopTracingChanges(Module currentModule, VirtualFile file);

    void traceChanges(Module currentModule, VirtualFile classpathVirtualFile);

    boolean isFileTraced(VirtualFile file);
}
