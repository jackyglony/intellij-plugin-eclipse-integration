package com.javaexpert.intellij.plugins.eclipseclasspath;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlToolsTest {

    @Test
    public void testParsesSimpleXmlFile() {

        Document doc = XmlTools.parseXmlFile(this.getClass().getResource("test.xml").getPath(), false);
        Element documentElement = doc.getDocumentElement();

        assert documentElement != null;
        Assert.assertEquals("idea-plugin", documentElement.getNodeName());
        Assert.assertEquals(15, documentElement.getChildNodes().getLength());
        Assert.assertEquals("5581", documentElement.getChildNodes().item(9).getAttributes().getNamedItem("since-build").getNodeValue());
    }
}
