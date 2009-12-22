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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;

public class StripesNewGroup extends DefaultActionGroup {
// --------------------------- CONSTRUCTORS ---------------------------


	public StripesNewGroup() {
		super(StripesConstants.STRIPES, true);
		getTemplatePresentation().setDescription(StripesConstants.STRIPES);
		getTemplatePresentation().setIcon(StripesConstants.STRIPES_ICON);

		add(new StripesNewActionBeanAction());
		add(new StripesNewActionBeanContextAction());
		add(new StripesNewActionBeanContextFactoryAction());
		add(new StripesNewInterceptorAction());
	}


	@Override
	public void update(AnActionEvent e) {
		Module module = LangDataKeys.MODULE.getData(e.getDataContext());
		VirtualFile vf = LangDataKeys.VIRTUAL_FILE.getData(e.getDataContext());

		boolean visible = StripesUtil.getStripesFacet(module) != null;
		boolean enabled = visible && ModuleRootManager.getInstance(module).getFileIndex().isInSourceContent(vf);

		e.getPresentation().setVisible(enabled);
		e.getPresentation().setEnabled(visible);
	}
}
