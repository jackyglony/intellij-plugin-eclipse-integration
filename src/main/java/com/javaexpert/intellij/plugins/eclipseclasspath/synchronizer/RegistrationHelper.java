package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.DependecySynchronizer.ClasspathFileModificationListener;

import java.util.HashMap;
import java.util.Map;

class RegistrationHelper {
    Map<String, Registration> activeListeners = new HashMap<String, Registration>();
    private DependecySynchronizer dependecySynchronizer;

    public RegistrationHelper(DependecySynchronizer dependecySynchronizer) {
        this.dependecySynchronizer = dependecySynchronizer;
    }

    public void unregisterAllListeners() {
        for (RegistrationHelper.Registration r : activeListeners.values())
            getVirtualFileManager().removeVirtualFileListener(r.listener);
    }

    Registration unregisterFileSystemListener(VirtualFile file) {
        Registration registration = activeListeners.remove(file.getUrl());
        getVirtualFileManager().removeVirtualFileListener(registration.listener);
        return registration;
    }

    Registration getRegistration(VirtualFile classpathVirtualFile) {
        return activeListeners.get(classpathVirtualFile.getUrl());
    }

    static class Registration {

        public ClasspathFileModificationListener listener;

        public String moduleName;

        public String libraryName;

        public Registration(ClasspathFileModificationListener listener, String moduleName, String libraryName) {
            this.listener = listener;
            this.moduleName = moduleName;
            this.libraryName = libraryName;
        }
    }

    void registerClasspathFileModificationListener(VirtualFile classpathVirtualFile, String libraryName, ClasspathFileModificationListener listener, Module currentModule) {
        if (!isFileRegistered(classpathVirtualFile)) {
            getVirtualFileManager().addVirtualFileListener(listener);
            activeListeners.put(classpathVirtualFile.getUrl(), new Registration(listener, currentModule.getName(), libraryName));
        }
    }

    VirtualFileManager getVirtualFileManager() {
        return VirtualFileManager.getInstance();
    }

    public boolean isFileRegistered(VirtualFile file) {
        return activeListeners.containsKey(file.getUrl());
    }
}
