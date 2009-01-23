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

package org.intellij.stripes.reference;

import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.jsp.JspImplicitVariableImpl;
import com.intellij.psi.impl.source.jsp.el.impl.ELElementProcessor;
import com.intellij.psi.impl.source.jsp.el.impl.ElVariablesProvider;
import com.intellij.psi.impl.source.jsp.el.impl.MethodSignatureFilter;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.jsp.el.ELExpressionHolder;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesTagFilter;
import org.intellij.stripes.util.StripesUtil;
import org.intellij.stripes.util.XmlTagContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StripesELVarProvider extends ElVariablesProvider {
    private static String ACTION_BEAN = "actionBean";
    private static String USE_ACTION_BEAN = "useActionBean";

    private static PsiElementFilter ACTION_BEAN_PROVIDER_FILTER = new StripesTagFilter() {
        protected boolean isDetailsAccepted(XmlTag tag) {
            return StripesConstants.USE_ACTION_BEAN_TAG.equals(tag.getLocalName())
                    || StripesConstants.FORM_TAG.equals(tag.getLocalName());
        }
    };
    public static PsiElementFilter BEANCLASS_ATTR_FILTER = new StripesTagFilter() {
        protected boolean isDetailsAccepted(XmlTag tag) {
            return tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR) != null;
        }
    };

    public boolean processImplicitVariables(@NotNull final PsiElement psiElement, @NotNull final ELExpressionHolder elExpressionHolder, @NotNull final ELElementProcessor elElementProcessor) {
        try {
            if (!(elExpressionHolder.getContainingFile() instanceof JspFile)) {
                return true;
            }

            final PsiFile jspFile = elExpressionHolder.getContainingFile();
            if (!StripesUtil.isStripesPage(elExpressionHolder.getContainingFile())) return true;

            Map<String, String> actionBeans = StripesUtil.collectTags(((JspFile) jspFile).getDocument().getRootTag(),
                    ACTION_BEAN_PROVIDER_FILTER, BEANCLASS_ATTR_FILTER,
                    new XmlTagContainer<Map<String, String>>(new HashMap<String, String>(8)) {
                        public void add(XmlTag tag) {
                            String actionBeanClassName = tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR);
                            if (StripesConstants.USE_ACTION_BEAN_TAG.equals(tag.getLocalName())) {
                                if (tag.getAttributeValue(StripesConstants.ID_ATTR) == null && tag.getAttributeValue(StripesConstants.VAR_ATTR) == null) {
                                    container.put(USE_ACTION_BEAN, actionBeanClassName);
                                } else if (tag.getAttributeValue(StripesConstants.ID_ATTR) != null) {
                                    processVariable(psiElement.getProject(), elElementProcessor, (JspFile) jspFile, tag.getAttributeValue(StripesConstants.ID_ATTR), actionBeanClassName);
                                } else if (tag.getAttributeValue(StripesConstants.VAR_ATTR) != null) {
                                    processVariable(psiElement.getProject(), elElementProcessor, (JspFile) jspFile, tag.getAttributeValue(StripesConstants.VAR_ATTR), actionBeanClassName);
                                }
                            } else if (StripesConstants.FORM_TAG.equals(tag.getLocalName())) {
                                container.put("__" + actionBeanClassName, actionBeanClassName);
                            }
                        }
                    }).getContainer();

            String clsName = actionBeans.remove(USE_ACTION_BEAN);
            if (null != clsName) {
                actionBeans.clear();
                actionBeans.put(ACTION_BEAN, clsName);
            }

            for (String actionBeanName : actionBeans.keySet()) {
                processVariable(psiElement.getProject(), elElementProcessor, (JspFile) jspFile, ACTION_BEAN, actionBeans.get(actionBeanName));
            }
        } catch (ProcessCanceledException e) {
            //Do nothig, this exception is very common and can be throw for intellij
            //Logger don't be reported or just raise an ugly error
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private static void processVariable(final Project project, final ELElementProcessor elElementProcessor, final JspFile jspFile, final String actionBeanName, final String actionBeanClassName) {
        final PsiClass actionBeanClass = StripesUtil.findPsiClassByName(actionBeanClassName, project);
        if (actionBeanClass == null) return;

        elElementProcessor.processVariable(
                new JspImplicitVariableImpl(actionBeanClass,
                        actionBeanName, JavaPsiFacade.getInstance(project).getElementFactory().createType(actionBeanClass),
                        actionBeanClass, JspImplicitVariableImpl.NESTED_RANGE) {

                    public PsiElement getDeclaration() {
                        return null;
                    }

                    @NotNull
                    public SearchScope getUseScope() {
                        return GlobalSearchScope.fileScope(jspFile);
                    }
                }
        );
    }

    public MethodSignatureFilter getMethodSignatureFilter(@NotNull PsiElement psiElement, @NotNull ELExpressionHolder elExpressionHolder, @Nullable PsiElement psiElement1) {
        return null;
    }
}
