package com.javaexpert.intellij.plugins.eclipseclasspath;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependecySynchronizer  implements ModuleComponent, JDOMExternalizable {

    Module module;


    public DependecySynchronizer(Module module) {
        this.module = module;
    }

    public String getComponentName() {
        return "DependecySynchronizer";
    }

    public void initComponent() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void disposeComponent() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    Map<String, Registration> activeListeners = new HashMap<String, Registration>();
    Map<String, Registration> loadedListeners = new HashMap<String, Registration>();

    public boolean isFileRegistered(VirtualFile file) {
        return activeListeners.containsKey(file.getUrl());
    }

    public void projectOpened() {
        for(Map.Entry<String,Registration> e: loadedListeners.entrySet()){
            try {
                VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(e.getKey());
//                Module module = ModuleManager.getInstance(myProject).findModuleByName(e.getValue().moduleName);
                registerClasspathFileModificationListener(file, module, e.getValue().libraryName);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void projectClosed() {
        for(Registration r: activeListeners.values())
            VirtualFileManager.getInstance().removeVirtualFileListener(r.listener);
    }

    public void moduleAdded() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    class Registration{
        public ClasspathFileModificationListener listener;
        public String moduleName;
        public String libraryName;

        public Registration(ClasspathFileModificationListener listener, String moduleName, String libraryName) {
            this.listener = listener;
            this.moduleName = moduleName;
            this.libraryName = libraryName;
        }
    }


    private static final String ECLIPSE_DEPENDENCIES_SUFFIX = "-eclipse_dependencies";


    void removeEclipseDependencyLibraryAndStopTracingChanges( final Module currentModule, VirtualFile file) {
        getVirtualFileManager().removeVirtualFileListener(activeListeners.remove(file.getUrl()).listener);
        final LibraryTable libraryTable = getLibraryTable(currentModule);
        final Library eclipseDepsLibrary = libraryTable.getLibraryByName(computeEclipseDependenciesLibraryProposedName(currentModule));

        if (eclipseDepsLibrary != null){
            final ModifiableRootModel model = getModuleRootManager(currentModule).getModifiableModel();
            final LibraryOrderEntry libraryOrderEntry = model.findLibraryOrderEntry(eclipseDepsLibrary);
            getApplication().runWriteAction(new Runnable() {
                public void run() {
                    if (libraryOrderEntry != null){
                        model.removeOrderEntry(libraryOrderEntry);
                        model.commit();
                    }

                    libraryTable.removeLibrary(eclipseDepsLibrary);
                }
            });
        }
    }

    private VirtualFileManager getVirtualFileManager() {
        return VirtualFileManager.getInstance();
    }

    private LibraryTable getLibraryTable(Module currentModule) {
        return LibraryTablesRegistrar.getInstance().getLibraryTable(currentModule.getProject());
    }

    private ModuleRootManager getModuleRootManager(Module currentModule) {
        return ModuleRootManager.getInstance(currentModule);
    }

    void createDependencyLibraryAndStartTracingChanges(Module currentModule, VirtualFile classpathVirtualFile) {

        if (currentModule == null){
            Messages.showWarningDialog("Please open any project.", "No open projects" );
            return;
        }


        String libraryName = Messages.showInputDialog("a","b", Messages.getQuestionIcon(), computeEclipseDependenciesLibraryProposedName(currentModule), null);
        
        registerClasspathFileModificationListener(classpathVirtualFile, currentModule, libraryName);

        Library.ModifiableModel model = refreshEclipseDependencies(classpathVirtualFile);
        displayInformationDialog(model);
    }

    private void registerClasspathFileModificationListener(VirtualFile classpathVirtualFile, Module currentModule, String libraryName) {
        ClasspathFileModificationListener listener;
        if (! isFileRegistered(classpathVirtualFile)){
            listener = new ClasspathFileModificationListener(classpathVirtualFile);
            getVirtualFileManager().addVirtualFileListener(listener);
            activeListeners.put(classpathVirtualFile.getUrl(), new Registration(listener, currentModule.getName(), libraryName));
        }
    }

    private void displayInformationDialog(Library.ModifiableModel model) {
        String res="";
        for (String url : model.getUrls(OrderRootType.CLASSES)){
            res+= url+"\n";
        }
        Messages.showMessageDialog("Added the following libs:\n"+res, "Eclipse Dependencies Update", Messages.getInformationIcon());
    }

    private Library.ModifiableModel refreshEclipseDependencies(VirtualFile classpathVirtualFile) {
        String classpathFilePath = classpathVirtualFile.getPath();
        final String classpathFileDir = classpathVirtualFile.getParent().getPath();
        final List<String> libs = EclipseTools.parseEclipseClassPath(classpathFilePath);


        String libraryName = activeListeners.get( classpathVirtualFile.getUrl()).libraryName;
        final Library newLibrary = getOrCreateEclipseDependenciesLibrary(module, libraryName);
        final Library.ModifiableModel libraryModel = newLibrary.getModifiableModel();

        getApplication().runWriteAction(new Runnable() {
            public void run() {
                clearLibrary(libraryModel);
                addJarsToLibraryModel(libs, libraryModel, classpathFileDir);
                libraryModel.commit();

                ModifiableRootModel moduleModel = getModuleRootManager(module).getModifiableModel();
                if (moduleModel.findLibraryOrderEntry(newLibrary) == null){
                    moduleModel.addLibraryEntry(newLibrary);
                    moduleModel.commit();
                }

            }
        });

        return libraryModel;
    }

    private void addJarsToLibraryModel(List<String> libs, Library.ModifiableModel model, String baseDirectory) {
        for(String lib:libs){
            if (lib.matches("[a-zA-Z]:[/\\\\].+") || lib.startsWith("/")) {
                model.addRoot("jar://"+lib+"!/", OrderRootType.CLASSES);
            } else {
                model.addRoot("jar://"+baseDirectory+"/"+lib+"!/", OrderRootType.CLASSES);
            }

        }
    }

    private void clearLibrary(Library.ModifiableModel model) {
        for(String url:model.getUrls(OrderRootType.CLASSES)){
            model.removeRoot(url, OrderRootType.CLASSES);
        }
    }

    private Library getOrCreateEclipseDependenciesLibrary(Module currentModule, final String libraryName) {
        final LibraryTable libraryTable = getLibraryTable(currentModule);
        final Library[] eclipseDepsLibrary = new Library[]{libraryTable.getLibraryByName(libraryName)};
        if (eclipseDepsLibrary[0] == null){
            getApplication().runWriteAction(new Runnable() {
                public void run() {
                    eclipseDepsLibrary[0] = libraryTable.createLibrary(libraryName);
                }
            });
        }

        return eclipseDepsLibrary[0];
    }

    private String computeEclipseDependenciesLibraryProposedName(Module currentModule) {
        return currentModule.getName() + ECLIPSE_DEPENDENCIES_SUFFIX;
    }

    public void readExternal(Element element) throws InvalidDataException {
        for(Object o : element.getChildren()){
            Element e = (Element) o;
            loadedListeners.put( e.getAttributeValue("tracedFile"), new Registration(null, e.getAttributeValue("module"), e.getAttributeValue("library")));
        }
    }

    public void writeExternal(Element element) throws WriteExternalException {
        for(String url: activeListeners.keySet()) {
            Element element1 = new Element("eclipse-dependency");
            element1.setAttribute("tracedFile", url);
            element1.setAttribute("module", activeListeners.get(url).moduleName);
            element1.setAttribute("library", activeListeners.get(url).libraryName);
            element.addContent(element1);
        }
    }


    private class ClasspathFileModificationListener extends VirtualFileAdapter {
        private final VirtualFile classpathVirtualFile;


        public ClasspathFileModificationListener(VirtualFile classpathVirtualFile) {
            this.classpathVirtualFile = classpathVirtualFile;
        }

        public void contentsChanged(VirtualFileEvent event) {
            if (classpathVirtualFile.getPath().equals(event.getFile().getPath())){
                Library.ModifiableModel model = refreshEclipseDependencies(classpathVirtualFile);
                displayInformationDialog(model);
            }
        }
    }


}
