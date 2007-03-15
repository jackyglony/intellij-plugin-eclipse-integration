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
import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseClasspathEntry;
import static com.javaexpert.intellij.plugins.eclipseclasspath.EclipseClasspathEntry.Kind.LIB;
import static com.javaexpert.intellij.plugins.eclipseclasspath.EclipseClasspathEntry.Kind.VAR;
import com.javaexpert.intellij.plugins.eclipseclasspath.VarEclipseClasspathEntry;
import org.jmock.util.NotImplementedException;

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

    void addJarsToLibrary(Library.ModifiableModel model, List<EclipseClasspathEntry> jars, String baseDirectory) {
        for (EclipseClasspathEntry entry : jars) {
//            VirtualFile f = VirtualFileManager.getInstance().findFileByUrl(transformToIntelliJLocation(entry, baseDirectory));

            String location = transformToIntelliJLocation(entry, baseDirectory);
            if (location.toLowerCase().endsWith(".jar")) {
                model.addRoot("jar://" + location + "!/", OrderRootType.CLASSES);
            } else {
                model.addRoot("file://" + location + "!/", OrderRootType.CLASSES);
            }

            if (entry.sourcePath() != null) model.addRoot(entry.sourcePath(), OrderRootType.SOURCES);
            if (entry.javadocPath() != null) model.addRoot(entry.javadocPath(), OrderRootType.JAVADOC);
        }
    }

    private String transformToIntelliJLocation(EclipseClasspathEntry entry, String baseDirectory) {
        if (entry.kind() == LIB) {
            if (entry.path().startsWith("/")) return String.format("%s/..%s", baseDirectory, entry.path());
            if (isUrl(entry.path())) return String.format("%s", entry.path());
            return String.format("%s/%s", baseDirectory, entry.path());
        } else if (entry.kind() == VAR) {
            String var = ((VarEclipseClasspathEntry) entry).variableName();
            return String.format("%s", entry.path().replaceFirst(var, "\\$" + var + "\\$"));
        }
        throw new NotImplementedException("Not implemented entry kind " + entry.kind());
    }
//    private String transformToIntelliJLocation(EclipseClasspathEntry entry, String baseDirectory) {
//        if (entry.kind() == LIB) {
//            if( entry.path().startsWith("/")) return String.format("jar://%s/../%s!/", baseDirectory, entry.path());
//            if (isUrl(entry.path())) return String.format("jar://%s!/", entry.path());
//            return String.format("jar://%s/%s!/", baseDirectory, entry.path());
//        }else if (entry.kind() == VAR){
//            String var = entry.path().split("/")[0];
//            return String.format("jar://%s!/", entry.path().replaceFirst(var,"\\$"+var+"\\$"));
//        }
//        throw new NotImplementedException("Not implemented entry kind "+entry.kind());
//    }

    boolean isUrl(String lib) {
        return lib.matches("[a-zA-Z]+:[/\\\\].+");
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

    void repopulateLibraryContent(final Library newLibrary, final List<EclipseClasspathEntry> libs, final String libsBaseDir) {
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

    public Library createOrRefreshLibraryWithJars(List<EclipseClasspathEntry> jars, String libraryName, String jarsBasePath) {
        Library lib = getOrCreateLibrary(libraryName);
        repopulateLibraryContent(lib, jars, jarsBasePath);
        makeModuleDependentOnLibrary(lib);
        return lib;
    }
}
