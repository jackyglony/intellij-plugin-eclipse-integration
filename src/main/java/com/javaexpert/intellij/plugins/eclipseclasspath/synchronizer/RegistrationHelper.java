package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;

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

    class ClasspathFileModificationListener extends VirtualFileAdapter {

        private final VirtualFile classpathVirtualFile;

        public ClasspathFileModificationListener(VirtualFile classpathVirtualFile) {
            this.classpathVirtualFile = classpathVirtualFile;
        }

        public void contentsChanged(VirtualFileEvent event) {
            if (classpathVirtualFile.getPath().equals(event.getFile().getPath())) {
                Library.ModifiableModel dependencyLibraryModel = dependecySynchronizer.refreshEclipseDependencies(classpathVirtualFile);
                dependecySynchronizer.displayInformationDialog(dependencyLibraryModel.getUrls(OrderRootType.CLASSES));
            }
        }
    }

    void registerClasspathFileModificationListener(VirtualFile classpathVirtualFile, Module currentModule, String libraryName) {
        ClasspathFileModificationListener listener;
        if (!isFileRegistered(classpathVirtualFile)) {
            listener = new ClasspathFileModificationListener(classpathVirtualFile);
            getVirtualFileManager().addVirtualFileListener(listener);
            activeListeners.put(classpathVirtualFile.getUrl(), new RegistrationHelper.Registration(listener, currentModule.getName(), libraryName));
        }
    }

    VirtualFileManager getVirtualFileManager() {
        return VirtualFileManager.getInstance();
    }

    public boolean isFileRegistered(VirtualFile file) {
        return activeListeners.containsKey(file.getUrl());
    }
}
