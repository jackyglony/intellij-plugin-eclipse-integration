package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.domain;

import org.jdom.Element;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private Map<String, RegistryImpl.Registration> loadedListeners = new HashMap<String, RegistryImpl.Registration>();


    public void readExternal(Element element) {
        for (Object o : element.getChildren()) {
            Element e = (Element) o;
            getLoadedListeners().put(e.getAttributeValue("tracedFile"), new RegistryImpl.Registration(null, e.getAttributeValue("module"), e.getAttributeValue("library")));
        }
    }

    public void writeExternal(Element element, Map<String, RegistryImpl.Registration> activeListeners) {
        for (String url : activeListeners.keySet()) {
            Element element1 = new Element("eclipse-dependency");
            element1.setAttribute("tracedFile", url);
            element1.setAttribute("module", activeListeners.get(url).moduleName);
            element1.setAttribute("library", activeListeners.get(url).libraryName);
            element.addContent(element1);
        }
    }

    public Map<String, RegistryImpl.Registration> getLoadedListeners() {
        return loadedListeners;
    }
}
