package com.javaexpert.intellij.plugins.eclipseclasspath.eclipse;

/**
 * User: piotrga
 * Date: 2007-03-07
 * Time: 22:35:27
 */
public class EclipseClasspathEntry {
    public enum Kind {
        LIB, VAR
    }

    private String path;
    private Kind kind;
    private String sourcePath;
    private String javadocPath;


    public EclipseClasspathEntry(Kind kind, String path) {
        this.path = path;
        this.kind = kind;
    }

    public EclipseClasspathEntry(String kind) {
        setKind(kind);
    }

    public String path() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Kind kind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = Kind.valueOf(kind.toUpperCase());
    }

    public String sourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }


    public String javadocPath() {
        return javadocPath;
    }

    public void setJavadocPath(String javadocPath) {
        this.javadocPath = javadocPath;
    }
}
