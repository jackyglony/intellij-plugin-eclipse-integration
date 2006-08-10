package com.javaexpert.intellij.plugins.eclipseclasspath;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlToolsTest extends TestCase {

    public void test() {

        Document doc = XmlTools.parseXmlFile(this.getClass().getResource("test.xml").getPath(), false);
        Element documentElement = doc.getDocumentElement();

        assertNotNull(documentElement);

        assertEquals("idea-plugin", documentElement.getNodeName());

        assertEquals(15, documentElement.getChildNodes().getLength());

        assertEquals("5581", documentElement.getChildNodes().item(9).getAttributes().getNamedItem("since-build").getNodeValue());

    }
}
