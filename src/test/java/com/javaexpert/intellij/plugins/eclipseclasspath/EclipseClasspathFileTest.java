package com.javaexpert.intellij.plugins.eclipseclasspath;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * User: piotrga
 * Date: 2006-08-10
 * Time: 07:28:39
 */
public class EclipseClasspathFileTest extends TestCase {
    public void testPersesEclipseClasspath() {
        List<EclipseClasspathEntry> cp = new EclipseClasspathFile(this.getClass().getResource(".classpath").getPath()).getClasspathEntries();
        List<String> res = new ArrayList<String>();
        for (EclipseClasspathEntry e : cp) res.add(e.path());
        assertEquals(res.size(), 10);
        assertTrue(res.contains("lib/cglib-nodep-2.1_3.jar"));
        assertTrue(!res.contains("uimocks"));
        assertTrue(!res.contains("bin"));
        assertTrue(!res.contains("org.eclipse.jdt.launching.JRE_CONTAINER"));
    }

    public void speedTest() {
        String s = "";
        double j = 1.111111111;
        for (int i = 0; i < 20 * 1000 * 1 * 1; i++) {
            s += "12345";
            j = j * j * j * j * j * j + j + j + j + j + j;
        }
        s = "";
        System.out.println(s + j);
    }
}
