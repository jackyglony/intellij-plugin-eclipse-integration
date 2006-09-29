package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseTools;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.RegistrationHelper.Registration;
import org.jdom.Element;

import java.util.List;
import java.util.Map;

public class DependecySynchronizer implements ModuleComponent, JDOMExternalizable {
    private static final String ECLIPSE_DEPENDENCIES_SUFFIX = "-eclipse_dependencies";

    Module module;

    // Helpers
    LibraryHelper libraryHelper = new LibraryHelper();
    ConfigurationHelper configurationHelper = new ConfigurationHelper(this);
    RegistrationHelper registrationHelper = new RegistrationHelper(this);

    public DependecySynchronizer(Module module) {
        this.module = module;
    }

    public void projectOpened() {
        registerLoadedListeners();
    }

    public void projectClosed() {
        registrationHelper.unregisterAllListeners();
    }

    public void stopTracingChanges(final Module currentModule, VirtualFile file) {
        Registration registration = registrationHelper.unregisterFileSystemListener(file);
        libraryHelper.removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(currentModule, registration.libraryName);
    }

    public void traceChanges(Module currentModule, VirtualFile classpathVirtualFile) {
        if (currentModule == null) {
            Messages.showWarningDialog("Please open any project.", "No open projects");
            return;
        }

        String libraryName = Messages.showInputDialog(currentModule.getProject(), "Please enter library name.", "Creating library for Eclipse dependencies", Messages.getQuestionIcon(), computeEclipseDependenciesLibraryDefaultName(currentModule), null);
        if (libraryName != null) {
            registerListener(classpathVirtualFile, currentModule, libraryName);

            Library.ModifiableModel model = refreshEclipseDependencies(classpathVirtualFile);
            displayInformationDialog(model.getUrls(OrderRootType.CLASSES));
        }
    }

    private void registerLoadedListeners() {
        for (Map.Entry<String, Registration> e : configurationHelper.loadedListeners.entrySet()) {
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
        registrationHelper.registerClasspathFileModificationListener(classpathVirtualFile, libraryName, new ClasspathFileModificationListener(classpathVirtualFile), currentModule);
    }

    void displayInformationDialog(String[] urls) {
        String res = "";
        for (String url : urls) {
            res += url + "\n";
        }
        Messages.showMessageDialog(
                "Added the following libs:\n" + res, "Eclipse Dependencies Update", Messages.getInformationIcon());
    }

    Library.ModifiableModel refreshEclipseDependencies(VirtualFile classpathVirtualFile) {
        List<String> libs = EclipseTools.extractJarsFromEclipseDotClasspathFile(classpathVirtualFile.getPath());
        return createOrUdateLibrary(classpathVirtualFile, libs);
    }

    private Library.ModifiableModel createOrUdateLibrary(VirtualFile classpathVirtualFile, final List<String> libs) {
        final String classpathFileDir = classpathVirtualFile.getParent().getPath();
        final Library newLibrary = libraryHelper.getOrCreateLibrary(module
                , registrationHelper.getRegistration(classpathVirtualFile).libraryName);
        final Library.ModifiableModel libraryModel = newLibrary.getModifiableModel();

        getApplication().runWriteAction(new Runnable() {
            public void run() {
                libraryHelper.repopulateLibraryWithLibs(libraryModel, classpathFileDir, libs);
                libraryHelper.addLibraryDependencyToModuleIfNotDependantAlready(module, newLibrary);
            }
        });
        return libraryModel;
    }

    private String computeEclipseDependenciesLibraryDefaultName(Module currentModule) {
        return currentModule.getName() + ECLIPSE_DEPENDENCIES_SUFFIX;
    }

    public void readExternal(Element element) throws InvalidDataException {
        configurationHelper.readExternal(element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        configurationHelper.writeExternal(element);
    }

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
        return registrationHelper.isFileRegistered(file);
    }

    class ClasspathFileModificationListener extends VirtualFileAdapter {

        private final VirtualFile classpathVirtualFile;

        public ClasspathFileModificationListener(VirtualFile classpathVirtualFile) {
            this.classpathVirtualFile = classpathVirtualFile;
        }

        public void contentsChanged(VirtualFileEvent event) {
            if (classpathVirtualFile.getPath().equals(event.getFile().getPath())) {
                Library.ModifiableModel dependencyLibraryModel = refreshEclipseDependencies(classpathVirtualFile);
                displayInformationDialog(dependencyLibraryModel.getUrls(OrderRootType.CLASSES));
            }
        }

        public VirtualFile getClasspathVirtualFile() {
            return classpathVirtualFile;
        }
    }
}
