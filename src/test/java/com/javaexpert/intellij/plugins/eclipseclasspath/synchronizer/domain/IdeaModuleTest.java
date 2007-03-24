package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import com.intellij.openapi.roots.ModuleRootManager;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.ApplicationRunningTasksStub;
import static testng.jmock.JMockNG.mock;
import testng.jmock.Mock2;

/**
 * User: piotrga
 * Date: 2007-03-20
 * Time: 09:13:32
 */
public class IdeaModuleTest {
    private Mock2<ModuleRootManager> mrm;
    private IdeaModule module;

    public void setUP() {
        mrm = mock(ModuleRootManager.class);
        module = new IdeaModuleImpl(new ApplicationRunningTasksStub(), mrm.proxy());

    }

//    @Test public void makeDependentOn(){
//        mrm.expects()
//        module.makeDependentOn(mock(IdeaLibrary.class).proxy());
//    }
}
