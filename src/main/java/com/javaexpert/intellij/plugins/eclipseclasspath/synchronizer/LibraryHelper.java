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
    private Module module;


    LibraryHelper() {
    }

    public LibraryHelper(Module module) {
        this.module = module;
    }

    public void removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(String libraryName) {
        final Library eclipseDepsLibrary = getLibraryTable(module).getLibraryByName(libraryName);

        if (eclipseDepsLibrary != null) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    removeDependencyBetweenModuleAndLibrary(getModuleRootManager(module).getModifiableModel(), eclipseDepsLibrary);
                    deleteLibrary(getLibraryTable(module), eclipseDepsLibrary);
                }
            });
        }
    }

    void deleteLibrary(LibraryTable moduleLibraryTable, Library eclipseDepsLibrary) {
        moduleLibraryTable.removeLibrary(eclipseDepsLibrary);
    }

    static LibraryTable getLibraryTable(Module currentModule) {
        return LibraryTablesRegistrar.getInstance().getLibraryTable(currentModule.getProject());
    }

    static ModuleRootManager getModuleRootManager(Module currentModule) {
        return ModuleRootManager.getInstance(currentModule);
    }

    void clearLibrary(Library.ModifiableModel model) {
        for (String url : model.getUrls(OrderRootType.CLASSES)) {
            model.removeRoot(url, OrderRootType.CLASSES);
        }
    }

    void addJarsToLibrary(Library.ModifiableModel model, List<String> jars, String baseDirectory) {
        for (String jar : jars) {
            model.addRoot(convertJarPathToIntelliJLibraryUrl(jar, baseDirectory), OrderRootType.CLASSES);
        }
    }

    private String convertJarPathToIntelliJLibraryUrl(String jarPath, String baseDirectory) {
        if (isAbsoulutePathOrUrl(jarPath)) return String.format("jar://%s!/", jarPath);
        return String.format("jar://%s/%s!/", baseDirectory, jarPath);
    }

    boolean isAbsoulutePathOrUrl(String lib) {
        return lib.matches("[a-zA-Z]:[/\\\\].+") || lib.startsWith("/");
    }

    private void removeDependencyBetweenModuleAndLibrary(ModifiableRootModel moduleModel, Library library) {
        LibraryOrderEntry libraryReference = moduleModel.findLibraryOrderEntry(library);
        if (libraryReference != null) {
            moduleModel.removeOrderEntry(libraryReference);
            moduleModel.commit();
        }
    }

    public Library createLibrary(final String libraryName, final Module currentModule) {
        Library res;
        res = ApplicationManager.getApplication().runWriteAction(new Computable<Library>() {
            public Library compute() {
                return getLibraryTable(currentModule).createLibrary(libraryName);
            }
        });
        return res;
    }

    public Library getLibraryByName(String libraryName) {
        return getLibraryTable(module).getLibraryByName(libraryName);
    }

    void makeModuleDependentOnLibrary(final Library lib) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                ModifiableRootModel moduleModel = getModuleRootManager(module).getModifiableModel();
                if (moduleModel.findLibraryOrderEntry(lib) == null) {
                    moduleModel.addLibraryEntry(lib);
                    moduleModel.commit();
                }
            }
        });
    }

    void repopulateLibraryContent(final Library newLibrary, final List<String> libs, final String libsBaseDir) {
        //noinspection ConstantConditions
        final Library.ModifiableModel libraryModel = newLibrary.getModifiableModel();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                clearLibrary(libraryModel);
                addJarsToLibrary(libraryModel, libs, libsBaseDir);
                libraryModel.commit();
            }
        });
    }

    Library getOrCreateLibrary(String libraryName) {
        Library lib = getLibraryByName(libraryName);
        if (lib == null) {
            lib = createLibrary(libraryName, module);
        }
        return lib;
    }

    public Library createOrRefreshLibraryWithJars(List<String> jars, String libraryName, String jarsBasePath) {
        Library lib = getOrCreateLibrary(libraryName);
        repopulateLibraryContent(lib, jars, jarsBasePath);
        makeModuleDependentOnLibrary(lib);
        return lib;
    }
}
