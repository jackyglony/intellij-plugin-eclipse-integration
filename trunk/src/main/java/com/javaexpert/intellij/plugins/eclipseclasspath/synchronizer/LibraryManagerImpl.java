package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.Computable;
import com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.EclipseClasspathEntry;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain.IdeaLibrary;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain.IdeaLibraryImpl;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain.IdeaModule;
import com.javaexpert.intellij.plugins.support.AbstractModuleComponent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LibraryManagerImpl extends AbstractModuleComponent implements LibraryManager {
    IdeaModule ideaModule;
    private Application application;
    private LibraryTable libraryTable;


    public LibraryManagerImpl(IdeaModule ideaModule, Application application, LibraryTable libraryTable) {
        this.ideaModule = ideaModule;
        this.application = application;
        this.libraryTable = libraryTable;
    }

    public void removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(String libraryName) {
        final Library eclipseDepsLibrary = libraryTable.getLibraryByName(libraryName);

        if (eclipseDepsLibrary != null) {
            application.runWriteAction(new Runnable() {
                public void run() {
                    ideaModule.removeDependency(eclipseDepsLibrary);
                    deleteLibrary(eclipseDepsLibrary);
                }
            });
        }
    }

    void deleteLibrary(Library eclipseDepsLibrary) {
        libraryTable.removeLibrary(eclipseDepsLibrary);
    }


    public IdeaLibrary createLibrary(final String libraryName) {
        Library res;
        res = application.runWriteAction(new Computable<Library>() {
            public Library compute() {
                return libraryTable.createLibrary(libraryName);
            }
        });
        return newIdeaLibrary(res);
    }

    private IdeaLibrary newIdeaLibrary(Library library) {
        assert library != null;
        return new IdeaLibraryImpl(library, application);
    }

    public IdeaLibrary findLibrary(String libraryName) {
        Library lib = libraryTable.getLibraryByName(libraryName);
        if (lib == null) return null;
        return newIdeaLibrary(lib);
    }

    IdeaLibrary findOrCreateLibrary(String libraryName) {
        IdeaLibrary lib = findLibrary(libraryName);
        if (lib == null) {
            lib = createLibrary(libraryName);
        }
        return lib;
    }

    public IdeaLibrary createOrRefreshLibraryWithJars(String libraryName, List<EclipseClasspathEntry> jars, String jarsBasePath) {
        IdeaLibrary lib = findOrCreateLibrary(libraryName);
        lib.repopulateEntries(jars, jarsBasePath);
        ideaModule.makeDependentOn(lib);
        return lib;
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "LibraryManager";
    }
}
