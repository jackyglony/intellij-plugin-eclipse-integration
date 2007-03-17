package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.libraries.Library;
import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseClasspathFile;
import net.sf.jdummy.JDummyTestCase;
import org.jmock.Mock;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import static java.util.Collections.EMPTY_LIST;
import java.util.List;


/**
 * User: piotrga
 * Date: 2006-12-06
 * Time: 11:10:17
 */
public class DependencySynchronizerTest extends JDummyTestCase {
    private DependencySynchronizer dependencySynchronizer;
    private Mock libraryHelper;
    private Mock ui;
    private Mock fileMock;
    private EclipseClasspathFile file;
    private Mock registry;

    @Before
    public void setUp() throws Exception {

        super.setUp();
        Module module = (Module) mimicWithDummyValues(Module.class);
        libraryHelper = mock(LibraryManager.class);
        ui = mock(UI.class);
        registry = mock(Registry.class);
        fileMock = mock(EclipseClasspathFile.class);

        dependencySynchronizer = new DependencySynchronizerImpl(module
                , (LibraryManager) libraryHelper.proxy()
                , (Registry) registry.proxy()
                , (UI) ui.proxy());

    }

    @Test
    public void testTraceChangesUserApprovesLibraryName() {
        expectFullRegistrationToBeDone();
        dependencySynchronizer.traceChanges(file);
    }

    @Test
    public void testIsFileTraced() {
        expectFullRegistrationToBeDone();
        dependencySynchronizer.traceChanges(file);
        registry.expects(once())
                .method("isFileRegistered")
                .with(eq(file.getFileName()))
                .will(returnValue(true));
        assertTrue(dependencySynchronizer.isFileTraced(file.getFileName()));
    }

    @Test
    public void testIsFileTracedWhenNotTraced() {
        registry.expects(once())
                .method("isFileRegistered")
                .with(eq("fileName"))
                .will(returnValue(false));
        assertFalse(dependencySynchronizer.isFileTraced("fileName"));
    }

    private void expectFullRegistrationToBeDone() {
        List<String> jars = EMPTY_LIST;
        String libraryName = "libX";
        String path = "/some/path/x.yz";

        ui.expects(once())
                .method("getLibraryNameFromUser")
                .will(returnValue(libraryName));
        fileMock.expects(atLeastOnce())
                .method("getFileName")
                .will(returnValue(path));
        fileMock.expects(once())
                .method("getClasspathEntries")
                .will(returnValue(jars));
        fileMock.stubs()
                .method("getDir")
                .will(returnValue("/some/path"));
        fileMock.stubs()
                .method("usedPathVariables")
                .will(returnValue(Collections.EMPTY_SET));
        file = (EclipseClasspathFile) fileMock.proxy();
        registry.expects(once())
                .method("registerClasspathFileModificationListener")
                .with(eq(libraryName), ANYTHING, ANYTHING, eq(path));
        registry.stubs().method("getLibraryName")
                .with(eq(path))
                .will(returnValue(libraryName));
        libraryHelper.expects(once())
                .method("createOrRefreshLibraryWithJars")
                .with(eq(jars), eq(libraryName), ANYTHING)
                .will(returnValue(mimicWithDummyValues(Library.class)));
        ui.expects(once())
                .method("displayInformationDialog");
    }

    @Test
    public void testTraceChangesWhenUserCancelsLibraryName() {
        ui.expects(once())
                .method("getLibraryNameFromUser")
                .will(returnValue(null));

        dependencySynchronizer.traceChanges(file);
    }


    @Test
    public void testStopTracingChanges() {
        registry.stubs()
                .method("getLibraryName")
                .will(returnValue("lib"));
        registry.expects(once())
                .method("unregisterFileSystemListener");
        libraryHelper.expects(once())
                .method("removeDependencyBetweenModuleAndLibraryAndDeleteLibrary")
                .with(eq("lib"));
        dependencySynchronizer.stopTracingChanges("anything");
    }
}
