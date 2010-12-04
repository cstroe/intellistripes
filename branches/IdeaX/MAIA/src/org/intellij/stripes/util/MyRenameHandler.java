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

package org.intellij.stripes.util;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.beanProperties.BeanProperty;
import com.intellij.refactoring.rename.BeanPropertyRenameHandler;
import org.jetbrains.annotations.NotNull;

public class MyRenameHandler extends BeanPropertyRenameHandler {

	protected BeanProperty getProperty(DataContext dataContext) {
		return null;
//		return BeanProperty.createBeanProperty((PsiMethod) LangDataKeys.PSI_ELEMENT.getData(dataContext));
	}

	@Override
	public boolean isAvailableOnDataContext(DataContext dataContext) {
		return super.isAvailableOnDataContext(dataContext);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public boolean isRenaming(DataContext dataContext) {
		return super.isRenaming(dataContext);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
		super.invoke(project, editor, psiFile, dataContext);
	}

	@Override
	public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {
		super.invoke(project, psiElements, dataContext);
	}
}
