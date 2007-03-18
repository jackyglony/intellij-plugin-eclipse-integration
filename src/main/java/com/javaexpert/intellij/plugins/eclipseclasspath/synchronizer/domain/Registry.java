package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import com.intellij.openapi.vfs.VirtualFileManager;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.DependencySynchronizerImpl;

import java.util.Map;

/**
 * User: piotrga
 * Date: 2007-03-17
 * Time: 00:50:55
 */
public interface Registry {
    void unregisterAllListeners();

    void unregisterFileSystemListener(String fileName);

    String getLibraryName(String fileName);

    void registerClasspathFileModificationListener(String libraryName, DependencySynchronizerImpl.ClasspathFileModificationListener listener, String moduleName, String fileName);

    void setVirtualFileManager(VirtualFileManager virtualFileManager);

    boolean isFileRegistered(String fileName);

    Map<String, RegistryImpl.Registration> getRegistrations();
}
