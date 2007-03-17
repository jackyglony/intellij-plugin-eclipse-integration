package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.vfs.VirtualFileManager;

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
