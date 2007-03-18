package com.javaexpert.intellij.plugins.eclipseclasspath.eclipse;

import com.intellij.util.containers.ArrayListSet;
import com.javaexpert.intellij.plugins.support.XmlTools;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EclipseClasspathFile {
    private String fileName;


    public EclipseClasspathFile(String filename) {
        this.fileName = filename;
    }


    EclipseClasspathFile() {
    }

    public List<EclipseClasspathEntry> getClasspathEntries() {
        Document document = parseClasspathFile();

        NodeList list = document.getDocumentElement().getChildNodes();
        List<EclipseClasspathEntry> libs = new ArrayList<EclipseClasspathEntry>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            NamedNodeMap attributes = node.getAttributes();
            if (node.getNodeName().equalsIgnoreCase("classpathentry") && attributes != null) {
                String kind = attributes.getNamedItem("kind").getNodeValue();

                EclipseClasspathEntry entry = createClasspathEntry(kind);

                if (entry != null) {
                    entry.setPath(attributes.getNamedItem("path").getNodeValue());
                    if (attributes.getNamedItem("sourcepath") != null)
                        entry.setSourcePath(attributes.getNamedItem("sourcepath").getNodeValue());
                    if (entry.path() != null) libs.add(entry);
                }
            }
        }
        return libs;
    }

    private static EclipseClasspathEntry createClasspathEntry(String kind) {
        EclipseClasspathEntry entry = null;
        if (kind.equalsIgnoreCase("lib")) entry = new EclipseClasspathEntry("lib");
        if (kind.equalsIgnoreCase("var")) entry = new VarEclipseClasspathEntry();
        return entry;
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

    public Set<String> usedPathVariables() {
        Set<String> vars = new ArrayListSet<String>();
        for (EclipseClasspathEntry e : getClasspathEntries())
            if (e.kind() == EclipseClasspathEntry.Kind.VAR) {
                vars.add(((VarEclipseClasspathEntry) e).variableName());
            }
        return vars;
    }
}
