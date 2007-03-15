package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.util.Set;

/**
 * User: piotrga
 * Date: 2006-12-03
 * Time: 10:08:29
 */
class UI {
    public void displayInformationDialog(String[] urls, Set<String> vars) {
        String res = "";
        for (String url : urls) {
            res += url + "\n";
        }
        Messages.showMessageDialog(
                "Added the following libs:\n" + res, "Eclipse Dependencies Update", Messages.getInformationIcon());
        if (!vars.isEmpty()) {
            Messages.showMessageDialog("The following PATH VARIABLES have been used. Make sure you define them in your workspace and reload the project.\n" + vars
                    , "Path variables."
                    , Messages.getInformationIcon());
        }
    }

    public void displayNoProjectSelectedWarnning() {
        Messages.showWarningDialog("Please open any project.", "No open projects");
    }

    public String getLibraryNameFromUser(Project project, String defaultLibraryName) {
        return Messages.showInputDialog(project, "Please enter library name.", "Creating library for Eclipse dependencies", Messages.getQuestionIcon(), defaultLibraryName, null);
    }
}
