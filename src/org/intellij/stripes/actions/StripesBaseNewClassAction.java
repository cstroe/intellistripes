/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
