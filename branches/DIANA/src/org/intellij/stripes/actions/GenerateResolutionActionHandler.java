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

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ex.MessagesEx;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;

public class GenerateResolutionActionHandler implements CodeInsightActionHandler {
    public void invoke(Project project, Editor editor, PsiFile file) {
        String methodName = MessagesEx.showInputDialog("Enter method name", "Create new Resolution", StripesConstants.RESOLUTION_ICON);
        if (StringUtil.isEmptyOrSpaces(methodName)) return;

        PsiClass currentBeanClass = PsiTreeUtil.getChildOfType(file, PsiClass.class);
        if (null == currentBeanClass) return;


		try {
            PsiMethod m = (PsiMethod) currentBeanClass.add(JavaPsiFacade.getInstance(currentBeanClass.getProject()).getElementFactory()
                    .createMethodFromText(StripesUtil.message("generate.resolution", methodName), null));

			JavaCodeStyleManager.getInstance(currentBeanClass.getProject())
				.shortenClassReferences(CodeStyleManager.getInstance(currentBeanClass.getProject()).reformat(m));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean startInWriteAction() {
        return true;
    }
}
