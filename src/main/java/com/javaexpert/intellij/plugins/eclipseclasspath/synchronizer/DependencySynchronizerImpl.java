package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseTools;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.Registry.Registration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DependencySynchronizerImpl implements ModuleComponent, JDOMExternalizable, DependencySynchronizer {
    private static final String ECLIPSE_DEPENDENCIES_SUFFIX = "-eclipse_dependencies";

    Module module;
    LibraryHelper libraryHelper;
    Configuration configuration;
    Registry registry;
    UI ui;

    public DependencySynchronizerImpl(Module module) {
        this.module = module;
    }

    public void projectOpened() {
        registerLoadedListeners();
    }

    public void projectClosed() {
        registry.unregisterAllListeners();
    }

    @NotNull
    public String getComponentName() {
        return "DependencySynchronizer";
    }

    public void initComponent() {
        libraryHelper = new LibraryHelper(module);
        configuration = new Configuration();
        registry = new Registry();
        ui = new UI();
    }

    public void stopTracingChanges(final Module currentModule, VirtualFile file) {
        Registration registration = registry.unregisterFileSystemListener(file);
        libraryHelper.removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(registration.libraryName);
    }

    public void traceChanges(Module currentModule, VirtualFile classpathVirtualFile) {
        if (currentModule == null) {
            ui.displayNoProjectSelectedWarnning();
            return;
        }

        String libraryName = ui.getLibraryNameFromUser(currentModule.getProject(), computeEclipseDependenciesLibraryDefaultName(currentModule));
        if (libraryName != null) {
            registerListener(classpathVirtualFile, currentModule, libraryName);
            detectedClasspathChanges(classpathVirtualFile);
        }
    }

    public boolean isFileTraced(VirtualFile file) {
        return registry.isFileRegistered(file);
    }

    private void registerLoadedListeners() {
        for (Map.Entry<String, Registration> e : configuration.getLoadedListeners().entrySet()) {
            try {
                registerListener(
                        VirtualFileManager.getInstance().findFileByUrl(e.getKey())
                        , module, e.getValue().libraryName);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void registerListener(VirtualFile classpathVirtualFile, Module currentModule, String libraryName) {
        registry.registerClasspathFileModificationListener(classpathVirtualFile, libraryName, new ClasspathFileModificationListener(classpathVirtualFile), currentModule);
    }


    private Library syncDependencies(VirtualFile classpathVirtualFile) {
        List<String> jars = EclipseTools.extractJarsFromEclipseDotClasspathFile(classpathVirtualFile.getPath());
        Library lib = libraryHelper.getOrCreateLibrary(getLibraryName(classpathVirtualFile));
        libraryHelper.repopulateLibraryContent(lib, jars, classpathVirtualFile.getParent().getPath());
        libraryHelper.makeModuleDependentOnLibrary(lib);
        return lib;
    }

    private String getLibraryName(VirtualFile classpathVirtualFile) {
        return registry.getRegistration(classpathVirtualFile).libraryName;
    }

    private String computeEclipseDependenciesLibraryDefaultName(Module currentModule) {
        return currentModule.getName() + ECLIPSE_DEPENDENCIES_SUFFIX;
    }

    public void readExternal(Element element) throws InvalidDataException {
        configuration.readExternal(element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        configuration.writeExternal(element, registry.getActiveListeners());
    }

    public void disposeComponent() {
        // do nothing
    }

    public void moduleAdded() {
        // do noting
    }

    private void detectedClasspathChanges(VirtualFile classpathVirtualFile) {
        Library library = syncDependencies(classpathVirtualFile);
        ui.displayInformationDialog(library.getUrls(OrderRootType.CLASSES));
    }

    class ClasspathFileModificationListener extends VirtualFileAdapter {
        private final VirtualFile classpathVirtualFile;

        public ClasspathFileModificationListener(VirtualFile classpathVirtualFile) {
            this.classpathVirtualFile = classpathVirtualFile;
        }

        public void contentsChanged(VirtualFileEvent event) {
            if (classpathVirtualFile.getPath().equals(event.getFile().getPath())) {
                detectedClasspathChanges(classpathVirtualFile);
            }
        }

        public VirtualFile getClasspathVirtualFile() {
            return classpathVirtualFile;
        }
    }
}
