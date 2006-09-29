package com.javaexpert.intellij.plugins.eclipseclasspath;

import junit.framework.TestCase;

import java.util.List;

/**
 * User: piotrga
 * Date: 2006-08-10
 * Time: 07:28:39
 */
public class EclipseToolsTest extends TestCase {
    public void testPersesEclipseClasspath() {
        List<String> res = EclipseTools.extractJarsFromEclipseDotClasspathFile(this.getClass().getResource(".classpath").getPath());
        assertEquals(res.size(), 10);
        assertTrue(res.contains("lib/cglib-nodep-2.1_3.jar"));
        assertTrue(!res.contains("uimocks"));
        assertTrue(!res.contains("bin"));
        assertTrue(!res.contains("org.eclipse.jdt.launching.JRE_CONTAINER"));
    }
}
