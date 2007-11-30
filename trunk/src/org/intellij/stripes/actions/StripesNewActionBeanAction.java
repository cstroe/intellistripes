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
import com.intellij.ide.actions.CreateElementActionBase;
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
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 10/11/2007 Time: 12:04:05 AM
 */
public class StripesNewActionBeanAction extends CreateElementActionBase
{
    public StripesNewActionBeanAction()
    {
        super("Stripes ActionBean", "Create a New Stripes Action Bean", StripesConstants.ACTION_BEAN_ICON);
    }

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(Project project, PsiDirectory directory)
    {
        MyInputValidator validator = new MyInputValidator(project, directory);
        Messages.showInputDialog(project, "Enter Name for New Stripes ActionBean", "New Stripes Action Bean", Messages.getQuestionIcon(), "", validator);
        return validator.getCreatedElements();
    }

    protected void checkBeforeCreate(String newName, PsiDirectory directory) throws IncorrectOperationException
    {
        directory.checkCreateClass(newName);
    }

    @Override
    public void update(AnActionEvent anActionEvent)
    {
        Presentation presentation = anActionEvent.getPresentation();

        if (!StripesUtil.isStripesFacetConfigured((Module) anActionEvent.getDataContext().getData(DataConstants.MODULE)))
        {
            presentation.setEnabled(false);
            presentation.setVisible(false);
        }
    }

    @NotNull
    protected PsiElement[] create(String s, PsiDirectory directory) throws Exception
    {
        FileTemplate fileTemplate = FileTemplateManager.getInstance().getJ2eeTemplate(StripesConstants.ACTION_BEAN_TEMPLATE);
        Properties properties = new Properties(FileTemplateManager.getInstance().getDefaultProperties());
        properties.setProperty("NAME", s);
        properties.setProperty("PACKAGE_NAME", directory.getText());
        PsiElement template = FileTemplateUtil.createFromTemplate(fileTemplate, s + ".java", properties, directory);
        return new PsiElement[]{template};
    }

    protected String getErrorTitle()
    {
        return CommonBundle.getErrorTitle();
    }

    protected String getCommandName()
    {
        return "Create Stripes ActionBean";
    }

    protected String getActionName(PsiDirectory directory, String s)
    {
        return "Creating Stripes ActionBean";
    }

    public static boolean isUnderSourceRoots(final AnActionEvent e)
    {
        final DataContext context = e.getDataContext();
        Module module = (Module) context.getData(DataConstants.MODULE);
        if (module == null)
        {
            return false;
        }
        final IdeView view = (IdeView) context.getData(DataConstants.IDE_VIEW);
        final Project project = (Project) context.getData(DataConstants.PROJECT);
        if (view != null && project != null)
        {
            ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
            PsiDirectory[] dirs = view.getDirectories();
            for (PsiDirectory dir : dirs)
            {
                if (projectFileIndex.isInSourceContent(dir.getVirtualFile()) && dir.getPackage() != null)
                {
                    return true;
                }
            }
        }
        return false;
    }


}
