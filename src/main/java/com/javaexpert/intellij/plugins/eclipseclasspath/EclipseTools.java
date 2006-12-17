package com.javaexpert.intellij.plugins.eclipseclasspath;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class EclipseTools {
    public List<String> extractJarsFromEclipseDotClasspathFile(String filename) {
        Document document = XmlTools.parseXmlFile(filename, false);

        NodeList list = document.getDocumentElement().getChildNodes();
        List<String> libs = new ArrayList<String>();
        for (int i = 0; i < list.getLength(); i++) {

            Node node = list.item(i);
            NamedNodeMap attributes = node.getAttributes();
            if (node.getNodeName().equalsIgnoreCase("classpathentry") && attributes != null) {
                if (attributes.getNamedItem("kind").getNodeValue().equalsIgnoreCase("lib")) {
                    String nodeValue = attributes.getNamedItem("path").getNodeValue();
                    if (nodeValue != null)
                        libs.add(nodeValue);
                }
            }
        }
        return libs;
    }
}
