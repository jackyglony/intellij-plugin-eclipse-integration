package com.javaexpert.intellij.plugins.eclipseclasspath;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EclipseClasspathFile {
    private String fileName;


    public EclipseClasspathFile(String filename) {
        this.fileName = filename;
    }


    EclipseClasspathFile() {
    }

    public List<String> getJars() {
        Document document = parseClasspathFile();

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

    protected Document parseClasspathFile() {
        return XmlTools.parseXmlFile(fileName, false);
    }


    public String getFileName() {
        return fileName;
    }

    public String getDir() {
        return new File(fileName).getParent();
    }

    public boolean hasPath(String path) {
        try {
            return new File(fileName).getCanonicalPath().equalsIgnoreCase(new File(path).getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException("Problem comparing paths " + fileName + " and " + path);
        }
    }
}
