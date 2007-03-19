package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import com.intellij.openapi.application.Application;
import static com.intellij.openapi.roots.OrderRootType.CLASSES;
import com.intellij.openapi.roots.libraries.Library;
import com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.EclipseClasspathEntry;
import static com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.EclipseClasspathEntry.Kind.LIB;
import com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.VarEclipseClasspathEntry;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.ApplicationRunningTasksStub;
import net.sf.jdummy.JDummyTestCase;
import org.jmock.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * User: piotrga
 * Date: 2007-03-18
 * Time: 20:58:45
 */
public class IdeaLibraryTest extends JDummyTestCase {
    private Mock nativeLib;
    private Mock modifiableModel;
    private Application app;
    private IdeaLibrary lib;
    private List<EclipseClasspathEntry> list;
    private static final String BASE_DIR = "base/dir";

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        list = new ArrayList<EclipseClasspathEntry>();
        modifiableModel = mock(Library.ModifiableModel.class);
        nativeLib = mock(Library.class);
        nativeLib.stubs().method("getModifiableModel").will(returnValue(modifiableModel.proxy()));
        lib = new IdeaLibraryImpl(((Library) nativeLib.proxy()), new ApplicationRunningTasksStub());
    }


    @AfterMethod
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void addRelativeLibJarEntry() {
        testOneEntry("some/path.jar", LIB, "jar://base/dir/some/path.jar!/");
    }

    @Test
    public void addWorkspaceRelativeLibJarEntry() {
        testOneEntry("/some/path.jar", LIB, "jar://base/dir/../some/path.jar!/");
    }

    @Test
    public void addVar() {
        initStubs();

        list.add(new VarEclipseClasspathEntry("VARIABLE/some/path.jar"));
        modifiableModel.expects(once()).method("addRoot").with(eq("jar://$VARIABLE$/some/path.jar!/"), eq(CLASSES));

        this.lib.repopulateEntries(list, BASE_DIR);
    }

    @Test
    public void clearsLibBeforeAddingNewStuff() {
        modifiableModel.expects(once()).method("getUrls").will(returnValue(new String[]{"1", "2"}));
        modifiableModel.expects(exactly(2)).method("removeRoot").with(ANYTHING, ANYTHING).will(returnValue(true));
        modifiableModel.expects(once()).method("commit");
        lib.repopulateEntries(list, "");
    }

    private void testOneEntry(String path, EclipseClasspathEntry.Kind lib, String expectedPath) {
        initStubs();

        list.add(new EclipseClasspathEntry(lib, path));
        modifiableModel.expects(once()).method("addRoot").with(eq(expectedPath), eq(CLASSES));

        this.lib.repopulateEntries(list, BASE_DIR);
    }

    private void initStubs() {
        modifiableModel.stubs().method("getUrls").will(returnValue(new String[0]));
        modifiableModel.stubs().method("commit");
    }

}
