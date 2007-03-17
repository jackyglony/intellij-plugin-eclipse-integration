package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.application.Application;
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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jmock.util.NotImplementedException;

import java.util.List;

public class LibraryManagerImpl extends AbstractModuleComponent implements LibraryManager {
    private Module module;
    private Application application;
    private LibraryTablesRegistrar libraryTablesRegistrar;

    public LibraryManagerImpl(Module module, Application application, LibraryTablesRegistrar libraryTablesRegistrar) {
        this.module = module;
        this.application = application;
        this.libraryTablesRegistrar = libraryTablesRegistrar;
    }

    public void removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(String libraryName) {
        final Library eclipseDepsLibrary = libraryTable().getLibraryByName(libraryName);

        if (eclipseDepsLibrary != null) {
            application.runWriteAction(new Runnable() {
                public void run() {
                    removeDependencyBetweenModuleAndLibrary(modifiableModuleModel(), eclipseDepsLibrary);
                    deleteLibrary(libraryTable(), eclipseDepsLibrary);
                }
            });
        }
    }

    void deleteLibrary(LibraryTable moduleLibraryTable, Library eclipseDepsLibrary) {
        moduleLibraryTable.removeLibrary(eclipseDepsLibrary);
    }

    private LibraryTable libraryTable() {
        return libraryTablesRegistrar.getLibraryTable(module.getProject());
    }

    private ModuleRootManager moduleRootManager() {
        return ModuleRootManager.getInstance(module);
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

    public Library createLibrary(final String libraryName) {
        Library res;
        res = application.runWriteAction(new Computable<Library>() {
            public Library compute() {
                return libraryTable().createLibrary(libraryName);
            }
        });
        return res;
    }

    public Library findLibrary(String libraryName) {
        return libraryTable().getLibraryByName(libraryName);
    }

    void makeModuleDependentOnLibrary(final Library lib) {
        application.runWriteAction(new Runnable() {
            public void run() {
                ModifiableRootModel moduleModel = modifiableModuleModel();
                if (moduleModel.findLibraryOrderEntry(lib) == null) {
                    moduleModel.addLibraryEntry(lib);
                    moduleModel.commit();
                }
            }
        });
    }

    private ModifiableRootModel modifiableModuleModel() {
        return moduleRootManager().getModifiableModel();
    }

    void repopulateLibraryContent(final Library newLibrary, final List<EclipseClasspathEntry> libs, final String libsBaseDir) {
        //noinspection ConstantConditions
        final Library.ModifiableModel libraryModel = newLibrary.getModifiableModel();

        application.runWriteAction(new Runnable() {
            public void run() {
                clearLibrary(libraryModel);
                addJarsToLibrary(libraryModel, libs, libsBaseDir);
                libraryModel.commit();
            }
        });
    }

    Library findOrCreateLibrary(String libraryName) {
        Library lib = findLibrary(libraryName);
        if (lib == null) {
            lib = createLibrary(libraryName);
        }
        return lib;
    }

    public Library createOrRefreshLibraryWithJars(List<EclipseClasspathEntry> jars, String libraryName, String jarsBasePath) {
        Library lib = findOrCreateLibrary(libraryName);
        repopulateLibraryContent(lib, jars, jarsBasePath);
        makeModuleDependentOnLibrary(lib);
        return lib;
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "LibraryManager";
    }
}
