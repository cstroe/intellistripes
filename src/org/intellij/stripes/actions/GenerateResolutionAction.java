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

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;

public class GenerateResolutionAction extends BaseGenerateAction {
    public GenerateResolutionAction() {
        super(new GenerateResolutionActionHandler());
        getTemplatePresentation().setIcon(StripesConstants.STRIPES_ICON);
    }

    protected boolean isValidForFile(Project project, Editor editor, PsiFile file) {
        return super.isValidForFile(project, editor, file) && StripesUtil.isSubclass(StripesConstants.ACTION_BEAN, getTargetClass(editor, file));
    }
}
