/*
 * Copyright 2000-2007 JetBrains s.r.o.
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

import com.intellij.CommonBundle;
import com.intellij.ide.IdeView;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.ide.actions.CreateClassAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public class StripesNewActionBeanAction extends CreateClassAction {
    protected String getActionName(PsiDirectory directory, String s) {
        return "Creating Stripes ActionBean";
    }

    protected String getCommandName() {
        return "Create Stripes ActionBean";
    }

	@Override
	protected PsiClass doCreate(PsiDirectory dir, String className) throws IncorrectOperationException {
		try {
			return JavaDirectoryService.getInstance().createClass(dir, className, StripesConstants.ACTION_BEAN_TEMPLATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@NotNull
	@Override
	protected PsiElement[] invokeDialog(Project project, PsiDirectory directory) {
		MyInputValidator validator = new MyInputValidator(project, directory);
		Messages.showInputDialog(project, "Enter ActionBean class name",
								 "New ActionBean", Messages.getQuestionIcon(), "ActionBean", validator);
		return validator.getCreatedElements();
	}
}