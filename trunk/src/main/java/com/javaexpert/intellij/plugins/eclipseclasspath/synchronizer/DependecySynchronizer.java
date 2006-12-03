package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
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

public class DependecySynchronizer implements ModuleComponent, JDOMExternalizable {
    private static final String ECLIPSE_DEPENDENCIES_SUFFIX = "-eclipse_dependencies";

    Module module;

    // Helpers
    LibraryHelper libraryHelper = new LibraryHelper();
    Configuration configuration = new Configuration();
    Registry registry = new Registry();
    UI ui = new UI();

    public DependecySynchronizer(Module module) {
        this.module = module;
    }

    public void projectOpened() {
        registerLoadedListeners();
    }

    public void projectClosed() {
        registry.unregisterAllListeners();
    }

    public void stopTracingChanges(final Module currentModule, VirtualFile file) {
        Registration registration = registry.unregisterFileSystemListener(file);
        libraryHelper.removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(currentModule, registration.libraryName);
    }

    public void traceChanges(Module currentModule, VirtualFile classpathVirtualFile) {
        if (currentModule == null) {
            ui.displayNoProjectSelectedWarnning();
            return;
        }

        String libraryName = ui.getLibraryNameFromUser(currentModule.getProject(), computeEclipseDependenciesLibraryDefaultName(currentModule));
        if (libraryName != null) {
            registerListener(classpathVirtualFile, currentModule, libraryName);

            Library.ModifiableModel model = refreshEclipseDependencies(classpathVirtualFile);
            ui.displayInformationDialog(model.getUrls(OrderRootType.CLASSES));
        }
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


    Library.ModifiableModel refreshEclipseDependencies(VirtualFile classpathVirtualFile) {
        List<String> libs = EclipseTools.extractJarsFromEclipseDotClasspathFile(classpathVirtualFile.getPath());
        return createOrUdateLibrary(classpathVirtualFile, libs);
    }

    private Library.ModifiableModel createOrUdateLibrary(VirtualFile classpathVirtualFile, final List<String> libs) {
        //noinspection ConstantConditions
        final String libsBaseDir = classpathVirtualFile.getParent().getPath();
        final Library newLibrary = libraryHelper.getOrCreateLibrary(module, registry.getRegistration(classpathVirtualFile).libraryName);
        final Library.ModifiableModel libraryModel = newLibrary.getModifiableModel();

        getApplication().runWriteAction(new Runnable() {
            public void run() {
                libraryHelper.repopulateLibraryWithJars(libraryModel, libsBaseDir, libs);
                libraryHelper.makeModuleDependentOnLibrary(module, newLibrary);
            }
        });
        return libraryModel;
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

    @NotNull
    public String getComponentName() {
        return "DependecySynchronizer";
    }

    public void initComponent() {
        // do nothing
    }

    public void disposeComponent() {
        // do nothing
    }

    public void moduleAdded() {
        // do noting
    }

    public boolean isFileTraced(VirtualFile file) {
        return registry.isFileRegistered(file);
    }

    class ClasspathFileModificationListener extends VirtualFileAdapter {

        private final VirtualFile classpathVirtualFile;

        public ClasspathFileModificationListener(VirtualFile classpathVirtualFile) {
            this.classpathVirtualFile = classpathVirtualFile;
        }

        public void contentsChanged(VirtualFileEvent event) {
            if (classpathVirtualFile.getPath().equals(event.getFile().getPath())) {
                Library.ModifiableModel dependencyLibraryModel = refreshEclipseDependencies(classpathVirtualFile);
                ui.displayInformationDialog(dependencyLibraryModel.getUrls(OrderRootType.CLASSES));
            }
        }

        public VirtualFile getClasspathVirtualFile() {
            return classpathVirtualFile;
        }
    }
}
