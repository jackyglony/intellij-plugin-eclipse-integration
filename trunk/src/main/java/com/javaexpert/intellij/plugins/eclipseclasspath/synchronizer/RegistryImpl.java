package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.vfs.VirtualFileManager;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.DependencySynchronizerImpl.ClasspathFileModificationListener;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RegistryImpl extends AbstractModuleComponent implements Registry {
    private VirtualFileManager virtualFileManager;

    public RegistryImpl(VirtualFileManager manager) {
        setVirtualFileManager(manager);
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "Registry";
    }

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
        for (RegistryImpl.Registration r : registrations.values())
            getVirtualFileManager().removeVirtualFileListener(r.listener);
    }

    public void unregisterFileSystemListener(String fileName) {
        Registration registration = registrations.remove(fileName);
        getVirtualFileManager().removeVirtualFileListener(registration.listener);
    }

    protected Registration getRegistration(String fileName) {
        return registrations.get(fileName);
    }

    public String getLibraryName(String fileName) {
        return getRegistration(fileName).libraryName;
    }

    public void registerClasspathFileModificationListener(String libraryName, ClasspathFileModificationListener listener, String moduleName, String fileName) {
        if (!isFileRegistered(fileName)) {
            getVirtualFileManager().addVirtualFileListener(listener);
            registrations.put(fileName, new Registration(listener, moduleName, libraryName));
        }
    }

    protected VirtualFileManager getVirtualFileManager() {
        return virtualFileManager;
    }


    public void setVirtualFileManager(VirtualFileManager virtualFileManager) {
        this.virtualFileManager = virtualFileManager;
    }

    public boolean isFileRegistered(String fileName) {
        return registrations.containsKey(fileName);
    }

    public Map<String, Registration> getRegistrations() {
        return registrations;
    }
}
