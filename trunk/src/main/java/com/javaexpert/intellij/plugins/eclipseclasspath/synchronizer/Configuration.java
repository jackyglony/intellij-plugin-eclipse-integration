package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import org.jdom.Element;

import java.util.HashMap;
import java.util.Map;

class Configuration {
    private Map<String, Registry.Registration> loadedListeners = new HashMap<String, Registry.Registration>();


    void readExternal(Element element) {
        for (Object o : element.getChildren()) {
            Element e = (Element) o;
            getLoadedListeners().put(e.getAttributeValue("tracedFile"), new Registry.Registration(null, e.getAttributeValue("module"), e.getAttributeValue("library")));
        }
    }

    public void writeExternal(Element element, Map<String, Registry.Registration> activeListeners) {
        for (String url : activeListeners.keySet()) {
            Element element1 = new Element("eclipse-dependency");
            element1.setAttribute("tracedFile", url);
            element1.setAttribute("module", activeListeners.get(url).moduleName);
            element1.setAttribute("library", activeListeners.get(url).libraryName);
            element.addContent(element1);
        }
    }

    Map<String, Registry.Registration> getLoadedListeners() {
        return loadedListeners;
    }
}
