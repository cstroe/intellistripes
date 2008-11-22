package org.intellij.stripes.actions;

import com.intellij.ide.actions.CreateClassAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

public abstract class StripesBaseNewClassAction extends CreateClassAction {
    @Override
    protected String getActionName(PsiDirectory directory, String s) {
        return getCommandName();
    }

    @Override
    protected String getCommandName() {
        return "Create Stripes " + getClassName();
    }

    @Override
    protected PsiClass doCreate(PsiDirectory dir, String className) throws IncorrectOperationException {
        try {
            return JavaDirectoryService.getInstance().createClass(dir, className, getTemplateName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(Project project, PsiDirectory directory) {
        MyInputValidator validator = new MyInputValidator(project, directory);
        Messages.showInputDialog(project, MessageFormat.format("Enter {0} class name", getClassName()),
                "New " + getClassName(), Messages.getQuestionIcon(), "My" + getClassName(), validator
        );
        return validator.getCreatedElements();
    }

    protected abstract String getClassName();

    protected abstract String getTemplateName();
}
