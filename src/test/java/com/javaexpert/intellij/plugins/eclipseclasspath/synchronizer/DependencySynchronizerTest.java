package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.module.Module;
import com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.EclipseClasspathFile;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain.IdeaLibrary;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain.Registry;
import net.sf.jdummy.JDummyTestCase;
import org.jmock.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    private static final String FILENAME = "filename";

    @BeforeMethod
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

    @AfterMethod
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testTraceChangesUserApprovesLibraryName() {
        expectFullRegistrationToBeDone();
        dependencySynchronizer.traceChanges(file);
    }

    @Test
    public void testIsFileTraced() {
        registry.expects(once())
                .method("isFileRegistered")
                .with(eq(FILENAME))
                .will(returnValue(true));
        assert dependencySynchronizer.isFileTraced(FILENAME);
    }

    @Test
    public void testIsFileTracedWhenNotTraced() {
        registry.expects(once())
                .method("isFileRegistered")
                .with(eq(FILENAME))
                .will(returnValue(false));
        assert !(dependencySynchronizer.isFileTraced(FILENAME));
    }

    private void expectFullRegistrationToBeDone() {
        List<String> jars = EMPTY_LIST;
        String libraryName = "libX";
        String path = "/some/path/createsLibraryWhenDoesntExist.yz";

        fileMock.stubs()
                .method("getFileName")
                .will(returnValue(path));
        fileMock.stubs()
                .method("getDir")
                .will(returnValue("/some/path"));
        fileMock.stubs()
                .method("usedPathVariables")
                .will(returnValue(Collections.EMPTY_SET));
        registry.stubs().method("getLibraryName")
                .with(eq(path))
                .will(returnValue(libraryName));

        ui.expects(once())
                .method("getLibraryNameFromUser")
                .will(returnValue(libraryName));
        fileMock.expects(atLeastOnce())
                .method("getClasspathEntries")
                .will(returnValue(jars));
        registry.expects(once())
                .method("registerClasspathFileModificationListener")
                .with(eq(libraryName), ANYTHING, ANYTHING, eq(path));
        libraryHelper.expects(once())
                .method("createOrRefreshLibraryWithJars")
                .with(eq(libraryName), eq(jars), ANYTHING)
                .will(returnValue(mimicWithDummyValues(IdeaLibrary.class)));
        ui.expects(once())
                .method("displayInformationDialog");
        file = (EclipseClasspathFile) fileMock.proxy();
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
        dependencySynchronizer.stopTracingChanges("lib/Path/.classpath");
    }
}
