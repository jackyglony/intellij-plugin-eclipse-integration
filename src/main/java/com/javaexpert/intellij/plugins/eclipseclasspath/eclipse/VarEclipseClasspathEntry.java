package com.javaexpert.intellij.plugins.eclipseclasspath.eclipse;

/**
 * User: piotrga
 * Date: 2007-03-14
 * Time: 23:52:48
 */
public class VarEclipseClasspathEntry extends EclipseClasspathEntry {

    public VarEclipseClasspathEntry(String path) {
        super(Kind.VAR.toString());
        setPath(path);
    }

    public VarEclipseClasspathEntry() {
        super(Kind.VAR.toString());
    }

    public String variableName() {
        return path().split("/")[0];
    }
}
