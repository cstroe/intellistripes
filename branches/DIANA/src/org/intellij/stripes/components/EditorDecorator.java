/*
 * Copyright 2000-2007 JetBrains s.r.o.
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

package org.intellij.stripes.components;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.util.containers.ContainerUtil;
import org.apache.commons.lang.StringUtils;
import org.intellij.stripes.facet.StripesFacet;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditorDecorator implements ProjectComponent, FileEditorManagerListener {
	private Project project;
	private FileEditorManager editorManager;

	public EditorDecorator(Project project) {
		this.project = project;
		editorManager = FileEditorManager.getInstance(project);
	}

	public void initComponent() {
		getEditorManager().addFileEditorManagerListener(this);
	}

	public void disposeComponent() {
	}

	@NotNull
	public String getComponentName() {
		return "EditorDecorator";
	}

	public void projectOpened() {
		// called when myProject is opened
	}

	public void projectClosed() {
		// called when myProject is being closed
	}

	public void fileOpened(FileEditorManager source, VirtualFile file) {
		PsiFile psiFile = PsiManager.getInstance(source.getProject()).findFile(file);
		if (psiFile instanceof PsiJavaFileImpl) {
			StripesFacet stripesFacet = StripesUtil.getStripesFacet(file, source.getProject());
			if (stripesFacet != null) {
				final String actionResolverPackages = stripesFacet.getConfiguration().getActionResolverPackages();
				if (StringUtils.isNotBlank(actionResolverPackages)) {
					PsiClass actionBean = ContainerUtil.find(((PsiJavaFile) psiFile).getClasses(), new Condition<PsiClass>() {
						public boolean value(PsiClass psiClass) {
							return StripesUtil.isSubclass(psiClass.getProject(), StripesConstants.ACTION_BEAN, psiClass)
								&& psiClass.getQualifiedName().startsWith(actionResolverPackages);
						}
					});
					if (actionBean != null) decorateActionBean(file, psiFile, actionBean);
				}
			}
		}
	}

	private void decoratePanel(VirtualFile file, JPanel panel) {
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
		getEditorManager().addTopComponent(getEditorManager().getEditors(file)[0], panel);
	}

	private void decorateActionBean(VirtualFile virtualFile, PsiFile psiFile, PsiClass actionBean) {
		JPanel panel = new JPanel();
		panel.add(new OpenDomainClassAction(StripesUtil.message("editor.top.views")));
		decoratePanel(virtualFile, panel);
	}

	public void fileClosed(FileEditorManager source, VirtualFile file) {
	}

	public void selectionChanged(FileEditorManagerEvent event) {
	}

	public FileEditorManager getEditorManager() {
		return editorManager;
	}

	private abstract class OpenFileAction extends JLabel {

		public OpenFileAction(String actionName) {
			super(actionName);

			setForeground(Color.BLACK);
			setOpaque(true);
			setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));

			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					actionPerformed();
				}

				public void mouseEntered(MouseEvent e) {
					setBackground(getBackground().darker());
				}

				public void mouseExited(MouseEvent e) {
					setBackground(getParent().getBackground());
				}
			});

			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		abstract void actionPerformed();
	}

	class OpenDomainClassAction extends OpenFileAction {

		public OpenDomainClassAction(String name) {
			super(name);
			setIcon(StripesConstants.STRIPES_JSP_ICON);
		}

		public void actionPerformed() {
//			VirtualFile domainClass = VfsUtil.findRelativeFile("/grails-app/domain/" + name + ".groovy", moduleFileDir);
			//Find domain class with qualified names
//			final Collection<VirtualFile> files = findDomainFilesWithSameName();
//			if (domainClass != null && domainClass.isValid() && !isForViews()) {
//				myEditorDecorator.getEditorManager().openFile(domainClass, true);
//			} else {
//				Collection<AnAction> anActionList = myEditorDecorator.mapFilesToActions(files);
//				DefaultActionGroup group = new DefaultActionGroup();
//				Navigate to domain classes
//				for (AnAction action : anActionList) {
//					group.add(action);
//				}
//				group.addSeparator();
			//Generate domain class
//				AnAction anAction = ActionManager.getInstance().getAction("Grails.GenerateDomainClass");
//				anAction.getTemplatePresentation().setText(GrailsBundle.message("action.Grails.GenerateDomainClass.text"));
//				group.add(anAction);
//				if (group.getChildrenCount() != 0) {
//					ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("", group);
//					actionPopupMenu.getComponent().show(this, 0, getHeight());
//				}
//			}
		}
	}
}
