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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;

public class StripesGenerateGroup extends DefaultActionGroup {
// --------------------------- CONSTRUCTORS ---------------------------

	public StripesGenerateGroup() {
		super(StripesConstants.STRIPES, true);
		getTemplatePresentation().setDescription(StripesConstants.STRIPES);
		getTemplatePresentation().setIcon(StripesConstants.STRIPES_ICON);

		add(new GenerateResolutionMethodAction());
		add(new GenerateSpringBeanAction());
		add(new GenerateValidationMethodAction());
		add(new GenerateBeforeMethodAction());
		add(new GenerateAfterMethodAction());
	}

	@Override
	public void update(AnActionEvent e) {
		Editor editor = LangDataKeys.EDITOR.getData(e.getDataContext());
		PsiFile file = LangDataKeys.PSI_FILE.getData(e.getDataContext());
		Module module = LangDataKeys.MODULE.getData(e.getDataContext());
		PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());

		if (StripesUtil.getStripesFacet(module) != null
			&& element instanceof PsiWhiteSpace
			&& element.getParent() instanceof PsiClass
			&& StripesUtil.isSubclass(StripesConstants.ACTION_BEAN, (PsiClass) element.getParent())) {
			e.getPresentation().setVisible(true);
			e.getPresentation().setEnabled(true);
			return;
		}


		e.getPresentation().setVisible(false);
	}
}