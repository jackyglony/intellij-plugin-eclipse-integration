package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import com.intellij.openapi.roots.libraries.Library;

/**
 * User: piotrga
 * Date: 2007-03-18
 * Time: 12:54:36
 */
public interface IdeaModule {
    void makeDependentOn(IdeaLibrary lib);

    void removeDependency(Library library);
}
