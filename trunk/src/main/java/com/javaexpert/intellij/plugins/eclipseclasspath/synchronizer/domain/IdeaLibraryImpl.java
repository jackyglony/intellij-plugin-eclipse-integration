package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import com.intellij.openapi.application.Application;
import static com.intellij.openapi.roots.OrderRootType.*;
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
        for (String url : model.getUrls(CLASSES)) {
            model.removeRoot(url, CLASSES);
        }
    }

    void addJars(Library.ModifiableModel model, List<EclipseClasspathEntry> jars, String baseDirectory) {
        for (EclipseClasspathEntry entry : jars) {
//            VirtualFile f = VirtualFileManager.getInstance().findFileByUrl(transformToIntelliJLocation(entry, baseDirectory));

            String location = transformToIntelliJLocation(entry, baseDirectory);
            if (location.toLowerCase().endsWith(".jar")) {
                model.addRoot("jar://" + location + "!/", CLASSES);
            } else {
                model.addRoot("file://" + location + "!/", CLASSES);
            }

            if (entry.sourcePath() != null) model.addRoot(buildPath(entry.sourcePath(), baseDirectory), SOURCES);
            if (entry.javadocPath() != null) model.addRoot(buildPath(entry.javadocPath(), baseDirectory), JAVADOC);
        }
    }

    String transformToIntelliJLocation(EclipseClasspathEntry entry, String baseDirectory) {
        if (entry.kind() == EclipseClasspathEntry.Kind.LIB) {
            return buildPath(entry.path(), baseDirectory);
        } else if (entry.kind() == EclipseClasspathEntry.Kind.VAR) {
            String var = ((VarEclipseClasspathEntry) entry).variableName();
            return entry.path().replaceFirst(var, "\\$" + var + "\\$");
        }
        throw new NotImplementedException("Not implemented entry kind " + entry.kind());
    }

    private String buildPath(String path, String baseDirectory) {
        if (path.startsWith("/")) return baseDirectory + "/.." + path;
        if (isUrl(path)) return path;
        return baseDirectory + "/" + path;
    }

    boolean isUrl(String lib) {
        return lib.matches("[a-zA-Z]+:[/\\\\].+");
    }

    public Library nativeLib() {
        return library;
    }
}
