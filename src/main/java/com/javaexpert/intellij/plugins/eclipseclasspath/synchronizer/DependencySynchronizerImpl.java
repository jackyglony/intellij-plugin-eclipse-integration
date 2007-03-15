package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.util.containers.ArrayListSet;
import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseClasspathEntry;
import static com.javaexpert.intellij.plugins.eclipseclasspath.EclipseClasspathEntry.Kind.VAR;
import com.javaexpert.intellij.plugins.eclipseclasspath.EclipseClasspathFile;
import com.javaexpert.intellij.plugins.eclipseclasspath.VarEclipseClasspathEntry;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.Registry.Registration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DependencySynchronizerImpl implements ModuleComponent, JDOMExternalizable, DependencySynchronizer {
    private static final String ECLIPSE_DEPENDENCIES_SUFFIX = "-eclipse_dependencies";

    private Module module;
    private LibraryHelper libraryHelper;
    private Configuration configuration;
    private Registry registry;
    private UI ui;

    public DependencySynchronizerImpl(Module module) {
        this.setModule(module);
        initComponent();
    }

    public void projectOpened() {
        registerLoadedListeners();
    }

    public void projectClosed() {
        registry.unregisterAllListeners();
    }

    @NotNull
    public String getComponentName() {
        return "DependencySynchronizer";
    }

    public void initComponent() {
        setLibraryHelper(new LibraryHelper(module));
        setRegistry(new Registry());
        setUi(new UI());
    }

    public void stopTracingChanges(String fileName) {
        String libraryName = registry.getLibraryName(fileName);
        registry.unregisterFileSystemListener(fileName);
        libraryHelper.removeDependencyBetweenModuleAndLibraryAndDeleteLibrary(libraryName);
    }

    public void traceChanges(EclipseClasspathFile eclipseClasspathFile) {
        if (module == null) {
            ui.displayNoProjectSelectedWarnning();
            return;
        }

        String libraryName = ui.getLibraryNameFromUser(module.getProject(), computeEclipseDependenciesLibraryDefaultName(module));
        if (libraryName == null) return;

        registerListener(eclipseClasspathFile, module, libraryName);
        detectedClasspathChanges(eclipseClasspathFile);
    }

    public boolean isFileTraced(String fileName) {
        return registry.isFileRegistered(fileName);
    }

    private void registerLoadedListeners() {
        for (Map.Entry<String, Registration> e : configuration.getLoadedListeners().entrySet()) {
            try {
                registerListener(new EclipseClasspathFile(e.getKey()), module, e.getValue().libraryName);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void registerListener(EclipseClasspathFile eclipseClasspathFile, Module currentModule, String libraryName) {
        registry.registerClasspathFileModificationListener(libraryName, new ClasspathFileModificationListener(eclipseClasspathFile), currentModule.getName(), eclipseClasspathFile.getFileName());
    }


    private Library syncDependencies(EclipseClasspathFile eclipseClasspathFile) {
        List<EclipseClasspathEntry> jars = eclipseClasspathFile.getClasspathEntries();
        return libraryHelper.createOrRefreshLibraryWithJars(jars, registry.getLibraryName(eclipseClasspathFile.getFileName()), eclipseClasspathFile.getDir());
    }

    private String computeEclipseDependenciesLibraryDefaultName(Module currentModule) {
        return currentModule.getName() + ECLIPSE_DEPENDENCIES_SUFFIX;
    }

    public void readExternal(Element element) throws InvalidDataException {
        setConfiguration(new Configuration());
        configuration.readExternal(element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        configuration.writeExternal(element, registry.getRegistrations());
    }

    public void disposeComponent() {
        // do nothing
    }

    public void moduleAdded() {
        // do noting
    }

    private void detectedClasspathChanges(EclipseClasspathFile classpathFile) {
        Library library = syncDependencies(classpathFile);
        Set<String> vars = new ArrayListSet<String>();
        for (EclipseClasspathEntry e : classpathFile.getClasspathEntries())
            if (e.kind() == VAR) {
                vars.add(((VarEclipseClasspathEntry) e).variableName());
            }

        ui.displayInformationDialog(library.getUrls(OrderRootType.CLASSES), vars);
    }

    protected void setModule(Module module) {
        this.module = module;
    }

    protected void setLibraryHelper(LibraryHelper libraryHelper) {
        this.libraryHelper = libraryHelper;
    }

    protected void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    protected void setRegistry(Registry registry) {
        this.registry = registry;
    }

    protected void setUi(UI ui) {
        this.ui = ui;
    }

    protected class ClasspathFileModificationListener extends VirtualFileAdapter {
        private final EclipseClasspathFile eclipseClasspathFile;


        public ClasspathFileModificationListener(EclipseClasspathFile eclipseClasspathFile) {
            this.eclipseClasspathFile = eclipseClasspathFile;
        }

        public void contentsChanged(VirtualFileEvent event) {
            if (eclipseClasspathFile.hasPath(event.getFile().getPath())) {
                detectedClasspathChanges(eclipseClasspathFile);
            }
        }
    }
}
