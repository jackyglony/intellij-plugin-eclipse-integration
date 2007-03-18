package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.javaexpert.intellij.plugins.support.AbstractModuleComponent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * User: piotrga
 * Date: 2006-12-03
 * Time: 10:08:29
 */
public class UI extends AbstractModuleComponent {
    public void displayInformationDialog(String[] urls, Set<String> vars) {
        String res = "";
        for (String url : urls) {
            res += url + "\n";
        }
        Messages.showMessageDialog(
                "Added the following libs:\n" + res, "Eclipse Dependencies Update", Messages.getInformationIcon());
        if (!vars.isEmpty()) {
            Messages.showDialog("The following PATH VARIABLES have been used. Make sure you define them in your workspace and reload the project.\n" + vars + "\n Do you want to reload project now?"
                    , "Reload project?", new String[]{"Reload now!", "Reload later manually"}, 0
                    , Messages.getQuestionIcon());
        }
    }

    public void displayNoProjectSelectedWarnning() {
        Messages.showWarningDialog("Please open any project.", "No open projects");
    }

    public String getLibraryNameFromUser(Project project, String defaultLibraryName) {
        return Messages.showInputDialog(project, "Please enter library name.", "Creating library for Eclipse dependencies", Messages.getQuestionIcon(), defaultLibraryName, null);
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "UI";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
