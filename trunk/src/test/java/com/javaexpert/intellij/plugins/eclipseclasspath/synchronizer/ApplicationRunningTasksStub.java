package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.util.Computable;

/**
 * User: piotrga
 * Date: 2007-03-18
 * Time: 21:12:38
 */
public class ApplicationRunningTasksStub extends ApplicationClassHack {

    public void runWriteAction(Runnable action) {
        action.run();
    }

    public <T> T runWriteAction(Computable<T> computation) {
        return computation.compute();
    }
}
