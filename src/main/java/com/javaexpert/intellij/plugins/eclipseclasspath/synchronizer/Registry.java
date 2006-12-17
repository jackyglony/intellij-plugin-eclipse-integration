package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.DependencySynchronizerImpl.ClasspathFileModificationListener;

import java.util.HashMap;
import java.util.Map;

class Registry {
    protected static class Registration {
        public ClasspathFileModificationListener listener;
        public String moduleName;
        public String libraryName;

        public Registration(ClasspathFileModificationListener listener, String moduleName, String libraryName) {
            this.listener = listener;
            this.moduleName = moduleName;
            this.libraryName = libraryName;
        }
    }

    private Map<String, Registration> registrations = new HashMap<String, Registration>();

    public void unregisterAllListeners() {
        for (Registry.Registration r : registrations.values())
            getVirtualFileManager().removeVirtualFileListener(r.listener);
    }

    public void unregisterFileSystemListener(VirtualFile file) {
        Registration registration = registrations.remove(file.getUrl());
        getVirtualFileManager().removeVirtualFileListener(registration.listener);
    }

    protected Registration getRegistration(VirtualFile classpathVirtualFile) {
        return registrations.get(classpathVirtualFile.getUrl());
    }

    public String getLibraryName(VirtualFile classpathVirtualFile) {
        return getRegistration(classpathVirtualFile).libraryName;
    }

    public void registerClasspathFileModificationListener(VirtualFile classpathVirtualFile, String libraryName, ClasspathFileModificationListener listener, String moduleName) {
        if (!isFileRegistered(classpathVirtualFile)) {
            getVirtualFileManager().addVirtualFileListener(listener);
            registrations.put(classpathVirtualFile.getUrl(), new Registration(listener, moduleName, libraryName));
        }
    }

    protected VirtualFileManager getVirtualFileManager() {
        return VirtualFileManager.getInstance();
    }

    public boolean isFileRegistered(VirtualFile file) {
        return registrations.containsKey(file.getUrl());
    }

    public Map<String, Registration> getRegistrations() {
        return registrations;
    }
}
