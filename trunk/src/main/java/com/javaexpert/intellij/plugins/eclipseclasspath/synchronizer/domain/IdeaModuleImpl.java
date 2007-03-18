package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;

/**
 * User: piotrga
 * Date: 2007-03-18
 * Time: 11:46:38
 */
public class IdeaModuleImpl implements IdeaModule {
    private Application application;
    private Module ideaModule;
    private LibraryTablesRegistrar libraryTablesRegistrar;


    public IdeaModuleImpl(Application application, Module ideaModule) {
        this.application = application;
        this.ideaModule = ideaModule;
    }

    public void makeDependentOn(final IdeaLibrary lib) {
        application.runWriteAction(new Runnable() {
            public void run() {
                ModifiableRootModel moduleModel = modifiableModuleModel();
                if (moduleModel.findLibraryOrderEntry(lib.nativeLib()) == null) {
                    moduleModel.addLibraryEntry(lib.nativeLib());
                    moduleModel.commit();
                }
            }
        });
    }

    ModuleRootManager moduleRootManager() {
        return ModuleRootManager.getInstance(ideaModule);
    }

    ModifiableRootModel modifiableModuleModel() {
        return moduleRootManager().getModifiableModel();
    }

    public void removeDependency(Library library) {
        ModifiableRootModel moduleModel = modifiableModuleModel();

        LibraryOrderEntry libraryReference = moduleModel.findLibraryOrderEntry(library);
        if (libraryReference != null) {
            moduleModel.removeOrderEntry(libraryReference);
            moduleModel.commit();
        }
    }
}
