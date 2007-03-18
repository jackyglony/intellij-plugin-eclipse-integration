package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.EclipseClasspathEntry;
import com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.VarEclipseClasspathEntry;
import org.jmock.util.NotImplementedException;

import java.util.List;

/**
 * User: piotrga
 * Date: 2007-03-18
 * Time: 12:15:26
 */
public class IdeaLibraryImpl implements IdeaLibrary {
    Library library;
    Application application;


    public IdeaLibraryImpl(Library library, Application application) {
        this.library = library;
        this.application = application;
    }

    public void repopulateEntries(final List<EclipseClasspathEntry> libs, final String libsBaseDir) {
        //noinspection ConstantConditions
        final Library.ModifiableModel libraryModel = library.getModifiableModel();

        application.runWriteAction(new Runnable() {
            public void run() {
                clear(libraryModel);
                addJars(libraryModel, libs, libsBaseDir);
                libraryModel.commit();
            }
        });
    }

    void clear(Library.ModifiableModel model) {
        for (String url : model.getUrls(OrderRootType.CLASSES)) {
            model.removeRoot(url, OrderRootType.CLASSES);
        }
    }

    void addJars(Library.ModifiableModel model, List<EclipseClasspathEntry> jars, String baseDirectory) {
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

    String transformToIntelliJLocation(EclipseClasspathEntry entry, String baseDirectory) {
        if (entry.kind() == EclipseClasspathEntry.Kind.LIB) {
            if (entry.path().startsWith("/")) return String.format("%s/..%s", baseDirectory, entry.path());
            if (isUrl(entry.path())) return String.format("%s", entry.path());
            return String.format("%s/%s", baseDirectory, entry.path());
        } else if (entry.kind() == EclipseClasspathEntry.Kind.VAR) {
            String var = ((VarEclipseClasspathEntry) entry).variableName();
            return String.format("%s", entry.path().replaceFirst(var, "\\$" + var + "\\$"));
        }
        throw new NotImplementedException("Not implemented entry kind " + entry.kind());
    }

    boolean isUrl(String lib) {
        return lib.matches("[a-zA-Z]+:[/\\\\].+");
    }

    public Library nativeLib() {
        return library;
    }
}
