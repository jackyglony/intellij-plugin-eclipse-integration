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

            String location = transformClasspathToInteliJLocation(entry, baseDirectory);
            location = addProtocolToClasspathLocation(location);

            model.addRoot(location, CLASSES);


            if (entry.sourcePath() != null)
                model.addRoot(transformOtherPathToIntelliJLocation(entry.sourcePath(), baseDirectory), SOURCES);
            if (entry.javadocPath() != null)
                model.addRoot(transformOtherPathToIntelliJLocation(entry.javadocPath(), baseDirectory), JAVADOC);
        }
    }

    private String transformOtherPathToIntelliJLocation(String s, String baseDirectory) {
        String res = buildPath(s, baseDirectory);
        return addFileProtocolIfNotUrl(res);
    }

    private String addProtocolToClasspathLocation(String location) {
        if (location.toLowerCase().endsWith(".jar")) {
            location = "jar://" + location + "!/";
        } else {
            location = addFileProtocolIfNotUrl(location);
        }
        return location;
    }

    private String addFileProtocolIfNotUrl(String location) {
        if (!isUrl(location)) location = "file://" + location;
        return location;
    }

    String transformClasspathToInteliJLocation(EclipseClasspathEntry entry, String baseDirectory) {
        if (entry.kind() == EclipseClasspathEntry.Kind.LIB) {
            return buildPath(entry.path(), baseDirectory);
        } else if (entry.kind() == EclipseClasspathEntry.Kind.VAR) {
            String var = ((VarEclipseClasspathEntry) entry).variableName();
            return entry.path().replaceFirst(var, "\\$" + var + "\\$");
        }
        throw new NotImplementedException("Not implemented entry kind " + entry.kind());
    }

    private String buildPath(String path, String baseDirectory) {
        if (path.startsWith("/") || isUrl(path)) return path;
        return baseDirectory + "/" + path;
    }

    boolean isUrl(String lib) {
        return lib.matches("[a-zA-Z]+:[/\\\\].+");
    }

    public Library nativeLib() {
        return library;
    }
}
