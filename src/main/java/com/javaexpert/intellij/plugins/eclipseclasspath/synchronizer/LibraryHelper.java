package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.util.Computable;

import java.util.List;

class LibraryHelper {

    public void removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(final Module currentModule, String libraryName) {
        final Library eclipseDepsLibrary = getLibraryTable(currentModule).getLibraryByName(libraryName);

        if (eclipseDepsLibrary != null) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    removeDependencyBetweenModuleAndLibrary(getModuleRootManager(currentModule).getModifiableModel(), eclipseDepsLibrary);
                    deleteLibrary(getLibraryTable(currentModule), eclipseDepsLibrary);
                }
            });
        }
    }

    void deleteLibrary(LibraryTable moduleLibraryTable, Library eclipseDepsLibrary) {
        moduleLibraryTable.removeLibrary(eclipseDepsLibrary);
    }

    LibraryTable getLibraryTable(Module currentModule) {
        return LibraryTablesRegistrar.getInstance().getLibraryTable(currentModule.getProject());
    }

    ModuleRootManager getModuleRootManager(Module currentModule) {
        return ModuleRootManager.getInstance(currentModule);
    }

    void clearLibrary(Library.ModifiableModel model) {
        for (String url : model.getUrls(OrderRootType.CLASSES)) {
            model.removeRoot(url, OrderRootType.CLASSES);
        }
    }

    void addJarsToLibrary(String baseDirectory, List<String> jars, Library.ModifiableModel model) {
        for (String jar : jars) {
            if (isAbsoulutePathOrUrl(jar)) {
                model.addRoot("jar://" + jar + "!/", OrderRootType.CLASSES);
            } else {
                model.addRoot("jar://" + baseDirectory + "/" + jar + "!/", OrderRootType.CLASSES);
            }
        }
    }

    boolean isAbsoulutePathOrUrl(String lib) {
        return lib.matches("[a-zA-Z]:[/\\\\].+") || lib.startsWith("/");
    }

    public void makeModuleDependentOnLibrary(Module module, Library newLibrary) {
        ModifiableRootModel moduleModel = getModuleRootManager(module).getModifiableModel();
        if (moduleModel.findLibraryOrderEntry(newLibrary) == null) {
            moduleModel.addLibraryEntry(newLibrary);
            moduleModel.commit();
        }
    }

    public void removeDependencyBetweenModuleAndLibrary(ModifiableRootModel moduleModel, Library library) {
        LibraryOrderEntry libraryReference = moduleModel.findLibraryOrderEntry(library);
        if (libraryReference != null) {
            moduleModel.removeOrderEntry(libraryReference);
            moduleModel.commit();
        }
    }

    public Library getOrCreateLibrary(Module currentModule, final String libraryName) {
        final LibraryTable libraryTable = getLibraryTable(currentModule);
        Library res = libraryTable.getLibraryByName(libraryName);

        if (res == null) {
            res = createLibrary(libraryTable, libraryName);
        }
        return res;
    }

    public Library createLibrary(final LibraryTable libraryTable, final String libraryName) {
        Library res;
        res = ApplicationManager.getApplication().runWriteAction(new Computable<Library>() {
            public Library compute() {
                return libraryTable.createLibrary(libraryName);
            }
        });
        return res;
    }

    void repopulateLibraryWithJars(Library.ModifiableModel libraryModel, String libsBaseDir, List<String> libs) {
        clearLibrary(libraryModel);
        addJarsToLibrary(libsBaseDir, libs, libraryModel);
        libraryModel.commit();
    }
}
