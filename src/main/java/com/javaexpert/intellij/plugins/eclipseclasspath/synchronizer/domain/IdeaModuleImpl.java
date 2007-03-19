package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.javaexpert.intellij.plugins.support.AbstractModuleComponent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * User: piotrga
 * Date: 2007-03-18
 * Time: 11:46:38
 */
public class IdeaModuleImpl extends AbstractModuleComponent implements IdeaModule {
    private Application application;
    private ModuleRootManager moduleRootManager;


    public IdeaModuleImpl(Application application, ModuleRootManager moduleRootManager) {
        this.moduleRootManager = moduleRootManager;
        this.application = application;
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


    ModifiableRootModel modifiableModuleModel() {
        return moduleRootManager.getModifiableModel();
    }

    public void removeDependency(Library library) {
        ModifiableRootModel moduleModel = modifiableModuleModel();

        LibraryOrderEntry libraryReference = moduleModel.findLibraryOrderEntry(library);
        if (libraryReference != null) {
            moduleModel.removeOrderEntry(libraryReference);
            moduleModel.commit();
        }
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "IdeaModule";
    }
}
