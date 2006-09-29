package com.javaexpert.intellij.plugins.eclipseclasspath;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer.DependecySynchronizer;

public class RefreshEclipseDependenciesAction extends AnAction {

    public void update(AnActionEvent e) {
        e.getPresentation().setVisible(false);

        VirtualFile file = getVirtualFile(e);
        if (file == null)
            return;

        if (file.getName().equalsIgnoreCase(".classpath")) {
            e.getPresentation().setVisible(true);

            DependecySynchronizer synchronizer = getDependencySynchronizer(e);
            assert synchronizer != null;

            if (synchronizer.isFileTraced(file)) {
                e.getPresentation().setText("Remove ECLIPSE dependency");
            } else {
                e.getPresentation().setText("Add ECLIPSE dependency");
            }
        }
    }

    public void actionPerformed(AnActionEvent e) {
        DependecySynchronizer synchronizer = getDependencySynchronizer(e);
        VirtualFile file = getVirtualFile(e);

        assert file != null;

        if (synchronizer.isFileTraced(file)) {
            synchronizer.stopTracingChanges(getModule(e), file);
        } else {
            synchronizer.traceChanges(getModule(e), file);
        }
    }

    private VirtualFile getVirtualFile(AnActionEvent e) {
        return (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
    }

    private Module getModule(AnActionEvent e) {
        return (Module) e.getDataContext().getData(DataConstants.MODULE);
    }

    private DependecySynchronizer getDependencySynchronizer(AnActionEvent e) {
        return getModule(e).getComponent(DependecySynchronizer.class);
    }
}
