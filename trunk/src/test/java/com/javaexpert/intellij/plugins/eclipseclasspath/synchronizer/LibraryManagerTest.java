package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.Computable;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain.IdeaLibrary;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain.IdeaModule;
import com.javaexpert.jdummy.JDummyCGTestCase;
import org.jmock.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

/**
 * User: piotrga
 * Date: 2007-03-17
 * Time: 09:53:20
 */
public class LibraryManagerTest extends JDummyCGTestCase {
    LibraryManager libraryManager;
    Mock libTable;
    Mock module;
    private Library dummyLibrary;

    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        dummyLibrary = (Library) mimicWithDummyValues(Library.class);
        libTable = mock(LibraryTable.class);
        module = mock(IdeaModule.class);
//        app = mock(ApplicationClassHack.class);
        libraryManager = new LibraryManagerImpl((IdeaModule) module.proxy()
                , new ApplcationRunningTasksStub()
//                , null
                , ((LibraryTable) libTable.proxy()));

    }

    @Override
    @AfterMethod
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void createsLibraryWhenDoesntExist() {
        libTable.expects(once()).method("getLibraryByName").will(returnValue(null));
        libTable.expects(once()).method("createLibrary").will(returnValue(dummyLibrary));
        module.stubs().method(ANYTHING);
        IdeaLibrary res = libraryManager.createOrRefreshLibraryWithJars("libName", Collections.EMPTY_LIST, "/some/path");
        assert res != null;
    }

    @Test
    public void createsNoLibraryWhenExists() {
        libTable.expects(once()).method("getLibraryByName").will(returnValue(dummyLibrary));
        libTable.expects(never()).method("createLibrary");
        module.stubs().method(ANYTHING);
        IdeaLibrary res = libraryManager.createOrRefreshLibraryWithJars("libName", Collections.EMPTY_LIST, "/some/path");
        assert res != null;
    }

    @Test
    public void makesModuleDependentOnLibrary() {
        libTable.stubs().method("getLibraryByName").will(returnValue(dummyLibrary));
        module.expects(atLeastOnce()).method("makeDependentOn");
        libraryManager.createOrRefreshLibraryWithJars("libName", Collections.EMPTY_LIST, "/some/path");
    }

    @Test
    public void removesDependency() {
        libTable.stubs().method("getLibraryByName").will(returnValue(dummyLibrary));
        libTable.stubs().method(ANYTHING);
        module.expects(atLeastOnce()).method("removeDependency");
        libraryManager.removeDependencyBetweenModuleAndLibraryAndDeleteLibrary("libName");
    }

    @Test
    public void deletesLibrary() {
        libTable.stubs().method("getLibraryByName").will(returnValue(dummyLibrary));
        libTable.expects(once()).method("removeLibrary");
        module.stubs().method(ANYTHING);
        libraryManager.removeDependencyBetweenModuleAndLibraryAndDeleteLibrary("libName");
    }

    private class ApplcationRunningTasksStub extends ApplicationClassHack {

        public void runWriteAction(Runnable action) {
            action.run();
        }

        public <T> T runWriteAction(Computable<T> computation) {
            return computation.compute();
        }
    }
}
