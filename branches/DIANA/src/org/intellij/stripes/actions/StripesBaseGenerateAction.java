/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.stripes.actions;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;

public abstract class StripesBaseGenerateAction extends BaseGenerateAction {

	public StripesBaseGenerateAction(CodeInsightActionHandler handler) {
		super(handler);
		getTemplatePresentation().setIcon(StripesConstants.STRIPES_ICON);
	}

	@Override
	protected boolean isValidForFile(Project project, Editor editor, PsiFile file) {
		if (super.isValidForFile(project, editor, file)) {
			PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
			return element instanceof PsiWhiteSpace && element.getParent() instanceof PsiClass;
		}
		return false;
	}

	@Override
	protected boolean isValidForClass(PsiClass targetClass) {
		return super.isValidForClass(targetClass) && StripesUtil.isSubclass(StripesConstants.ACTION_BEAN, targetClass);
	}
}
