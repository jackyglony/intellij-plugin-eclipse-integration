package com.javaexpert.intellij.plugins.eclipseclasspath;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

public class RefreshEclipseDependenciesAction extends AnAction {


    public void update(AnActionEvent e) {
        e.getPresentation().setVisible(false);

        VirtualFile file = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
        if (file == null)
            return;
        
        if(file.getName().equalsIgnoreCase(".classpath")){
            e.getPresentation().setVisible(true);

            DependecySynchronizer dependecySynchronizer = getModule(e).getComponent(DependecySynchronizer.class);
            assert dependecySynchronizer != null;

            if (dependecySynchronizer.isFileRegistered(file)){
                e.getPresentation().setText("Remove ECLIPSE dependency");
            }else{
                e.getPresentation().setText("Add ECLIPSE dependency");
            }

        }
    }

    private Module getModule(AnActionEvent e) {
        return (Module) e.getDataContext().getData(DataConstants.MODULE);
    }

//    private Project getProject(AnActionEvent e) {
//        return ((Project) e.getDataContext().getData(DataConstants.PROJECT));
//    }


    public void actionPerformed(AnActionEvent e) {

        DependecySynchronizer dependecySynchronizer = getModule(e).getComponent(DependecySynchronizer.class);
        Module currentModule = (Module) e.getDataContext().getData(DataConstants.MODULE);
        VirtualFile file = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);

        assert file != null;

        if (dependecySynchronizer.isFileRegistered(file)) {
            dependecySynchronizer.removeEclipseDependencyLibraryAndStopTracingChanges(currentModule, file);
        } else {
            dependecySynchronizer.createDependencyLibraryAndStartTracingChanges(currentModule, file);
        }

    }


}
