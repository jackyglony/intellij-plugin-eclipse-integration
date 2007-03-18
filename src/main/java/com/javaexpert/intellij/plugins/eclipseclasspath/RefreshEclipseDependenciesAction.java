package com.javaexpert.intellij.plugins.eclipseclasspath;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.javaexpert.intellij.plugins.eclipseclasspath.eclipse.EclipseClasspathFile;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.DependencySynchronizer;


public class RefreshEclipseDependenciesAction extends AnAction {

    public void update(AnActionEvent e) {
        e.getPresentation().setVisible(false);

        VirtualFile file = getVirtualFile(e);
        if (file == null)
            return;

        if (file.getName().equalsIgnoreCase(".classpath")) {
            e.getPresentation().setVisible(true);

            DependencySynchronizer synchronizer = getDependencySynchronizer(e);
            assert synchronizer != null;

            if (synchronizer.isFileTraced(file.getPath())) {
                e.getPresentation().setText("Remove ECLIPSE dependency");
            } else {
                e.getPresentation().setText("Add ECLIPSE dependency");
            }
        }
    }

    public void actionPerformed(AnActionEvent e) {
        DependencySynchronizer synchronizer = getDependencySynchronizer(e);
        VirtualFile file = getVirtualFile(e);

        assert file != null;

        if (synchronizer.isFileTraced(file.getPath())) {
            synchronizer.stopTracingChanges(file.getPath());
        } else {
            synchronizer.traceChanges(new EclipseClasspathFile(file.getPath()));
        }
    }

    protected VirtualFile getVirtualFile(AnActionEvent e) {
        return (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
    }

    protected Module getModule(AnActionEvent e) {
        return (Module) e.getDataContext().getData(DataConstants.MODULE);
    }

    protected DependencySynchronizer getDependencySynchronizer(AnActionEvent e) {
        return (DependencySynchronizer) getModule(e).getComponent("DependencySynchronizer");
    }
}
