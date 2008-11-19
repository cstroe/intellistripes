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
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

public class GenerateResolutionMethodAction extends StripesBaseGenerateAction {
    public GenerateResolutionMethodAction() {
        super(new GenerateResolutionMethodHandler());
    }

	private static class GenerateResolutionMethodHandler implements CodeInsightActionHandler {
		public void invoke(Project project, Editor editor, PsiFile file) {

			Template t = TemplateManager.getInstance(project).createTemplate("", "", "public net.sourceforge.stripes.action.Resolution $RESOLUTION_NAME$() {\n return new $TYPE$($END$);\n}");
			t.setToReformat(true);
			t.setToShortenLongNames(true);
			t.setToIndent(true);
			t.addVariable("RESOLUTION_NAME", "", "\"action\"", true);
			t.addVariable("TYPE", "completeSmart()", "", true);
//			t.addVariable("TYPE", "descendantClassEnum(\"net.sourceforge.stripes.action.Resolution\")", "", true);
			TemplateManager.getInstance(project).startTemplate(editor, t);

		}
		                                                                               
		public boolean startInWriteAction() {
			return true;
		}
	}
}
