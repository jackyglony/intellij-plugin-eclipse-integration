package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseTools;
import net.sf.jdummy.JDummyTestCase;
import org.jmock.Mock;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;


/**
 * User: piotrga
 * Date: 2006-12-06
 * Time: 11:10:17
 */
public class DependencySynchronizerTest extends JDummyTestCase {
    private DependencySynchronizer dependencySynchronizer;
    private Module module;
    private VirtualFile file;
    private Mock libraryHelper;
    private Mock ui;
    private Mock eclipseTools;
    private Mock registry;

    @Before
    public void setUp() throws Exception {
        DependencySynchronizerImpl ds;

        super.setUp();
        module = (Module) mimicWithDummyValues(Module.class);
        file = (VirtualFile) mimicWithDummyValues(VirtualFile.class);
        libraryHelper = mock(LibraryHelper.class);
        ui = mock(UI.class);
        registry = mock(Registry.class);
        eclipseTools = mock(EclipseTools.class);

        ds = new DependencySynchronizerImpl(module);
        ds.setLibraryHelper((LibraryHelper) libraryHelper.proxy());
        ds.setUi((UI) ui.proxy());
        ds.setEclipseTools((EclipseTools) eclipseTools.proxy());
        ds.setRegistry((Registry) registry.proxy());
        dependencySynchronizer = ds;

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
        registry.expects(once()).method("isFileRegistered").with(eq(file)).will(returnValue(true));
        assertTrue(dependencySynchronizer.isFileTraced(file));
    }

    @Test
    public void testIsFileTracedWhenNotTraced() {
        registry.expects(once()).method("isFileRegistered").with(eq(file)).will(returnValue(false));
        assertFalse(dependencySynchronizer.isFileTraced(file));
    }

    private void expectFullRegistrationToBeDone() {
        List<String> jars = Collections.EMPTY_LIST;
        String libraryName = "libX";
        String path = "/some/path";

        ui.expects(once()).method("getLibraryNameFromUser").will(returnValue(libraryName));
        assertBehavior(file).expects(atLeastOnce()).method("getPath").will(returnValue(path));
        eclipseTools.expects(once()).method("extractJarsFromEclipseDotClasspathFile").with(eq(path)).will(returnValue(jars));
        registry.expects(once()).method("registerClasspathFileModificationListener").with(eq(file), eq(libraryName), ANYTHING, ANYTHING);
        registry.stubs().method("getLibraryName").with(eq(file)).will(returnValue(libraryName));
        libraryHelper.expects(once()).method("createOrRefreshLibraryWithJars").with(eq(jars), eq(libraryName), ANYTHING).will(returnValue(mimicWithDummyValues(Library.class)));
        ui.expects(once()).method("displayInformationDialog");
    }

    @Test
    public void testTraceChangesWhenUserCancelsLibraryName() {
        ui.expects(once()).method("getLibraryNameFromUser").will(returnValue(null));

        dependencySynchronizer.traceChanges(file);
    }


    @Test
    public void testStopTracingChanges() {
        registry.stubs().method("getLibraryName").will(returnValue("lib"));
        registry.expects(once()).method("unregisterFileSystemListener");
        libraryHelper.expects(once()).method("removeDependencyBetweenModuleAndLibraryAndDeleteLibrary").with(eq("lib"));
        ((DependencySynchronizer) dependencySynchronizer).stopTracingChanges(file);
    }
}
