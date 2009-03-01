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

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.intellij.stripes.util.StripesUtil;

public class GenerateValidationMethodAction extends StripesBaseGenerateAction {

	public GenerateValidationMethodAction() {
		super(new GenerateValidationMethodHandler());
	}

	@Override
	public void update(AnActionEvent event) {
		super.update(event);
		event.getPresentation().setText(StripesUtil.message("action.generate.validationMethod"));
	}

	private static class GenerateValidationMethodHandler implements CodeInsightActionHandler {
		public void invoke(Project project, Editor editor, PsiFile file) {

			Template t = TemplateManager.getInstance(project).createTemplate("", "");
			t.setToReformat(true);
			t.setToShortenLongNames(true);
			t.setToIndent(true);

			t.addTextSegment("@net.sourceforge.stripes.validation.ValidationMethod public void validate(net.sourceforge.stripes.validation.ValidationErrors errors) {\n\t");
			t.addEndVariable();
			t.addSelectionStartVariable();
			t.addTextSegment("//	put validation actions here");
			t.addSelectionEndVariable();
			t.addTextSegment("\n}");

			TemplateManager.getInstance(project).startTemplate(editor, t);
		}

		public boolean startInWriteAction() {
			return true;
		}
	}
}