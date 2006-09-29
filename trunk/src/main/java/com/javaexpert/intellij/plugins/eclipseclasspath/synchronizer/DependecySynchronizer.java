package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseTools;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependecySynchronizer implements ModuleComponent, JDOMExternalizable {

    Module module;
    LibraryHelper libraryHelper = new LibraryHelper();
    ConfigurationHelper configurationHelper = new ConfigurationHelper(this);
    RegistrationHelper registrationHelper = new RegistrationHelper(this);

    Map<String, RegistrationHelper.Registration> loadedListeners = new HashMap<String, RegistrationHelper.Registration>();

    public DependecySynchronizer(Module module) {
        this.module = module;
    }

    public void projectOpened() {
        for (Map.Entry<String, RegistrationHelper.Registration> e : loadedListeners.entrySet()) {
            try {
                VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(e.getKey());
                registrationHelper.registerClasspathFileModificationListener(file, module, e.getValue().libraryName);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void projectClosed() {
        registrationHelper.unregisterAllListeners();
    }

    private static final String ECLIPSE_DEPENDENCIES_SUFFIX = "-eclipse_dependencies";

    public void stopTracingChanges(final Module currentModule, VirtualFile file) {
        RegistrationHelper.Registration registration = registrationHelper.unregisterFileSystemListener(file);
        libraryHelper.removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(currentModule, registration.libraryName);
    }

    public void traceChanges(Module currentModule, VirtualFile classpathVirtualFile) {

        if (currentModule == null) {
            Messages.showWarningDialog("Please open any project.", "No open projects");
            return;
        }

        String libraryName = Messages.showInputDialog("Please enter library name.", "Creating library for Eclipse dependencies", Messages.getQuestionIcon(), computeEclipseDependenciesLibraryProposedName(currentModule), null);
        registrationHelper.registerClasspathFileModificationListener(classpathVirtualFile, currentModule, libraryName);

        Library.ModifiableModel model = refreshEclipseDependencies(classpathVirtualFile);
        displayInformationDialog(model.getUrls(OrderRootType.CLASSES));
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
        String classpathFilePath = classpathVirtualFile.getPath();
        final String classpathFileDir = classpathVirtualFile.getParent().getPath();
        final List<String> libs = EclipseTools.parseEclipseClassPath(classpathFilePath);

        String libraryName = registrationHelper.getRegistration(classpathVirtualFile).libraryName;
        final Library newLibrary = getOrCreateEclipseDependenciesLibrary(module, libraryName);
        final Library.ModifiableModel libraryModel = newLibrary.getModifiableModel();

        getApplication().runWriteAction(new Runnable() {
            public void run() {
                libraryHelper.clearLibrary(libraryModel);
                libraryHelper.addJarsToLibrary(classpathFileDir, libs, libraryModel);
                libraryModel.commit();

                libraryHelper.addLibraryDependencyToModule(newLibrary, DependecySynchronizer.this.module);
            }
        });

        return libraryModel;
    }

    private Library getOrCreateEclipseDependenciesLibrary(Module currentModule, final String libraryName) {
        final LibraryTable libraryTable = libraryHelper.getLibraryTable(currentModule);
        final Library[] eclipseDepsLibrary = new Library[]{libraryTable.getLibraryByName(libraryName)};
        if (eclipseDepsLibrary[0] == null) {
            getApplication().runWriteAction(new Runnable() {
                public void run() {
                    eclipseDepsLibrary[0] = libraryTable.createLibrary(libraryName);
                }
            });
        }

        return eclipseDepsLibrary[0];
    }

    private String computeEclipseDependenciesLibraryProposedName(Module currentModule) {
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
}
